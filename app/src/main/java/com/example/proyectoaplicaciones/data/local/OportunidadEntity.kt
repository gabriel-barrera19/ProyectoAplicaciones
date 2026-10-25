package com.example.proyectoaplicaciones.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Representa una oportunidad almacenada en Room.
@Entity(tableName = "oportunidades")
data class OportunidadEntity(

    // Identificador único generado automáticamente.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val descripcion: String,
    val tipo: String,
    val fuente: String,
    val prioridad: String,
    val potencialComercial: String,

    // Estado inicial definido para una oportunidad nueva.
    val estado: String = "Evento identificado",

    val observaciones: String = ""
)