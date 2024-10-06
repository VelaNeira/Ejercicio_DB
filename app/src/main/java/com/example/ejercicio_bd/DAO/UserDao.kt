package com.example.ejercicio_bd.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ejercicio_bd.Model.User

@Dao
interface UserDao {
    // Suspend evitar que la aplicacion falle cuando se realizan las peticiones al realizar operaciones de forma asicronas

    @Insert(onConflict = OnConflictStrategy.REPLACE) //Revisión de conflictos entre registros
    suspend fun insert (user: User)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteById(userId: Int):Int

    @Update
    suspend fun updateUser(user: User)

}