package com.example.app_armario

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app_armario.Models.Usuario
import com.example.app_armario.Repositories.UsuarioRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(navController: NavHostController) {
    val context = LocalContext.current
    val repo = remember { UsuarioRepository() }
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    val emailRegex = Regex("^[^\\s@]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$", RegexOption.IGNORE_CASE)
    val correoValido = remember(correo) { correo.isNotEmpty() && emailRegex.matches(correo.trim()) }

    val regexMayuscula = Regex(".*[A-Z].*")
    val regexEspecial = Regex(".*[!@#\\\$%^&*()_+\\-=\\[\\]{};':\",.<>?/`~].*")

    fun mensajeErrorPassword(): String = when {
        contrasena.isEmpty() -> ""
        contrasena.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
        !regexMayuscula.containsMatchIn(contrasena) -> "Debe contener al menos una letra mayúscula."
        !regexEspecial.containsMatchIn(contrasena) -> "Debe incluir al menos un carácter especial (!, @, #, etc.)."
        else -> ""
    }

    val errorPass = mensajeErrorPassword()
    val contrasenaValida = contrasena.isNotEmpty() && errorPass.isEmpty()
    val contrasenasCoinciden = contrasena == confirmarContrasena

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

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo", color = Color.White) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico", color = Color.White) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = !correoValido && correo.isNotEmpty(),
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña", color = Color.White) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = !contrasenaValida && contrasena.isNotEmpty(),
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            if (contrasena.isNotEmpty() && !contrasenaValida) {
                Text(errorPass, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmarContrasena,
                onValueChange = { confirmarContrasena = it },
                label = { Text("Confirmar contraseña", color = Color.White) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = !contrasenasCoinciden && confirmarContrasena.isNotEmpty(),
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            if (confirmarContrasena.isNotEmpty() && !contrasenasCoinciden) {
                Text("Las contraseñas no coinciden", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(Modifier.height(25.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (nombre.isBlank() || !correoValido || !contrasenaValida || !contrasenasCoinciden) {
                            mensaje = "⚠️ Revisa los campos antes de continuar"
                            return@launch
                        }

                        val nuevoUsuario = Usuario(
                            nombre = nombre,
                            email = correo,
                            password = contrasena
                        )

                        val resultado = repo.agregarUsuario(nuevoUsuario)

                        resultado.onSuccess {
                            mensaje = "✅ Registro exitoso. Inicia sesión."
                            navController.navigate("login")
                        }.onFailure {
                            mensaje = "❌ Error al registrar: ${it.message}"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Registrarse", color = Color.White, fontSize = 16.sp)
            }

            if (mensaje.isNotEmpty()) {
                Text(
                    mensaje,
                    color = if (mensaje.startsWith("✅")) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Color(0xFFB32DD4),
    unfocusedBorderColor = Color(0xFF9C27B0),
    cursorColor = Color(0xFFB32DD4)
)
