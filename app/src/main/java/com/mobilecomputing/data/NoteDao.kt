package com.mobilecomputing.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Query("SELECT * FROM noteTable")
    fun getAllNotes(): List<Note>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNotes(notes: List<Note>)
}