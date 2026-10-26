package com.example.proyectoaplicaciones.model

//estos son los mensajes opcionales que salen cuando se ingresan datos incorrectos
//también se clasifican los tipos de datos que van a dar esos errores
data class OportunidadErrores(
    val nombre: String? = null,
    val descripcion: String? = null,
    val tipo: String? = null,
    val prioridad: String? = null,
    val potencialComercial: String? = null,
    val estado: String? = null,
    val fuente: String? = null
)