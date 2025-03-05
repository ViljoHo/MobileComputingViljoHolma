package com.mobilecomputing.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class, Note::class], version = 1)
abstract class UserDataBase: RoomDatabase() {
    abstract  fun userDao(): UserDao
    abstract  fun noteDao(): NoteDao
}