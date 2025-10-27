package com.example.app_armario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
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
import coil.request.ImageRequest
import com.example.app_armario.Models.Producto
import com.example.app_armario.Repositories.ProductoRepository
import com.example.app_armario.Repositories.CarritoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { ProductoRepository(context) }

    // 🛒 Carrito: contador reactivo
    val carritoRepo = remember { CarritoRepository(context) }
    val cartCount by carritoRepo.contador.collectAsState()

    // Cargar productos desde DataStore
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    LaunchedEffect(Unit) {
        seedProductosSiVacio(context)
        productos = repo.getProductos()
    }

    val carouselAssets = listOf("img/fotoCarusel.1.png", "img/fotoCarusel.2.png")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController,
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Armario de Sombras",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
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

                        //  Badge dinámico
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
        ) { inner ->
            Column(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                CarouselAssets(images = carouselAssets)

                Text(
                    text = "Conoce los nuevos productos",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                )
                Text(
                    text = "Lo mejor del estilo dark/rock para tu outfit.",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Divider(color = Color(0xFFB32DD4), thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))

                Spacer(Modifier.height(8.dp))

                // Muestra los últimos 4 productos
                val destacados = remember(productos) { productos.takeLast(4) }
                if (destacados.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Sin productos por ahora.", color = Color.Gray)
                    }
                } else {
                    ProductGrid(products = destacados, modifier = Modifier.weight(1f))
                }

                Footer()
            }
        }
    }
}

/* ========================= Drawer ========================= */

@Composable
private fun DrawerContent(navController: NavHostController, onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var rolUsuario by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        rolUsuario = obtenerRolUsuario(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Menú principal",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Divider(color = Color(0xFFB32DD4))
        Spacer(Modifier.height(12.dp))

        DrawerItem("🏠 Inicio") { navController.navigate("home"); onClose() }
        DrawerItem("🛍 Productos") { navController.navigate("productos"); onClose() }

        DrawerItem("👤 Mi cuenta") { navController.navigate("mi_cuenta"); onClose() }


        if (rolUsuario.equals("ADMIN", ignoreCase = true)) {
            DrawerItem("⚙ Panel Admin") {
                navController.navigate("admin_dashboard")
                onClose()
            }
            DrawerItem("🧾 Historial de Ventas") { navController.navigate("ventas_historial"); onClose() }

            DrawerItem("🚪 Cerrar sesión") {
                scope.launch {
                    cerrarSesion(context)
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                onClose()
            }
        }

        Spacer(Modifier.weight(1f))
        Divider(color = Color.Gray)
        Text(
            "© ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} Armario de Sombras",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun DrawerItem(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(
            text,
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/* ========================= Carrusel ========================= */

@Composable
private fun CarouselAssets(images: List<String>, autoScrollMs: Long = 2500L) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    LaunchedEffect(pagerState.currentPage, images.size) {
        delay(autoScrollMs)
        val next = (pagerState.currentPage + 1) % images.size
        pagerState.animateScrollToPage(next)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        HorizontalPager(state = pagerState) { page ->
            AssetImage(
                assetPath = images[page],
                contentDescription = "Banner $page",
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
        ) {
            repeat(images.size) { idx ->
                val active = pagerState.currentPage == idx
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .size(if (active) 9.dp else 7.dp)
                        .background(
                            color = if (active) Color(0xFFB32DD4) else Color.White.copy(alpha = 0.4f),
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}

@Composable
private fun AssetImage(
    assetPath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("file:///android_asset/$assetPath")
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}

/* ========================= Grilla de Productos ========================= */

@Composable
private fun ProductGrid(products: List<Producto>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val carritoRepo = remember { CarritoRepository(context) }

    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

    productoSeleccionado?.let { prod ->
        ProductDetailDialog(
            producto = prod,
            onDismiss = { productoSeleccionado = null },
            onAddToCart = { /* ya agrega desde diálogo */ }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier.padding(horizontal = 12.dp)
    ) {
        items(products, key = { it.id }) { p ->
            ProductCard(
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

@Composable
private fun ProductCard(
    p: Producto,
    onClick: () -> Unit,
    onAdd: () -> Unit = {}
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

/* ========================= Footer ========================= */

@Composable
private fun Footer() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        Divider(color = Color(0xFFB32DD4))
        Text(
            "© ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} Armario de Sombras",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
