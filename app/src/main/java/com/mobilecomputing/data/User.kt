package com.mobilecomputing.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "userTable")
data class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userName: String,
    val profilePicPath: String,
)