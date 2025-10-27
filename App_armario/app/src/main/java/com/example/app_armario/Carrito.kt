package com.example.app_armario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app_armario.Models.CarritoItem
import com.example.app_armario.Repositories.CarritoRepository
import com.example.app_armario.Repositories.ProductoRepository
import com.example.app_armario.Repositories.VentaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Carrito(navController: NavHostController) {
    val context = LocalContext.current
    val repoCarrito = remember { CarritoRepository(context) }
    val repoProducto = remember { ProductoRepository(context) }
    val repoVenta = remember { VentaRepository(context) }

    var items by remember { mutableStateOf(repoCarrito.obtenerCarrito()) }
    fun refrescar() { items = repoCarrito.obtenerCarrito() }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showConfirm by remember { mutableStateOf(false) }
    var processing by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Carrito de compras", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    if (items.isNotEmpty()) {
                        TextButton(onClick = { repoCarrito.limpiar(); refrescar() }) {
                            Text("Vaciar", color = Color(0xFFFF7070))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu carrito está vacío 🛒", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    items(items, key = { it.idProducto }) { item ->
                        ItemCarrito(
                            item = item,
                            onInc = { repoCarrito.incrementar(item.idProducto); refrescar() },
                            onDec = { repoCarrito.decrementar(item.idProducto); refrescar() },
                            onRemove = { repoCarrito.eliminar(item.idProducto); refrescar() }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                val total = remember(items) { items.sumOf { it.precio * it.cantidad } }
                val totalItems = remember(items) { items.sumOf { it.cantidad } }

                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Productos: $totalItems", color = Color.LightGray)
                        Text("Total:", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(
                            "$${formatoClp(total)}",
                            color = Color(0xFFB32DD4),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { showConfirm = true },
                        enabled = items.isNotEmpty() && !processing,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (processing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Procesando...", color = Color.White)
                        } else {
                            Text("Finalizar compra", color = Color.White)
                        }
                    }
                }
            }
        }

        // === Confirmación ===
        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { if (!processing) showConfirm = false },
                confirmButton = {
                    Row {
                        TextButton(enabled = !processing, onClick = { showConfirm = false }) {
                            Text("Cancelar", color = Color.White)
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            enabled = !processing,
                            onClick = {
                                processing = true
                                scope.launch {
                                    delay(1000)

                                    val pedidoId = "PED-${UUID.randomUUID().toString().take(8).uppercase()}"
                                    val totalCompra = items.sumOf { it.precio * it.cantidad }
                                    val totalProductos = items.sumOf { it.cantidad }
                                    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

                                    // Guardar venta en DataStore
                                    repoVenta.registrarVenta(
                                        id = pedidoId,
                                        fecha = fecha,
                                        total = totalCompra,
                                        productos = items
                                    )

                                    // Descontar stock
                                    items.forEach { repoProducto.descontarStock(it.idProducto, it.cantidad) }

                                    // Limpiar carrito
                                    repoCarrito.limpiar()
                                    refrescar()

                                    snackbarHostState.showSnackbar(
                                        "✅ Su pedido $pedidoId por $${formatoClp(totalCompra)} (${totalProductos} productos).\nGracias por su compra 💜",
                                        withDismissAction = true
                                    )

                                    processing = false
                                    showConfirm = false

                                    // Ir al Home
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4))
                        ) { Text("Confirmar", color = Color.White) }
                    }
                },
                title = { Text("Confirmar compra", color = Color.White) },
                text = {
                    val total = items.sumOf { it.precio * it.cantidad }
                    Text(
                        "Se procesará el pago por $${formatoClp(total)}.\n¿Deseas continuar?",
                        color = Color.White
                    )
                },
                containerColor = Color(0xFF121212)
            )
        }
    }
}

/* ========================== ITEM CARRITO ========================== */

@Composable
private fun ItemCarrito(
    item: CarritoItem,
    onInc: () -> Unit,
    onDec: () -> Unit,
    onRemove: () -> Unit
) {
    val subtotal = remember(item) { item.precio * item.cantidad }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = item.imagenUrl ?: "file:///android_asset/img/default.png",
                contentDescription = item.nombre,
                modifier = Modifier.size(70.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(item.nombre, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text("$${formatoClp(item.precio)}", color = Color(0xFFB32DD4))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onDec,
                        border = BorderStroke(1.dp, Color(0xFFB32DD4))
                    ) { Text("−", color = Color.White) }

                    Text(
                        "${item.cantidad}",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    OutlinedButton(
                        onClick = onInc,
                        border = BorderStroke(1.dp, Color(0xFFB32DD4))
                    ) { Text("+", color = Color.White) }
                }

                Spacer(Modifier.height(6.dp))
                Text(
                    "Subtotal: $${formatoClp(subtotal)}",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}

/* ========================== UTIL ========================== */

// OJO: pública (sin 'private') para usarla también desde otros archivos.
fun formatoClp(n: Long): String = "%,d".format(n).replace(',', '.')
