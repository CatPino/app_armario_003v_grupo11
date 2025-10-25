package com.example.app_armario

import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay

// ===== Modelo de producto =====
data class Product(
    val id: String,
    val name: String,
    val price: String,
    val assetPath: String
)

// ===== Pantalla principal =====
@Composable
fun Home(navController: NavHostController) {
    val products by remember { mutableStateOf(loadHomeProducts()) }
    val carouselAssets = listOf(
        "img/fotoCarusel.1.png",
        "img/fotoCarusel.2.png"
    )

    Scaffold(
        topBar = { TopBar(navController) },
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            CarouselAssets(images = carouselAssets)

            Text(
                text = "Conoce los nuevos productos",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
            )
            Text(
                text = "Lo mejor del estilo dark/rock para tu outfit.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(Modifier.height(8.dp))

            ProductGrid(products = products, modifier = Modifier.weight(1f))

            Footer()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(navController: NavHostController) {
    TopAppBar(
        title = {
            Text(
                "Armario de Sombras",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(onClick = { /* abrir menú */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menú")
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate("login") }) {
                AsyncImage(
                    model = "file:///android_asset/img/user.png",
                    contentDescription = "Iniciar sesión",
                    modifier = Modifier
                        .size(26.dp)
                        .padding(2.dp)
                )
            }
            IconButton(onClick = { navController.navigate("registro") }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Registro")
            }
            IconButton(onClick = { /* carro */ }) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
            }
        }


    )
}

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
                            color = if (active) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}

@Composable
private fun ProductGrid(products: List<Product>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier.padding(horizontal = 12.dp)
    ) {
        items(products, key = { it.id }) { p ->
            ProductCard(p)
        }
    }
}

@Composable
private fun ProductCard(p: Product) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            AssetImage(
                assetPath = p.assetPath,
                contentDescription = p.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            Column(Modifier.padding(12.dp)) {
                Text(p.name, fontWeight = FontWeight.SemiBold)
                Text(p.price, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                Button(
                    onClick = { /* agregar al carro */ },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Agregar")
                }
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

@Composable
private fun Footer() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        Divider()
        Text(
            "© ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} Armario de Sombras",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

private fun loadHomeProducts(): List<Product> = listOf(
    Product("1", "Polera Iron Maiden", "$12.990", "img/PoleraIron.jpg"),
    Product("2", "Polera Slipknot", "$12.990", "img/PoleraSlip.jpg"),
    Product("3", "Calza Baphomet", "$13.990", "img/CalzaBap.jpg"),
    Product("4", "Falda Baphomet", "$14.990", "img/FaldaBap.png"),
    Product("5", "Chocker Calavera", "$6.990", "img/ChockerCalavera.jpg"),
    Product("6", "Cinturón Tachas", "$9.990", "img/Cinturon.jpg"),
)
