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

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    // ===== Regex igual a tu versión JS =====
    val emailRegex = Regex("^[^\\s@]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)\$", RegexOption.IGNORE_CASE)

    // ===== Validaciones de correo =====
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

    // ===== Validaciones de contraseña =====
    val regexMayuscula = Regex(".*[A-Z].*")
    val regexEspecial = Regex(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\",.<>?/`~].*")

    fun mensajeErrorPassword(): String {
        return when {
            contrasena.isEmpty() -> ""
            contrasena.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
            !regexMayuscula.containsMatchIn(contrasena) -> "Debe contener al menos una letra mayúscula."
            !regexEspecial.containsMatchIn(contrasena) -> "Debe incluir al menos un carácter especial (!, @, #, etc.)."
            contrasena.length > 100 -> "La contraseña no puede superar los 100 caracteres."
            else -> ""
        }
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
            // 🖤 Logo
            AsyncImage(
                model = "file:///android_asset/img/Logo.png",
                contentDescription = "Logo Armario de Sombras",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(20.dp))
            Text("Accede a tu cuenta", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))

            // ======= CAMPO CORREO =======
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
                        correoValido -> Icon(Icons.Default.CheckCircle, "válido", tint = Color(0xFF4CAF50))
                        errorCorreo.isNotEmpty() -> Icon(Icons.Default.Close, "inválido", tint = Color.Red)
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

            // ======= CAMPO CONTRASEÑA =======
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
                        contrasenaValida -> Icon(Icons.Default.CheckCircle, "válida", tint = Color(0xFF4CAF50))
                        errorPass.isNotEmpty() -> Icon(Icons.Default.Close, "inválida", tint = Color.Red)
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

            // ======= BOTÓN LOGIN =======
            Button(
                onClick = {
                    scope.launch {
                        if (!correoValido || !contrasenaValida) {
                            mensaje = "⚠️ Corrige los errores antes de continuar"
                            return@launch
                        }

                        val usuario = repo.validarLogin(correo, contrasena)
                        if (usuario != null) {
                            mensaje = "✅ Inicio de sesión correcto"
                            delay(1500)
                            if (usuario.rol.nombre.equals("ADMIN", ignoreCase = true))
                                navController.navigate("admin_dashboard")
                            else
                                navController.navigate("home")
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
