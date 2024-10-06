package com.example.ejercicio_bd.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users") //se va a convertir en una tabla para la BD, llamada users
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val edad: Int
)
