package com.example.app_armario

import android.content.Context
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
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app_armario.Models.Producto
import com.example.app_armario.Repositories.ProductoRepository
import com.example.app_armario.Repositories.CarritoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun Home(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val repo = remember { ProductoRepository(context) }

    val carritoRepo = remember { CarritoRepository(context) }
    val cartCount by carritoRepo.contador.collectAsState()

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
                        Text("Armario de Sombras", fontWeight = FontWeight.Bold, color = Color.White)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.Login.route) }) {
                            AsyncImage(model = "file:///android_asset/img/user.png", contentDescription = "Iniciar sesión", modifier = Modifier.size(26.dp).padding(2.dp))
                        }
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(containerColor = Color(0xFFB32DD4), contentColor = Color.White) { Text("$cartCount") }
                                }
                            }
                        ) {
                            IconButton(onClick = { navController.navigate(Screen.Carrito.route) }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = Color.White)
                            }
                        }
                    }
                )
            }
        ) { inner ->
            Column(
                modifier = Modifier.padding(inner).fillMaxSize().background(Color.Black)
            ) {
                CarouselAssets(images = carouselAssets)

                Text("Conoce los nuevos productos", color = Color.White, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp))
                Text("Lo mejor del estilo dark/rock para tu outfit.", color = Color.LightGray, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

                HorizontalDivider(color = Color(0xFFB32DD4), thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))

                Spacer(Modifier.height(8.dp))

                if (productos.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Cargando productos...", color = Color.Gray)
                    }
                } else {
                    ProductGrid(products = productos.takeLast(4), modifier = Modifier.weight(1f), navController = navController)
                }

                Footer()
            }
        }
    }
}

@Composable
fun DrawerContent(navController: NavHostController, onClose: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var rolUsuario by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        rolUsuario = obtenerRolUsuarioLocal(context)
    }

    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF121212),
        drawerContentColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text("Menú principal", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

            HorizontalDivider(color = Color(0xFFB32DD4))
            Spacer(Modifier.height(12.dp))

            DrawerItem("🏠 Inicio") { navController.navigate(Screen.Home.route); onClose() }
            DrawerItem("🛍 Productos") { navController.navigate(Screen.Productos.route); onClose() }
            DrawerItem("👤 Mi cuenta") { navController.navigate(Screen.MiCuenta.route); onClose() }

            if (rolUsuario != null && rolUsuario.equals("ADMIN", ignoreCase = true)) {
                DrawerItem("⚙ Panel Admin") { navController.navigate(Screen.AdminDashboard.route); onClose() }
                DrawerItem("🧾 Historial de Ventas") { navController.navigate(Screen.VentasHistorial.route); onClose() }
                DrawerItem("🚪 Cerrar sesión") {
                    scope.launch {
                        cerrarSesionLocal(context)
                        navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true }; launchSingleTop = true }
                    }
                    onClose()
                }
            }

            Spacer(Modifier.weight(1f))
            HorizontalDivider(color = Color.Gray)
            Text("© ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} Armario de Sombras", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(text, color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Start, modifier = Modifier.fillMaxWidth())
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CarouselAssets(images: List<String>, autoScrollMs: Long = 2500L) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    LaunchedEffect(pagerState.currentPage, images.size) {
        delay(autoScrollMs)
        pagerState.animateScrollToPage((pagerState.currentPage + 1) % images.size)
    }

    Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
        HorizontalPager(state = pagerState) { page ->
            AssetImage(images[page], "Banner $page", Modifier.fillMaxSize())
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
        ) {
            repeat(images.size) { idx ->
                Box(modifier = Modifier.padding(3.dp).size(if (pagerState.currentPage == idx) 9.dp else 7.dp).background(if (pagerState.currentPage == idx) Color(0xFFB32DD4) else Color.White.copy(alpha = 0.4f), MaterialTheme.shapes.small))
            }
        }
    }
}

@Composable
fun AssetImage(assetPath: String, contentDescription: String?, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Crop) {
    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data("file:///android_asset/$assetPath").crossfade(true).build(), contentDescription = contentDescription, contentScale = contentScale, modifier = modifier)
}

@Composable
fun ProductGrid(products: List<Producto>, modifier: Modifier = Modifier, navController: NavHostController) {
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

    productoSeleccionado?.let { prod ->
        ProductDetailDialog(prod, { productoSeleccionado = null })
    }

    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 160.dp), modifier = modifier.padding(horizontal = 12.dp)) {
        items(products, key = { it.id }) { p ->
            ProductCard(p, { productoSeleccionado = p })
        }
    }
}

@Composable
fun ProductCard(p: Producto, onClick: () -> Unit) {
    val context = LocalContext.current
    val carritoRepo = remember { CarritoRepository(context) }
    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
            AsyncImage(p.imagenUrl ?: "file:///android_asset/img/default.png", p.nombre, modifier = Modifier.fillMaxWidth().height(150.dp), contentScale = ContentScale.Crop)
            Spacer(Modifier.height(8.dp))
            Text(p.nombre, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text("$${p.precio}", color = Color(0xFFB32DD4))
            Text(p.categoria.nombre, color = Color.Gray, fontSize = 12.sp)

            Button(onClick = { carritoRepo.agregar(p.id, p.nombre, p.precio, p.imagenUrl) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4)), modifier = Modifier.padding(top = 8.dp).fillMaxWidth().height(40.dp)) {
                Text("Agregar", color = Color.White)
            }
        }
    }
}

@Composable
fun Footer() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
    ) {
        HorizontalDivider(color = Color(0xFFB32DD4))
        Text("© ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} Armario de Sombras", textAlign = TextAlign.Center, color = Color.Gray, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
    }
}

private suspend fun obtenerRolUsuarioLocal(context: Context): String? {
    val key = stringPreferencesKey("rol_logueado")
    return context.dataStore.data.map { it[key] }.first()
}

private suspend fun cerrarSesionLocal(context: Context) {
    context.dataStore.edit { prefs ->
        prefs.remove(stringPreferencesKey("usuario_logueado"))
        prefs.remove(stringPreferencesKey("rol_logueado"))
    }
}

@Composable
fun ProductDetailDialog(producto: Producto, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val carritoRepo = remember { CarritoRepository(context) }
    val stockColor = if (producto.stock < 5) Color.Red else Color(0xFF4CAF50)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cerrar", color = Color.White) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { 
                        carritoRepo.agregar(producto.id, producto.nombre, producto.precio, producto.imagenUrl)
                        onDismiss()
                     },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4))
                ) { Text("Agregar", color = Color.White) }
            }
        },
        title = { Text(producto.nombre, color = Color.White) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                AsyncImage(producto.imagenUrl ?: "file:///android_asset/img/default.png", producto.nombre, modifier = Modifier.fillMaxWidth().height(200.dp), contentScale = ContentScale.Crop)
                Spacer(Modifier.height(12.dp))
                Text("Precio: $${producto.precio}", color = Color(0xFFB32DD4))
                Text("Stock: ${producto.stock}", color = stockColor)
                if (producto.descripcion.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Detalle", color = Color.LightGray)
                    Text(producto.descripcion, color = Color.White)
                }
                if (producto.tallas.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Tallas: ${producto.tallas.joinToString()}", color = Color.White)
                }
                if (producto.colores.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text("Colores: ${producto.colores.joinToString()}", color = Color.White)
                }
                producto.material?.let {
                    Spacer(Modifier.height(4.dp))
                    Text("Material: $it", color = Color.White)
                }
            }
        },
        containerColor = Color(0xFF121212),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}
