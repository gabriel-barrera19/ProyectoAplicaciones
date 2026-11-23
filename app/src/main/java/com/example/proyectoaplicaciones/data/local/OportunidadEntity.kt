package com.example.proyectoaplicaciones.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa una oportunidad comercial relacionada
 * con la participación de la agencia en un evento.
 */
@Entity(
    tableName = "oportunidades",
    foreignKeys = [
        ForeignKey(
            entity = EventoEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["eventoId"])
    ]
)
data class OportunidadEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Información comercial.
    val nombre: String,
    val descripcion: String,
    val tipo: String,
    val fuente: String,
    val prioridad: String,
    val potencialComercial: String,

    // Estado inicial de la oportunidad.
    val estado: String = "Evento identificado",

    // Observaciones comerciales.
    val observaciones: String = "",

    // Fecha de detección de la oportunidad.
    val fecha: String = "",

    // Evento asociado.
    // Puede ser null para conservar oportunidades anteriores
    // o registrar una oportunidad todavía sin evento asociado.
    val eventoId: Int? = null
)