package com.example.app_armario

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Registro : Screen("registro")
    object Login : Screen("login")
    object Productos : Screen("productos")
    object AdminDashboard : Screen("admin_dashboard")
    object Carrito : Screen("carrito")
    object VentasHistorial : Screen("ventas_historial")
    object MiCuenta : Screen("mi_cuenta")
}
