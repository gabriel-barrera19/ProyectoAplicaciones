package com.example.proyectoaplicaciones.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa un evento o actividad organizada por una empresa
 * o institución.
 *
 * Un evento puede existir aunque todavía no tenga una
 * oportunidad comercial asociada.
 */
@Entity(
    tableName = "eventos",
    foreignKeys = [
        ForeignKey(
            entity = OrganizacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["organizacionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["organizacionId"])
    ]
)
data class EventoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Información general del evento.
    val nombre: String,
    val tipo: String,
    val descripcion: String,

    // Fechas y horario.
    val fechaInicio: String,
    val fechaTermino: String,
    val horario: String,

    // Ubicación del evento.
    val ubicacion: String,
    val comuna: String,
    val region: String,

    // Organización responsable.
    // Puede quedar sin asignar inicialmente.
    val organizacionId: Int? = null,

    // Información del público.
    val publicoObjetivo: String = "",
    val publicoEsperado: String = "",
    val cantidadEstimadaAsistentes: Int? = null,

    // Condiciones para participar.
    val condicionesParticipacion: String = "",
    val requisitos: String = "",

    // Información comercial del evento.
    val costosReferenciales: String = "",
    val espaciosDisponibles: String = "",

    // Información adicional.
    val observaciones: String = ""
)