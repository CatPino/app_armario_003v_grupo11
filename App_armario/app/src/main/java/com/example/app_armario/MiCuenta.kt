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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app_armario.Repositories.UsuarioRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiCuenta(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val usuarioRepo = remember { UsuarioRepository() }

    var email by remember { mutableStateOf<String?>(null) }
    var rol by remember { mutableStateOf<String?>(null) }
    var nombre by remember { mutableStateOf<String?>(null) }
    var region by remember { mutableStateOf<String?>(null) }
    var comuna by remember { mutableStateOf<String?>(null) }
    var direccion by remember { mutableStateOf<String?>(null) }
    var avatarUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        email = obtenerEmailUsuarioLocal(context)
        rol = obtenerRolUsuarioLocal(context)
        avatarUri = leerPrefLocal(context, "avatar_uri")

        val user = email?.let { usuarioRepo.buscarPorEmail(it) }
        if (user != null) {
            nombre = user.nombre
            region = user.region
            comuna = user.comuna
            direccion = user.direccion
        } else {
            nombre = "Invitado"
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            scope.launch {
                guardarPrefLocal(context, "avatar_uri", it.toString())
                avatarUri = it.toString()
            }
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
        if (bmp != null) {
            scope.launch {
                val localUri = guardarBitmapEnCache(context, bmp)
                guardarPrefLocal(context, "avatar_uri", localUri.toString())
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
            modifier = Modifier.padding(inner).fillMaxSize().background(Color.Black).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                if (!avatarUri.isNullOrBlank()) {
                    AsyncImage(model = avatarUri, contentDescription = "Foto de perfil", modifier = Modifier.size(110.dp).clip(CircleShape))
                } else {
                    AsyncImage(model = "file:///android_asset/img/user.png", contentDescription = "Foto de perfil", modifier = Modifier.size(110.dp).clip(CircleShape))
                }
            }
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFFB32DD4))
                    Spacer(Modifier.width(6.dp))
                    Text("Galería")
                }
                OutlinedButton(onClick = { takePictureLauncher.launch(null) }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFB32DD4))
                    Spacer(Modifier.width(6.dp))
                    Text("Cámara")
                }
            }

            Spacer(Modifier.height(24.dp))

            if (email == null) {
                Text("No has iniciado sesión.", color = Color.Gray)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { navController.navigate(Screen.Login.route) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4))) {
                    Text("Ir a iniciar sesión", color = Color.White)
                }
            } else {
                InfoRow("Nombre", nombre ?: "-")
                InfoRow("Email", email ?: "-")
                InfoRow("Rol", rol?.uppercase() ?: "-")
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                InfoRow("Región", region ?: "-")
                InfoRow("Comuna", comuna ?: "-")
                InfoRow("Dirección", direccion ?: "-")

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        scope.launch {
                            cerrarSesionLocal(context)
                            navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true }; launchSingleTop = true }
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
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.LightGray)
        Text(value, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
    Spacer(Modifier.height(8.dp))
}

private suspend fun obtenerEmailUsuarioLocal(context: Context): String? {
    val key = stringPreferencesKey("usuario_logueado")
    return context.dataStore.data.map { it[key] }.first()
}

private suspend fun obtenerRolUsuarioLocal(context: Context): String? {
    val key = stringPreferencesKey("rol_logueado")
    return context.dataStore.data.map { it[key] }.first()
}

private suspend fun leerPrefLocal(context: Context, keyName: String): String? {
    val k = stringPreferencesKey(keyName)
    return context.dataStore.data.map { it[k] }.first()
}

private suspend fun guardarPrefLocal(context: Context, keyName: String, value: String) {
    val k = stringPreferencesKey(keyName)
    context.dataStore.edit { prefs -> prefs[k] = value }
}

private suspend fun cerrarSesionLocal(context: Context) {
    context.dataStore.edit {
        it.clear()
    }
}

private fun guardarBitmapEnCache(context: Context, bmp: Bitmap): Uri {
    val file = File(context.cacheDir, "avatar_${UUID.randomUUID()}.jpg")
    FileOutputStream(file).use { out ->
        bmp.compress(Bitmap.CompressFormat.JPEG, 92, out)
    }
    return Uri.fromFile(file)
}

@Composable
fun HorizontalDivider(modifier: Modifier = Modifier, thickness: Dp = 1.dp, color: Color = Color.Gray) {
    Box(modifier.fillMaxWidth().height(thickness).background(color))
}
