package com.example.proyectoaplicaciones.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una organización registrada en la aplicación.
 * Se almacena localmente mediante Room.
 */
@Entity(tableName = "organizaciones")
data class OrganizacionEntity(

    // Identificador único generado automáticamente.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Nombre de la empresa o institución.
    val nombre: String,

    // Actividad económica o rubro.
    val rubro: String,

    // Tipo de organización: empresa, municipalidad,
    // institución educativa, entre otras.
    val tipo: String,

    // Dirección física de la organización.
    val direccion: String,

    // Ubicación geográfica.
    val comuna: String,
    val region: String,

    // Información de contacto general.
    val telefono: String,
    val correo: String,

    // Información adicional.
    val descripcion: String = ""
)