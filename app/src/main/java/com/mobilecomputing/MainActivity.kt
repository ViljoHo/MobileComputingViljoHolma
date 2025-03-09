@file:OptIn(ExperimentalMaterial3Api::class)

package com.mobilecomputing

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mobilecomputing.ui.theme.MobileComputingTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect
import androidx.room.Room
import com.mobilecomputing.data.User
import com.mobilecomputing.data.UserDao
import com.mobilecomputing.data.UserDataBase
import com.mobilecomputing.data.NoteDao
import com.mobilecomputing.data.Note
import com.mobilecomputing.ui.components.ApiScreenContent
import com.mobilecomputing.ui.components.Conversation
import com.mobilecomputing.ui.components.SettingsContent
import com.mobilecomputing.utils.NotificationHelper


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannel(this)

        setContent {
            MobileComputingTheme {
                val applicationContext = this.applicationContext
                val db = Room.databaseBuilder(
                            applicationContext,
                            UserDataBase::class.java, "User-database"
                        )
                    .allowMainThreadQueries()
                    .build()
                val userDao = db.userDao()
                val noteDao = db.noteDao()

                // initialize some notes to app
                val notes = SampleData.conversationSample.map {
                    Note(0, author = it.author, body = it.body)
                }

                noteDao.addNotes(notes)

                MyApp(applicationContext, userDao, noteDao, showNotification = NotificationHelper::showNotification)
            }
        }
    }
}


@Composable
fun ApiScreen(
    onNavigateToLandingPage: () -> Unit,
    applicationContext: Context,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Api Screen")
                },
                navigationIcon = {
                    IconButton(onClick = {onNavigateToLandingPage()}) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back Button")
                    }
                }
            )
        },

    ) { innerPadding ->
        ApiScreenContent(innerPadding, applicationContext)

    }

}

@Composable
fun SettingsScreen(
    onNavigateToLandingPage: () -> Unit,
    applicationContext: Context,
    user: User?,
    updatedUser: (User) -> Unit,
    sendNotification: (Context, String, String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Settings Screen")
                },
                navigationIcon = {
                    IconButton(onClick = {onNavigateToLandingPage()}) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Settings button")
                    }
                }
            )
        },
    ) { innerPadding ->
        SettingsContent(innerPadding, applicationContext, user, updatedUser, sendNotification)
    }
}


@Composable
fun LandingScreen(onNavigateToSettings: () -> Unit, onNavigateToApi: () -> Unit, user: User?, notes: List<Note>?, addNote: (Note) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Landing Screen")
                },
                actions = {
                    IconButton(onClick = {onNavigateToApi()}) {
                        Icon(Icons.Filled.Info, contentDescription = "Button to api page")
                    }
                    IconButton(onClick = {onNavigateToSettings()}) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings button")
                    }
                }
            )
        },
    ) { innerPadding ->
        Conversation(SampleData.conversationSample, innerPadding, user, notes, addNote)
    }


}


@Composable
fun MyApp(applicationContext: Context, userDao: UserDao, noteDao: NoteDao, showNotification: (Context,String, String) -> Unit) {
    val navController = rememberNavController()
    var user by remember {
        mutableStateOf<User?>(null)
    }

    var notes by remember {
        mutableStateOf<List<Note>?>(null)
    }

    LaunchedEffect(Unit) {
        user = userDao.findById(1)
        notes = noteDao.getAllNotes()
    }



    NavHost(navController, startDestination = "landingScreen" ) {
        composable("landingScreen") { LandingScreen(onNavigateToSettings = { navController.navigate("settingsScreen")},
            onNavigateToApi = { navController.navigate("apiScreen")},
            user,
            notes,
            addNote = { newNote ->
                notes = (notes ?: emptyList()) + newNote
                Thread { noteDao.addNote(newNote) }.start()

            }
            )}
        composable("settingsScreen") { SettingsScreen(onNavigateToLandingPage = {
            navController.navigate("landingScreen") {
                popUpTo("landingScreen") {
                    inclusive = true
                }
            }
        },
            applicationContext,
            user,
            updatedUser = { updatedUser ->
                user = updatedUser
                Thread { userDao.addOrUpdateUser(updatedUser) }.start()
            },
            showNotification
            ) }
        composable("apiScreen") { ApiScreen(onNavigateToLandingPage = {
            navController.navigate("landingScreen") {
                popUpTo("landingScreen") {
                    inclusive = true
                }
            }
        },
            applicationContext,
        ) }
    }
}

/**
@Preview
@Composable
fun PreviewMyApp() {
    MobileComputingTheme {
        MyApp()
    }
}


@Preview
@Composable
fun PreviewConversation() {
    MobileComputingTheme {
        Conversation(SampleData.conversationSample, innerPaddingValues = PaddingValues(0.dp))
    }
}

@Preview(name = "Light mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun PreviewMessageCard() {
    MobileComputingTheme {
        Surface {
            MessageCard(
                msg = Message("Lexi", "Take a look at Jetpack Compose, it's great!")
            )
        }
    }
}
 **/