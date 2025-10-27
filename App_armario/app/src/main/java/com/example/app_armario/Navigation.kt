package com.example.app_armario

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash")   { SplashScreen(navController) }
        composable("home") { Home(navController) }
        composable("registro") { Registro(navController) }
        composable("login") { Login(navController) }
        composable("productos") { Productos(navController) }
        composable("admin_dashboard") { AdminDashboard(navController) }
        composable("carrito") { Carrito(navController) }
        composable("ventas_historial") { VentasHistorial(navController) }
        composable("mi_cuenta") { MiCuenta(navController) }






    }
}


