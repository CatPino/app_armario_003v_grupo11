package com.example.app_armario

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.app_armario.Models.Producto
import com.example.app_armario.Repositories.CarritoRepository

@Composable
fun ProductDetailDialog(
    producto: Producto,
    onDismiss: () -> Unit,
    onAddToCart: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val carritoRepo = CarritoRepository(context)

    val stockColor = if (producto.stock < 5) Color.Red else Color(0xFF4CAF50)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Cerrar", color = Color.White) }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        //
                        carritoRepo.agregar(
                            idProducto = producto.id,
                            nombre = producto.nombre,
                            precio = producto.precio,
                            imagenUrl = producto.imagenUrl
                        )

                        onAddToCart?.invoke()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4))
                ) { Text("Agregar", color = Color.White) }
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
