package com.example.app_armario

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(nav: NavHostController) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800),
        label = "splashAlpha"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(1200)
        nav.navigate("home") {
            popUpTo("splash") { inclusive = true }  // no volver al splash
            launchSingleTop = true
        }
    }


    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AsyncImage(
            model = "file:///android_asset/img/Logo.png",
            contentDescription = "Logo Armario de Sombras",
            modifier = Modifier.alpha(alpha)
                .let { it } // (placeholder para futuros tamaños si quieres)
        )
    }
}
