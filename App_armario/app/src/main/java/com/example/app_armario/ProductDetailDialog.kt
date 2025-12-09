package com.example.app_armario

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.app_armario.Models.Producto
import com.example.app_armario.Repositories.CarritoRepository
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

@Composable
fun ProductDetailDialog(
    producto: Producto,
    onDismiss: () -> Unit,
    onAddToCart: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val carritoRepo = CarritoRepository(context)

    val stockColor = if (producto.stock < 5) Color.Red else Color(0xFF4CAF50)

    // ===== Animación para los botones =====
    var cerrarPressed by remember { mutableStateOf(false) }
    var agregarPressed by remember { mutableStateOf(false) }

    val cerrarScale by animateFloatAsState(
        targetValue = if (cerrarPressed) 0.9f else 1f,
        animationSpec = tween(120)
    )
    val agregarScale by animateFloatAsState(
        targetValue = if (agregarPressed) 0.8f else 1f,
        animationSpec = tween(150)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                // 🔹 Botón "Cerrar" con animación
                TextButton(
                    onClick = {
                        cerrarPressed = true
                        onDismiss()
                    },
                    modifier = Modifier.graphicsLayer(
                        scaleX = cerrarScale,
                        scaleY = cerrarScale
                    )
                ) {
                    Text("Cerrar", color = Color.White)
                }

                Spacer(Modifier.width(8.dp))

                // 🔹 Botón "Agregar" con animación (idéntico al de las cards)
                Button(
                    onClick = {
                        agregarPressed = true
                        carritoRepo.agregar(
                            idProducto = producto.id,
                            nombre = producto.nombre,
                            precio = producto.precio,
                            imagenUrl = producto.imagenUrl
                        )
                        onAddToCart?.invoke()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4)),
                    modifier = Modifier.graphicsLayer(
                        scaleX = agregarScale,
                        scaleY = agregarScale
                    )
                ) {
                    Text("Agregar", color = Color.White)
                }
            }

            // 🔁 Restablece animación al terminar
            LaunchedEffect(cerrarPressed) {
                if (cerrarPressed) {
                    delay(120)
                    cerrarPressed = false
                }
            }
            LaunchedEffect(agregarPressed) {
                if (agregarPressed) {
                    delay(150)
                    agregarPressed = false
                }
            }
        },
        title = { Text(producto.nombre, color = Color.White) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = producto.imagenUrl ?: "file:///android_asset/img/default.png",
                    contentDescription = producto.nombre,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
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