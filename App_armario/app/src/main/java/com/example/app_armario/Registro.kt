package com.example.app_armario

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// === DataStore para guardar los datos localmente ===



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registro(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Registro de Usuario",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            AsyncImage(
                model = "file:///android_asset/img/Logo.png",
                contentDescription = "Logo Armario de Sombras",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Crea tu cuenta en Armario de Sombras",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo", color = Color.White) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFB32DD4),
                    unfocusedBorderColor = Color(0xFF9C27B0)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico", color = Color.White) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFB32DD4),
                    unfocusedBorderColor = Color(0xFF9C27B0)
                ),
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFB32DD4),
                    unfocusedBorderColor = Color(0xFF9C27B0)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (nombre.isNotBlank() && correo.isNotBlank() && contrasena.isNotBlank()) {
                        scope.launch {
                            guardarUsuario(context, nombre, correo, contrasena)
                            mensaje = "✅ Registro guardado correctamente"
                            delay(1500)
                            navController.navigate("home")
                        }
                    } else {
                        mensaje = "⚠️ Completa todos los campos"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4)),
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

// === Guardar datos en DataStore ===
suspend fun guardarUsuario(context: Context, nombre: String, correo: String, contrasena: String) {
    val nombreKey = stringPreferencesKey("nombre")
    val correoKey = stringPreferencesKey("correo")
    val contrasenaKey = stringPreferencesKey("contrasena")

    context.dataStore.edit { prefs ->
        prefs[nombreKey] = nombre
        prefs[correoKey] = correo
        prefs[contrasenaKey] = contrasena
    }
}