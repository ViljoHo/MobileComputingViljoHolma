package com.mobilecomputing.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM userTable")
    fun getAllUsers(): List<User>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateUser(user: User)

    @Query("SELECT * FROM userTable WHERE id LIKE :id LIMIT 1" )
    fun findById(id: Int): User?
}