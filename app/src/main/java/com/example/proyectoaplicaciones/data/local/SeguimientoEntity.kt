package com.example.proyectoaplicaciones.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "seguimientos",
    foreignKeys = [
        ForeignKey(
            entity = OportunidadEntity::class,
            parentColumns = ["id"],
            childColumns = ["oportunidadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["oportunidadId"])
    ]
)
data class SeguimientoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val oportunidadId: Int,

    val fecha: String,

    val tipoGestion: String,

    val resultado: String,

    val observaciones: String = ""
)