package com.example.app_armario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.app_armario.Models.Venta
import com.example.app_armario.Repositories.VentaRepository
import com.example.app_armario.Repositories.ProductoRepository
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasHistorial(navController: NavHostController) {
    val context = LocalContext.current
    val repo = remember { VentaRepository(context) }
    val repoProductos = remember { ProductoRepository(context) }

    var ventas by remember { mutableStateOf<List<Venta>>(emptyList()) }
    var ventaExpandida by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        ventas = repo.obtenerVentas().reversed() // las más recientes primero
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Historial de Ventas", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (ventas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no hay ventas registradas 🛍", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(ventas, key = { it.id }) { venta ->
                        VentaCard(
                            venta = venta,
                            expandida = ventaExpandida == venta.id,
                            onExpandToggle = {
                                ventaExpandida = if (ventaExpandida == venta.id) null else venta.id
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VentaCard(
    venta: Venta,
    expandida: Boolean,
    onExpandToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandToggle() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C)),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Pedido: ${venta.id}", color = Color(0xFFB32DD4), fontWeight = FontWeight.Bold)
                    Text("Fecha: ${venta.fecha}", color = Color.White)
                }
                Text(
                    "$${formatoClp(venta.total)}",
                    color = Color(0xFFDFB9FF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            if (expandida) {
                Spacer(Modifier.height(8.dp))
                Divider(color = Color.Gray.copy(alpha = 0.4f))
                Spacer(Modifier.height(8.dp))
                venta.productos.forEach { item ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.nombre} (x${item.cantidad})", color = Color.White)
                        Text(
                            "$${formatoClp(item.precio * item.cantidad)}",
                            color = Color(0xFFB32DD4)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Divider(color = Color.Gray.copy(alpha = 0.4f))
                Text(
                    "Total: $${formatoClp(venta.total)}",
                    color = Color(0xFFDFB9FF),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

