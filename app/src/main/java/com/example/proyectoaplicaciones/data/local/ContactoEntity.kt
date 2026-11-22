package com.example.proyectoaplicaciones.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa un contacto perteneciente a una organización.
 */
@Entity(
    tableName = "contactos",
    foreignKeys = [
        ForeignKey(
            entity = OrganizacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["organizacionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["organizacionId"])
    ]
)
data class ContactoEntity(

    // Identificador único del contacto.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Organización a la que pertenece el contacto.
    val organizacionId: Int,

    // Datos personales y profesionales.
    val nombre: String,
    val cargo: String,
    val correo: String,
    val telefono: String
)