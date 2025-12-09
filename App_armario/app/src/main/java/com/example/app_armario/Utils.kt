package com.example.app_armario

/**
 * Da formato a un número Long como moneda CLP (ej: 1000 -> 1.000).
 */
fun formatoClp(n: Long): String = "%,d".format(n).replace(',', '.')
