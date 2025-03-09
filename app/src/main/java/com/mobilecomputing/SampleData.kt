package com.mobilecomputing

import com.mobilecomputing.ui.components.Message

/**
 * SampleData for Jetpack Compose Tutorial
 */
object SampleData {
    // Sample conversation data
    val conversationSample = listOf(
        Message(
            "Nickname",
            "Test...Test...Test..."
        ),
        Message(
            "Nickname",
            """List of Android versions:
            |Android KitKat (API 19)
            |Android Lollipop (API 21)
            |Android Marshmallow (API 23)
            |Android Nougat (API 24)
            |Android Oreo (API 26)
            |Android Pie (API 28)
            |Android 10 (API 29)
            |Android 11 (API 30)
            |Android 12 (API 31)""".trim()
        ),
        Message(
            "Nickname",
            """I think Kotlin is my favorite programming language.
            |It's so much fun!""".trim()
        ),
        Message(
            "Nickname",
            "Searching for alternatives to XML layouts..."
        ),
        Message(
            "Nickname",
            """Hey, take a look at Jetpack Compose, it's great!
            |It's the Android's modern toolkit for building native UI.
            |It simplifies and accelerates UI development on Android.
            |Less code, powerful tools, and intuitive Kotlin APIs :)""".trim()
        ),
        Message(
            "Nickname",
            "It's available from API 21+ :)"
        ),
        Message(
            "Nickname",
            "Writing Kotlin for UI seems so natural, Compose where have you been all my life?"
        ),

    )
}