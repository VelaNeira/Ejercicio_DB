package com.example.ejercicio_bd.Repository

import androidx.room.Update
import com.example.ejercicio_bd.DAO.UserDao
import com.example.ejercicio_bd.Model.User


class UserRepository(private val userDao: UserDao) {
    suspend fun insertar(user: User){
        userDao.insert(user)
    }
    suspend fun getAllUsers(): List<User>{
        return userDao.getAllUsers()
    }
    suspend fun deleteById(userId:Int):Int {
        return userDao.deleteById(userId)
    }
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}