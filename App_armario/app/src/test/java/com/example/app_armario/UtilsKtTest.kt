package com.example.app_armario

import org.junit.Assert.*
import org.junit.Test

class UtilsKtTest {

    @Test
    fun formatoClp_conCero_retornaCero() {
        val resultado = formatoClp(0)
        assertEquals("0", resultado)
    }

    @Test
    fun formatoClp_conMiles_retornaConPunto() {
        val resultado = formatoClp(1000)
        assertEquals("1.000", resultado)
    }

    @Test
    fun formatoClp_conMillones_retornaConPuntos() {
        val resultado = formatoClp(1500000)
        assertEquals("1.500.000", resultado)
    }

    @Test
    fun formatoClp_conNumeroNegativo_retornaConSignoYFormato() {
        val resultado = formatoClp(-50000)
        assertEquals("-50.000", resultado)
    }
}
