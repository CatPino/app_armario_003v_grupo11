package com.example.app_armario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.app_armario.Repositories.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { crearAdminInicial(context) }

    val repo = remember { UsuarioRepository(context) }
    val scope = rememberCoroutineScope()

    // ========= Comprobamos si hay sesión activa =========
    var usuarioSesion by remember { mutableStateOf<com.example.app_armario.Models.Usuario?>(null) }
    LaunchedEffect(Unit) {
        val emailSesion = obtenerEmailSesion(context)   // <-- helper nuevo
        usuarioSesion = emailSesion?.let { repo.buscarPorEmail(it) }
    }

    // Si hay usuario en sesión => vista de cuenta
    usuarioSesion?.let { user ->
        CuentaView(
            navController = navController,
            usuario = user,
            onCerrarSesion = {
                scope.launch {
                    cerrarSesion(context)
                    usuarioSesion = null
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        )
        return
    }

    // ========= Si NO hay sesión: formulario de login =========

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    val emailRegex = Regex("^[^\\s@]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)\$", RegexOption.IGNORE_CASE)
    val correoValido = remember(correo) { correo.isNotEmpty() && emailRegex.matches(correo.trim()) }

    fun mensajeErrorCorreo(): String {
        val correoTrim = correo.trim()
        if (correoTrim.isEmpty()) return ""
        if (!correoTrim.contains("@")) return "El correo debe incluir '@'."
        if (!correoTrim.contains(".")) return "Ingresa un correo válido (@duoc.cl, @profesor.duoc.cl o @gmail.com)."
        if (correoTrim.length > 100) return "El correo no puede tener más de 100 caracteres."
        if (!emailRegex.matches(correoTrim))
            return "Ingresa un correo válido (@duoc.cl, @profesor.duoc.cl o @gmail.com)."
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
    val contrasenaValida = contrasena.isNotEmpty() && errorPass.isEmpty()

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { navController.navigate("registro") }) {
                        Text("Registrarse", color = Color(0xFFB32DD4))
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
            Text("Accede a tu cuenta", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico", color = Color.White) },
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = when {
                        correoValido -> Color(0xFF4CAF50)
                        errorCorreo.isNotEmpty() -> Color.Red
                        else -> Color(0xFFB32DD4)
                    },
                    unfocusedBorderColor = Color(0xFF9C27B0)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (correo.isNotEmpty()) {
                Text(
                    if (correoValido) "Correo válido ✅" else errorCorreo,
                    color = if (correoValido) Color(0xFF4CAF50) else Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña", color = Color.White) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = errorPass.isNotEmpty(),
                trailingIcon = {
                    when {
                        contrasena.isEmpty() -> {}
                        contrasenaValida -> Icon(Icons.Default.CheckCircle, contentDescription = "válida", tint = Color(0xFF4CAF50))
                        errorPass.isNotEmpty() -> Icon(Icons.Default.Close, contentDescription = "inválida", tint = Color.Red)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = when {
                        contrasenaValida -> Color(0xFF4CAF50)
                        errorPass.isNotEmpty() -> Color.Red
                        else -> Color(0xFFB32DD4)
                    },
                    unfocusedBorderColor = Color(0xFF9C27B0)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (contrasena.isNotEmpty()) {
                Text(
                    if (contrasenaValida) "Contraseña válida ✅" else errorPass,
                    color = if (contrasenaValida) Color(0xFF4CAF50) else Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(Modifier.height(25.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (!correoValido || !contrasenaValida) {
                            mensaje = "⚠️ Corrige los errores antes de continuar"
                            return@launch
                        }

                        val usuario = repo.validarLogin(correo, contrasena)
                        if (usuario != null) {
                            guardarSesion(
                                context = context,
                                email = usuario.email,
                                rol = usuario.rol.nombre
                            )
                            mensaje = "✅ Inicio de sesión correcto"
                            delay(600)
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        } else {
                            mensaje = "❌ Correo o contraseña incorrectos"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4)),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Iniciar sesión", color = Color.White, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))
            if (mensaje.isNotEmpty()) {
                Text(
                    mensaje,
                    color = if (mensaje.contains("✅")) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(20.dp))
            TextButton(onClick = { navController.navigate("registro") }) {
                Text("¿No tienes cuenta? Regístrate aquí", color = Color(0xFFB32DD4))
            }
            TextButton(onClick = { navController.navigate("home") }) {
                Text("⬅ Volver al inicio", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CuentaView(
    navController: NavHostController,
    usuario: com.example.app_armario.Models.Usuario,
    onCerrarSesion: () -> Unit
) {
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi cuenta",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
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
            AsyncImage(
                model = "file:///android_asset/img/user.png",
                contentDescription = "Avatar",
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(usuario.nombre, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(usuario.email, color = Color.LightGray)
            Text("Rol: ${usuario.rol.nombre.uppercase()}", color = Color(0xFFB32DD4), fontWeight = FontWeight.SemiBold)

            Spacer(Modifier.height(16.dp))
            // Solo si existen esos campos (pueden ser nulos según tu modelo)
            usuario.region?.let { Text("Región: $it", color = Color.White) }
            usuario.comuna?.let { Text("Comuna: $it", color = Color.White) }
            usuario.direccion?.let { Text("Dirección: $it", color = Color.White) }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true } },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Ir al Home", color = Color.White) }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onCerrarSesion,
                border = ButtonDefaults.outlinedButtonBorder(true),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cerrar sesión", color = Color.Red) }
        }
    }
}
