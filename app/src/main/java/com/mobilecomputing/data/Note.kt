package com.mobilecomputing.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "noteTable")
data class Note (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val author: String,
    val body: String
)
