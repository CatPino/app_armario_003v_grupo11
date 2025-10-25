package com.example.app_armario

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.app_armario.Models.RolesPredefinidos
import com.example.app_armario.Models.Usuario
import com.example.app_armario.Repositories.UsuarioRepository
import com.example.app_armario.ui.theme.App_armarioTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //  Crear admin si no existe
        crearAdminInicial(this)

        setContent {
            App_armarioTheme {
                AppNavigation()
            }
        }
    }
}