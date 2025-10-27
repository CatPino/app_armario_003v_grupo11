package com.example.app_armario

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
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

    // ✅ Repositorio de carrito compartido
    val carritoRepo = remember(context) { CarritoRepository(context) }
    val cartCount by carritoRepo.contador.collectAsState()

    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        seedProductosSiVacio(context)
        delay(1000)
        productos = repo.getProductos()
        loading = false
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
                        // 🛒 Badge dinámico que reacciona al instante
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
            if (loading) {
                LoadingSplash()
            } else {
                AnimatedVisibility(
                    visible = !loading,
                    enter = fadeIn(animationSpec = tween(1200))
                ) {
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

                        val destacados = remember(productos) { productos.takeLast(4) }
                        if (destacados.isEmpty()) {
                            Box(
                                Modifier.fillMaxWidth().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Sin productos por ahora.", color = Color.Gray)
                            }
                        } else {
                            // ✅ Ahora se pasa el carrito compartido
                            ProductGrid(
                                products = destacados,
                                modifier = Modifier.weight(1f),
                                carritoRepo = carritoRepo
                            )
                        }

                        Footer()
                    }
                }
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
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
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
        DrawerItem("📝 Regístrate") { navController.navigate("registro"); onClose() }

        if (rolUsuario.equals("ADMIN", ignoreCase = true)) {
            Divider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            DrawerItem("⚙ Panel Admin") { navController.navigate("admin_dashboard"); onClose() }
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
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            Text(text, color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Start)
        }
    }
}

/* ========================= Carrusel ========================= */
@Composable
private fun CarouselAssets(images: List<String>, autoScrollMs: Long = 3000L) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    var currentPage by remember { mutableStateOf(0) }

    LaunchedEffect(currentPage, images.size) {
        delay(autoScrollMs)
        val next = (currentPage + 1) % images.size
        pagerState.animateScrollToPage(next)
        currentPage = next
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(220.dp).background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(state = pagerState) { page ->
            val scale by animateFloatAsState(
                targetValue = if (pagerState.currentPage == page) 1f else 0.9f,
                animationSpec = tween(800, easing = LinearOutSlowInEasing)
            )
            val alpha by animateFloatAsState(
                targetValue = if (pagerState.currentPage == page) 1f else 0.4f,
                animationSpec = tween(800, easing = LinearOutSlowInEasing)
            )

            AsyncImage(
                model = "file:///android_asset/${images[page]}",
                contentDescription = "Banner $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha)
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
        ) {
            repeat(images.size) { idx ->
                val active = pagerState.currentPage == idx
                val size by animateDpAsState(targetValue = if (active) 10.dp else 6.dp, animationSpec = tween(300))
                Box(
                    modifier = Modifier.padding(3.dp).size(size)
                        .background(
                            color = if (active) Color(0xFFB32DD4) else Color.White.copy(alpha = 0.4f),
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}

/* ✅ ProductGrid ahora recibe carritoRepo compartido */
@Composable
private fun ProductGrid(products: List<Producto>, modifier: Modifier = Modifier, carritoRepo: CarritoRepository) {
    val context = LocalContext.current
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

    productoSeleccionado?.let { prod ->
        ProductDetailDialog(
            producto = prod,
            onDismiss = { productoSeleccionado = null },
            onAddToCart = {
                carritoRepo.agregar(
                    idProducto = prod.id,
                    nombre = prod.nombre,
                    precio = prod.precio,
                    imagenUrl = prod.imagenUrl
                )
            }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier.padding(horizontal = 12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(products, key = { it.id }) { p ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { it / 3 }),
                exit = fadeOut(tween(300))
            ) {
                ProductCardAnimatedHome(
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

/* ========================= Card Producto ========================= */
@Composable
private fun ProductCardAnimatedHome(p: Producto, onClick: () -> Unit, onAdd: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, animationSpec = tween(300, easing = LinearOutSlowInEasing))

    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable { pressed = true; onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            AsyncImage(
                model = p.imagenUrl ?: "file:///android_asset/img/default.png",
                contentDescription = p.nombre,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = p.nombre, color = Color.White, fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                softWrap = true, maxLines = 3
            )
            Spacer(Modifier.height(4.dp))
            Text("$${p.precio}", color = Color(0xFFB32DD4), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(p.categoria.nombre, color = Color.Gray, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            AnimatedAddButtonHome(onAdd)
        }
    }
    LaunchedEffect(pressed) { if (pressed) { delay(120); pressed = false } }
}

/* ========================= Botón Agregar ========================= */
@Composable
private fun AnimatedAddButtonHome(onAdd: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f)
    Button(
        onClick = { pressed = true; onAdd() },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4)),
        modifier = Modifier.fillMaxWidth().height(42.dp).graphicsLayer(scaleX = scale, scaleY = scale)
    ) { Text("Agregar", color = Color.White, fontWeight = FontWeight.Medium) }
    LaunchedEffect(pressed) { if (pressed) { delay(150); pressed = false } }
}

/* ========================= Loading ========================= */
@Composable
private fun LoadingSplash() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(200); visible = true }
    AnimatedVisibility(visible = visible, enter = fadeIn(animationSpec = tween(1000))) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(model = "file:///android_asset/img/logo.png", contentDescription = "Logo", modifier = Modifier.size(180.dp))
            Spacer(Modifier.height(12.dp))
            Text("Cargando productos...", color = Color.White)
        }
    }
}

/* ========================= Footer ========================= */
@Composable
private fun Footer() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)) {
        Divider(color = Color(0xFFB32DD4))
        Text(
            "© ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} Armario de Sombras",
            textAlign = TextAlign.Center, color = Color.Gray,
            style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp)
        )
    }
}
