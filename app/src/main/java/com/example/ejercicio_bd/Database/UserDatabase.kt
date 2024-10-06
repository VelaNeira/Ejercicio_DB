package com.example.ejercicio_bd.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ejercicio_bd.DAO.UserDao
import com.example.ejercicio_bd.Model.User

//Abstract es usado para evitar crear nuevas instancias de la BD y room gestiona la relacion

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    // Compain Object se usa para definir miembros estáticos en kotlin
    //Volatile permite que cualquier hilo que exceda la variable tenga la version 1 NO CAMBIARLA NUNCA

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null
        //permite crear la instancia de la base de datos

        fun getDatabase(context: Context): UserDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "userdatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}