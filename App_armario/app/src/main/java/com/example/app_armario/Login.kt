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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController) {
    val context = LocalContext.current
    val repo = remember { UsuarioRepository() }
    val scope = rememberCoroutineScope()

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (repo.validarLogin("","") != null) {
            navController.navigate(Screen.Home.route)
        }
    }

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
                    TextButton(onClick = { navController.navigate(Screen.Registro.route) }) {
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
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(25.dp))

            Button(
                onClick = {
                    scope.launch {
                        val usuario = repo.validarLogin(correo, contrasena)
                        if (usuario != null) {
                            guardarSesion(context, usuario.email, usuario.rol.nombre)
                            mensaje = "✅ Inicio de sesión correcto"
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
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
