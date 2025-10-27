package com.example.app_armario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app_armario.Models.Categoria
import com.example.app_armario.Models.Producto
import com.example.app_armario.Repositories.CarritoRepository
import com.example.app_armario.Repositories.ProductoRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Productos(navController: NavHostController) {
    val context = LocalContext.current
    val repo = remember { ProductoRepository(context) }

    val carritoRepo = remember { CarritoRepository(context) }
    val cartCount by carritoRepo.contador.collectAsState()
    val scope = rememberCoroutineScope()

    // Cargar desde DataStore
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    LaunchedEffect(Unit) {
        seedProductosSiVacio(context)
        productos = repo.getProductos()
    }

    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }

    val productosFiltrados = remember(productos, categoriaSeleccionada) {
        if (categoriaSeleccionada == null) productos
        else productos.filter { it.categoria.nombre == categoriaSeleccionada!!.nombre }
    }

    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

    productoSeleccionado?.let { prod ->
        ProductDetailDialog(
            producto = prod,
            onDismiss = { productoSeleccionado = null },
            onAddToCart = { carritoRepo.agregar(prod.id, prod.nombre, prod.precio, prod.imagenUrl) }
        )
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Productos", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("login") }) {
                        AsyncImage(
                            model = "file:///android_asset/img/user.png",
                            contentDescription = "Iniciar sesión",
                            modifier = Modifier.size(26.dp).padding(2.dp)
                        )
                    }
                    IconButton(onClick = { navController.navigate("registro") }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Registro", tint = Color.White)
                    }
                    // 🛒 Badge dinámico
                    BadgedBox(
                        badge = {
                            if (cartCount > 0) {
                                Badge(
                                    containerColor = Color(0xFFB32DD4),
                                    contentColor = Color.White
                                ) { Text("$cartCount") }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate("carrito") }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // ===== FILTRO DE CATEGORÍAS =====
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoriaChip(
                    texto = "Todas",
                    seleccionado = categoriaSeleccionada == null,
                    onClick = { categoriaSeleccionada = null }
                )
                Categoria.TODAS.forEach { cat ->
                    CategoriaChip(
                        texto = cat.nombre,
                        seleccionado = categoriaSeleccionada == cat,
                        onClick = { categoriaSeleccionada = cat }
                    )
                }
            }

            Divider(color = Color(0xFFB32DD4))
            CategoriaDescripcion(categoriaSeleccionada)
            Spacer(Modifier.height(10.dp))

            // ===== LISTA DE PRODUCTOS =====
            if (productosFiltrados.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sin productos para mostrar.", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                ) {
                    items(productosFiltrados, key = { it.id }) { p ->
                        ProductoCard(
                            p = p,
                            onClick = { productoSeleccionado = p },
                            onAdd = {
                                carritoRepo.agregar(
                                    idProducto = p.id,
                                    nombre = p.nombre,
                                    precio = p.precio,
                                    imagenUrl = p.imagenUrl
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

/* =============== COMPONENTES AUXILIARES =============== */

@Composable
fun CategoriaChip(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                texto,
                color = if (seleccionado) Color.White else Color(0xFFB32DD4),
                fontSize = 14.sp
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (seleccionado) Color(0xFFB32DD4) else Color.Transparent,
            labelColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (seleccionado) Color(0xFFB32DD4) else Color.Gray
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
fun ProductoCard(
    p: Producto,
    onClick: () -> Unit,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = p.imagenUrl ?: "file:///android_asset/img/default.png",
                contentDescription = p.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
            Text(p.nombre, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text("$${p.precio}", color = Color(0xFFB32DD4))
            Text(p.categoria.nombre, color = Color.Gray, fontSize = 12.sp)

            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4)),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Text("Agregar", color = Color.White)
            }
        }
    }
}

@Composable
fun CategoriaDescripcion(categoria: Categoria?) {
    val (titulo, descripcion) = when (categoria) {
        null -> "Conoce todos nuestros productos" to
                "Dale a tu outfit un toque gótico auténtico. Cada prenda está diseñada para expresar tu personalidad con fuerza."
        Categoria.POLERAS -> "Poleras" to
                "Nuestras poleras son el reflejo de un estilo auténtico. Simples, pero con personalidad marcada, añaden a tu look un aire oscuro y moderno."
        Categoria.FALDAS -> "Faldas" to
                "Las faldas están pensadas para realzar tu esencia con un aire alternativo. Su estilo sutilmente gótico permite expresar tu personalidad con fuerza."
        Categoria.CALZAS -> "Calzas" to
                "Las calzas combinan comodidad con un aire rebelde y moderno. Ideales para un look urbano con matices oscuros, acompañan tu día a día sin perder estilo."
        Categoria.ACCESORIOS -> "Accesorios" to
                "Cada accesorio es un detalle que transforma tu estilo. Diseñados para resaltar tu individualidad, aportan un toque único y sofisticado a tu look."
        else -> "" to ""
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        border = BorderStroke(1.dp, Color(0xFFB96CFF).copy(alpha = 0.6f)),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                color = Color(0xFFDFB9FF),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = descripcion,
                color = Color.LightGray,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
