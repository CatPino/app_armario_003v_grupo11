package com.example.app_armario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.app_armario.ui.theme.App_armarioTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //  Crear admin y sembrar productos si no existen (en corrutina porque son suspend)
        lifecycleScope.launch {
            crearAdminInicial(this@MainActivity)
            seedProductosSiVacio(this@MainActivity)
        }

        setContent {
            App_armarioTheme {
                AppNavigation()
            }
        }
    }
}
