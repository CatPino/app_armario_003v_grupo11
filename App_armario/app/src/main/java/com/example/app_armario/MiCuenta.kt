package com.example.app_armario

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app_armario.Repositories.UsuarioRepository
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiCuenta(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val usuarioRepo = remember { UsuarioRepository(context) }

    // --- Estado: sesión + perfil ---
    var email by remember { mutableStateOf<String?>(null) }
    var rol by remember { mutableStateOf<String?>(null) }
    var nombre by remember { mutableStateOf<String?>(null) }
    var region by remember { mutableStateOf<String?>(null) }
    var comuna by remember { mutableStateOf<String?>(null) }
    var direccion by remember { mutableStateOf<String?>(null) }
    var avatarUri by remember { mutableStateOf<String?>(null) }

    // Cargar datos de sesión y perfil
    LaunchedEffect(Unit) {
        email = obtenerEmailUsuario(context)
        rol = obtenerRolUsuario(context)
        // Datos del UsuarioRepository (nombre, etc.)
        val user = email?.let { usuarioRepo.buscarPorEmail(it) }
        nombre = user?.nombre ?: "Invitado"
        // Extras guardados en DataStore por Registro
        region = leerPref(context, "region")
        comuna = leerPref(context, "comuna")
        direccion = leerPref(context, "direccion")
        avatarUri = leerPref(context, "avatar_uri")
    }

    // Launchers para seleccionar/tomar foto
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                scope.launch {
                    guardarPref(context, "avatar_uri", it.toString())
                    avatarUri = it.toString()
                }
            }
        }

    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
            if (bmp != null) {
                scope.launch {
                    val localUri = guardarBitmapEnCache(context, bmp)
                    guardarPref(context, "avatar_uri", localUri.toString())
                    avatarUri = localUri.toString()
                }
            }
        }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Mi cuenta", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                if (!avatarUri.isNullOrBlank()) {
                    AsyncImage(
                        model = avatarUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                    )
                } else {
                    // placeholder
                    AsyncImage(
                        model = "file:///android_asset/img/user.png",
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            // Botones de foto
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFFB32DD4))
                    Spacer(Modifier.width(6.dp))
                    Text("Galería")
                }
                OutlinedButton(
                    onClick = { takePictureLauncher.launch(null) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFB32DD4))
                    Spacer(Modifier.width(6.dp))
                    Text("Cámara")
                }
            }

            Spacer(Modifier.height(24.dp))

            // Datos de usuario (si hay sesión)
            if (email == null) {
                Text("No has iniciado sesión.", color = Color.Gray)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4))
                ) { Text("Ir a iniciar sesión", color = Color.White) }
            } else {
                // Bloques de info
                InfoRow(label = "Nombre", value = nombre ?: "-")
                InfoRow(label = "Email", value = email ?: "-")
                InfoRow(label = "Rol", value = rol?.uppercase() ?: "-")
                Divider(color = Color.DarkGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
                InfoRow(label = "Región", value = region ?: "-")
                InfoRow(label = "Comuna", value = comuna ?: "-")
                InfoRow(label = "Dirección", value = direccion ?: "-")

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        // cerrar sesión y volver al Home
                        val scopeLocal = scope
                        scopeLocal.launch {
                            cerrarSesion(context)
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5F5F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.LightGray)
        Text(value, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
    Spacer(Modifier.height(8.dp))
}

/* ==================== Helpers DataStore y archivos ==================== */

private suspend fun obtenerEmailUsuario(context: Context): String? {
    val key = stringPreferencesKey("usuario_logueado")
    return context.dataStore.data.map { it[key] }.first()
}

private suspend fun leerPref(context: Context, keyName: String): String? {
    val k = stringPreferencesKey(keyName)
    return context.dataStore.data.map { it[k] }.first()
}

private suspend fun guardarPref(context: Context, keyName: String, value: String) {
    val k = stringPreferencesKey(keyName)
    context.dataStore.edit { prefs -> prefs[k] = value }
}

private fun guardarBitmapEnCache(context: Context, bmp: Bitmap): Uri {
    val file = File(context.cacheDir, "avatar_${UUID.randomUUID()}.jpg")
    FileOutputStream(file).use { out ->
        bmp.compress(Bitmap.CompressFormat.JPEG, 92, out)
    }
    return Uri.fromFile(file)
}
