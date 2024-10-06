package com.example.ejercicio_bd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ejercicio_bd.DAO.UserDao
import com.example.ejercicio_bd.Database.UserDatabase
import com.example.ejercicio_bd.Repository.UserRepository
import com.example.ejercicio_bd.Screen.UserApp

class MainActivity : ComponentActivity() {
    //Creacion de variables locales.
    private lateinit var userDao: UserDao
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = UserDatabase.getDatabase(applicationContext)
        userDao = db.userDao()
        userRepository= UserRepository(userDao) // Inicializa user Repository
        enableEdgeToEdge()
        setContent {
            UserApp(userRepository)
        }
    }
}