package com.example.app_armario

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import com.example.app_armario.Models.Categoria
import com.example.app_armario.Models.Producto
import com.example.app_armario.Repositories.CarritoRepository
import com.example.app_armario.Repositories.ProductoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Productos(navController: NavHostController) {
    val context = LocalContext.current
    val repo = remember { ProductoRepository(context) }

    val carritoRepo = remember { CarritoRepository(context) }
    val cartCount by carritoRepo.contador.collectAsState()
    val scope = rememberCoroutineScope()

    // === Cargar productos desde DataStore ===
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        seedProductosSiVacio(context)
        delay(600)
        productos = repo.getProductos()
        cargando = false
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
        if (cargando) {
            LoadingAnimacionProductos()
        } else {
            AnimatedVisibility(
                visible = !cargando,
                enter = fadeIn(tween(800)) + slideInVertically(initialOffsetY = { it / 4 }),
                exit = fadeOut(tween(300))
            ) {
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

                    AnimatedContent(
                        targetState = categoriaSeleccionada,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                        },
                        label = ""
                    ) { categoria ->
                        Divider(color = Color(0xFFB32DD4))
                        CategoriaDescripcion(categoria)
                    }

                    Spacer(Modifier.height(10.dp))

                    // ===== LISTA DE PRODUCTOS =====
                    if (productosFiltrados.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Sin productos para mostrar.", color = Color.Gray)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 160.dp),
                            modifier = Modifier.padding(8.dp).fillMaxSize()
                        ) {
                            items(productosFiltrados, key = { it.id }) { p ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(tween(700)) + slideInVertically(initialOffsetY = { it / 3 }),
                                    exit = fadeOut(tween(300))
                                ) {
                                    ProductoCardAnimado(
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
        }
    }
}

/* =================== CHIP =================== */

@Composable
fun CategoriaChip(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(if (seleccionado) 1.1f else 1f)
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
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
    )
}

/* =================== CARD PRODUCTO ANIMADO =================== */

/* =================== CARD PRODUCTO ANIMADO =================== */
@Composable
fun ProductoCardAnimado(p: Producto, onClick: () -> Unit, onAdd: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }

    // Animación de escala sutil al presionar
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(300, easing = LinearOutSlowInEasing)
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable {
                pressed = true
                onClick()
            },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // 🔹 Imagen con tamaño fijo
            AsyncImage(
                model = p.imagenUrl ?: "file:///android_asset/img/default.png",
                contentDescription = p.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp), // mantiene proporción de las imágenes
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(8.dp))

            // 🔹 Nombre del producto, adaptable sin cortar
            Text(
                text = p.nombre,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                softWrap = true, // permite salto de línea
                maxLines = 3     // pero sin cortar bruscamente
            )

            Spacer(Modifier.height(4.dp))

            // 🔹 Precio
            Text(
                text = "$${p.precio}",
                color = Color(0xFFB32DD4),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            // 🔹 Categoría
            Text(
                text = p.categoria.nombre,
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(8.dp))

            // 🔹 Botón inferior
            AnimatedAddButton(onAdd)
        }
    }

    // 🔁 Vuelve al estado normal después de presionar
    LaunchedEffect(pressed) {
        if (pressed) {
            delay(120)
            pressed = false
        }
    }
}

/* =================== BOTÓN "AGREGAR" =================== */
@Composable
private fun AnimatedAddButton(onAdd: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.9f else 1f)

    Button(
        onClick = {
            pressed = true
            onAdd()
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4)),
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
    ) {
        Text("Agregar", color = Color.White, fontWeight = FontWeight.Medium)
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(150)
            pressed = false
        }
    }
}

/* =================== DESCRIPCIÓN DE CATEGORÍA =================== */

@Composable
fun CategoriaDescripcion(categoria: Categoria?) {
    val (titulo, descripcion) = when (categoria) {
        null -> "Conoce todos nuestros productos" to
                "Dale a tu outfit un toque gótico auténtico. Cada prenda está diseñada para expresar tu personalidad con fuerza."
        Categoria.POLERAS -> "Poleras" to
                "Nuestras poleras son el reflejo de un estilo auténtico y moderno."
        Categoria.FALDAS -> "Faldas" to
                "Las faldas realzan tu esencia con un aire alternativo."
        Categoria.CALZAS -> "Calzas" to
                "Comodidad y rebeldía en un solo estilo urbano y oscuro."
        Categoria.ACCESORIOS -> "Accesorios" to
                "Detalles que transforman tu look y resaltan tu individualidad."
        else -> "" to ""
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(400)) + expandVertically(),
        exit = fadeOut(tween(300))
    ) {
        Card(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
            border = BorderStroke(1.dp, Color(0xFFB96CFF).copy(alpha = 0.6f)),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
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
}

/* =================== LOADING =================== */

@Composable
fun LoadingAnimacionProductos() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(1000))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = Color(0xFFB32DD4))
            Spacer(Modifier.height(20.dp))
            Text("Cargando productos...", color = Color.White)
        }
    }
}
