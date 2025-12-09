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
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route)   { SplashScreen(navController) }
        composable(Screen.Home.route) { Home(navController) }
        composable(Screen.Registro.route) { Registro(navController) }
        composable(Screen.Login.route) { Login(navController) }
        composable(Screen.Productos.route) { Productos(navController) }
        composable(Screen.AdminDashboard.route) { AdminDashboard(navController) }
        composable(Screen.Carrito.route) { Carrito(navController) }
        composable(Screen.VentasHistorial.route) { VentasHistorial(navController) }
        composable(Screen.MiCuenta.route) { MiCuenta(navController) }
    }
}
