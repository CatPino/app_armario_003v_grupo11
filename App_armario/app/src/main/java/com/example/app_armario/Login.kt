package com.example.app_armario

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
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
            AsyncImage(
                model = "file:///android_asset/img/Logo.png",
                contentDescription = "Logo Armario de Sombras",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Bienvenida a tu Armario de Sombras",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

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
                    scope.launch {
                        val datos = obtenerUsuario(context)
                        if (correo == datos["correo"] && contrasena == datos["contrasena"]) {
                            mensaje = "✅ Bienvenida, ${datos["nombre"]}"
                            delay(1500)
                            navController.navigate("home")
                        } else {
                            mensaje = "❌ Correo o contraseña incorrectos"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB32DD4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión", color = Color.White, fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            if (mensaje.isNotEmpty()) {
                Text(
                    mensaje,
                    color = if (mensaje.contains("✅")) Color(0xFFB32DD4) else Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Botón para cerrar sesión (solo visible si ya hay usuario)
            Button(
                onClick = {
                    scope.launch {
                        limpiarDatosUsuario(context)
                        mensaje = "🔒 Sesión cerrada"
                        delay(1200)
                        navController.navigate("home")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión", color = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("registro") }) {
                Text(
                    "¿No tienes cuenta? Regístrate aquí",
                    color = Color(0xFFB32DD4),
                    fontSize = 14.sp
                )
            }
        }
    }
}

// === Leer datos guardados en DataStore ===
suspend fun obtenerUsuario(context: Context): Map<String, String> {
    val nombreKey = stringPreferencesKey("nombre")
    val correoKey = stringPreferencesKey("correo")
    val contrasenaKey = stringPreferencesKey("contrasena")

    val prefs = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            mapOf(
                "nombre" to (preferences[nombreKey] ?: ""),
                "correo" to (preferences[correoKey] ?: ""),
                "contrasena" to (preferences[contrasenaKey] ?: "")
            )
        }
        .first()

    return prefs
}

// === Función para cerrar sesión (limpiar DataStore) ===
suspend fun limpiarDatosUsuario(context: Context) {
    val nombreKey = stringPreferencesKey("nombre")
    val correoKey = stringPreferencesKey("correo")
    val contrasenaKey = stringPreferencesKey("contrasena")

    context.dataStore.edit { prefs ->
        prefs.remove(nombreKey)
        prefs.remove(correoKey)
        prefs.remove(contrasenaKey)
    }
}
