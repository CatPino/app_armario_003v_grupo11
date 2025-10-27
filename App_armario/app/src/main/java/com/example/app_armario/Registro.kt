package com.example.app_armario

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import com.example.app_armario.data.RegionesChile
import com.example.app_armario.Repositories.UsuarioRepository
import com.example.app_armario.Models.Usuario
import com.example.app_armario.Models.RolesPredefinidos
import com.example.app_armario.dataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repoUsuarios = remember { UsuarioRepository(context) }

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    var expandRegion by remember { mutableStateOf(false) }
    var expandComuna by remember { mutableStateOf(false) }

    val comunasDeRegion = remember(region) { RegionesChile.comunasDe(region) }

    // ===== Validaciones =====
    val emailRegex = Regex("^[^\\s@]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)\$", RegexOption.IGNORE_CASE)
    val correoValido = remember(correo) { correo.isNotEmpty() && emailRegex.matches(correo.trim()) }

    fun mensajeErrorCorreo(): String {
        val c = correo.trim()
        if (c.isEmpty()) return ""
        if (!c.contains("@")) return "El correo debe incluir '@'."
        if (!c.contains(".")) return "Ingresa un correo válido (@duoc.cl, @profesor.duoc.cl o @gmail.com)."
        if (c.length > 100) return "El correo no puede tener más de 100 caracteres."
        if (!emailRegex.matches(c)) return "Ingresa un correo válido (@duoc.cl, @profesor.duoc.cl o @gmail.com)."
        return ""
    }

    val regexMayuscula = Regex(".*[A-Z].*")
    val regexEspecial = Regex(".*[!@#\\\$%^&*()_+\\-=\\[\\]{};':\",.<>?/`~].*")
    fun mensajeErrorPassword(): String = when {
        contrasena.isEmpty() -> ""
        contrasena.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
        !regexMayuscula.containsMatchIn(contrasena) -> "Debe contener al menos una letra mayúscula."
        !regexEspecial.containsMatchIn(contrasena) -> "Debe incluir al menos un carácter especial (!, @, #, etc.)."
        contrasena.length > 100 -> "La contraseña no puede superar los 100 caracteres."
        else -> ""
    }

    val errorCorreo = mensajeErrorCorreo()
    val errorPass = mensajeErrorPassword()
    val nombreValido = nombre.trim().isNotEmpty()
    val regionValida = region.isNotBlank()
    val comunaValida = comuna.isNotBlank()
    val direccionValida = direccion.trim().isNotEmpty()

    val formularioValido =
        nombreValido && correoValido && errorPass.isEmpty() &&
                regionValida && comunaValida && direccionValida

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = "file:///android_asset/img/Logo.png",
                contentDescription = "Logo Armario de Sombras",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(20.dp))
            Text("Crea tu cuenta en Armario de Sombras", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            // ===== Campos =====
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre completo", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White), // 👈 texto blanco
                singleLine = true,
                isError = !nombreValido && nombre.isNotEmpty(),
                supportingText = {
                    if (!nombreValido && nombre.isNotEmpty())
                        Text("Ingresa tu nombre.", color = Color.Red, fontSize = 12.sp)
                },
                colors = tfColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = correo, onValueChange = { correo = it },
                label = { Text("Correo electrónico", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = errorCorreo.isNotEmpty(),
                trailingIcon = {
                    when {
                        correo.isEmpty() -> {}
                        correoValido -> Icon(Icons.Default.CheckCircle, contentDescription = "válido", tint = Color(0xFF4CAF50))
                        errorCorreo.isNotEmpty() -> Icon(Icons.Default.Close, contentDescription = "inválido", tint = Color.Red)
                    }
                },
                supportingText = {
                    if (correo.isNotEmpty())
                        Text(
                            if (correoValido) "Correo válido ✅" else errorCorreo,
                            color = if (correoValido) Color(0xFF4CAF50) else Color.Red,
                            fontSize = 12.sp
                        )
                },
                colors = tfColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = contrasena, onValueChange = { contrasena = it },
                label = { Text("Contraseña", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = errorPass.isNotEmpty(),
                trailingIcon = {
                    when {
                        contrasena.isEmpty() -> {}
                        errorPass.isEmpty() -> Icon(Icons.Default.CheckCircle, contentDescription = "válida", tint = Color(0xFF4CAF50))
                        else -> Icon(Icons.Default.Close, contentDescription = "inválida", tint = Color.Red)
                    }
                },
                supportingText = {
                    if (contrasena.isNotEmpty())
                        Text(
                            if (errorPass.isEmpty()) "Contraseña válida ✅" else errorPass,
                            color = if (errorPass.isEmpty()) Color(0xFF4CAF50) else Color.Red,
                            fontSize = 12.sp
                        )
                },
                colors = tfColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // ===== Región =====
            ExposedDropdownMenuBox(expanded = expandRegion, onExpandedChange = { expandRegion = it }) {
                OutlinedTextField(
                    value = region,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    isError = !regionValida && region.isNotEmpty(),
                    label = { Text("Región", color = Color.White) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandRegion) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = tfColors()
                )
                ExposedDropdownMenu(expanded = expandRegion, onDismissRequest = { expandRegion = false }) {
                    RegionesChile.regiones().forEach { r ->
                        DropdownMenuItem(
                            text = { Text(r) },
                            onClick = {
                                region = r
                                comuna = ""
                                expandRegion = false
                            }
                        )
                    }
                }
            }
            if (!regionValida && region.isNotEmpty()) {
                Text("Selecciona una región válida.", color = Color.Red, fontSize = 12.sp)
            }
            Spacer(Modifier.height(10.dp))

            // ===== Comuna =====
            ExposedDropdownMenuBox(
                expanded = expandComuna,
                onExpandedChange = { if (region.isNotBlank()) expandComuna = it }
            ) {
                OutlinedTextField(
                    value = comuna,
                    onValueChange = {},
                    readOnly = true,
                    enabled = region.isNotBlank(),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    isError = region.isNotBlank() && !comunaValida && comuna.isNotEmpty(),
                    label = {
                        Text(
                            if (region.isBlank()) "Selecciona primero una región" else "Comuna",
                            color = Color.White
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandComuna) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = tfColors()
                )
                ExposedDropdownMenu(expanded = expandComuna, onDismissRequest = { expandComuna = false }) {
                    comunasDeRegion.forEach { c ->
                        DropdownMenuItem(text = { Text(c) }, onClick = { comuna = c; expandComuna = false })
                    }
                }
            }
            if (region.isNotBlank() && !comunaValida && comuna.isNotEmpty()) {
                Text("Selecciona una comuna válida.", color = Color.Red, fontSize = 12.sp)
            }
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = direccion, onValueChange = { direccion = it },
                label = { Text("Dirección", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                singleLine = true,
                isError = !direccionValida && direccion.isNotEmpty(),
                supportingText = {
                    if (!direccionValida && direccion.isNotEmpty())
                        Text("Ingresa tu dirección.", color = Color.Red, fontSize = 12.sp)
                },
                colors = tfColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (!formularioValido) {
                            mensaje = "⚠️ Completa y corrige los campos marcados"
                            return@launch
                        }

                        try {
                            val existente = repoUsuarios.buscarPorEmail(correo.trim())
                            if (existente != null) {
                                mensaje = "❌ El correo ya está registrado"
                                return@launch
                            }

                            repoUsuarios.agregarUsuario(
                                Usuario(
                                    nombre = nombre.trim(),
                                    email = correo.trim(),
                                    password = contrasena,
                                    telefono = null,
                                    rol = RolesPredefinidos.CLIENTE,
                                    region = region,
                                    comuna = comuna,
                                    direccion = direccion
                                )
                            )

                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                                guardarUsuario(context, nombre, correo, contrasena, region, comuna, direccion)
                            }

                            mensaje = "✅ Registro guardado correctamente"
                            delay(900)
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            mensaje = "❌ Error al registrar: ${e.message ?: "desconocido"}"
                        }
                    }
                },
                enabled = formularioValido,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB32DD4),
                    disabledContainerColor = Color(0xFF6A4B74)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar", color = Color.White, fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))
            if (mensaje.isNotEmpty()) {
                Text(
                    mensaje,
                    color = if (mensaje.contains("✅")) Color(0xFFB32DD4) else Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun tfColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFB32DD4),
    unfocusedBorderColor = Color(0xFF9C27B0),
    cursorColor = Color(0xFFB32DD4),
    focusedLabelColor = Color(0xFFB32DD4),
    unfocusedLabelColor = Color.LightGray
)

suspend fun guardarUsuario(
    context: Context,
    nombre: String,
    correo: String,
    contrasena: String,
    region: String,
    comuna: String,
    direccion: String
) {
    val nombreKey = stringPreferencesKey("nombre")
    val correoKey = stringPreferencesKey("correo")
    val contrasenaKey = stringPreferencesKey("contrasena")
    val regionKey = stringPreferencesKey("region")
    val comunaKey = stringPreferencesKey("comuna")
    val direccionKey = stringPreferencesKey("direccion")

    context.dataStore.edit { prefs ->
        prefs[nombreKey] = nombre.trim()
        prefs[correoKey] = correo.trim()
        prefs[contrasenaKey] = contrasena
        prefs[regionKey] = region
        prefs[comunaKey] = comuna
        prefs[direccionKey] = direccion.trim()
    }
}
