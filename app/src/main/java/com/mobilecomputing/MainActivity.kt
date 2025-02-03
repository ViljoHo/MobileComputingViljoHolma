@file:OptIn(ExperimentalMaterial3Api::class)

package com.mobilecomputing

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mobilecomputing.ui.theme.MobileComputingTheme
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.material3.Icon
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
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
import coil.compose.AsyncImage
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.material3.TextField
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.room.Room
import coil.compose.rememberAsyncImagePainter
import com.mobilecomputing.data.User
import com.mobilecomputing.data.UserDao
import com.mobilecomputing.data.UserDataBase
import java.io.File


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

                MyApp(applicationContext, userDao)
            }
        }
    }
}

data class Message(val author: String, val body: String)


    //: R.drawable.profile_picture
@Composable
fun MessageCard(msg: Message, user: User?) {
    //painter = painterResource(R.drawable.profile_picture),
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = rememberAsyncImagePainter(
                model = user?.profilePicPath?.let { File(it) }
            ),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember {
            mutableStateOf(false)
        }
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            label = "",
        )

        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = user?.userName ?: msg.author,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
                ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

}

@Composable
fun Conversation(message: List<Message>, innerPaddingValues: PaddingValues, user: User?) {
    Row(modifier = Modifier.padding(top = innerPaddingValues.calculateTopPadding())) {
        LazyColumn {
            items(message) { message ->
                MessageCard(message, user)
            }
        }

    }
}

@Composable
fun SettingsContent(innerPaddingValues: PaddingValues, applicationContext: Context, user: User?, updatedUser: (User) -> Unit) {

    val filename = "profile_pic_${System.currentTimeMillis()}"
    val file = File(applicationContext.filesDir, filename)


    var currentFilePath by remember {
        mutableStateOf<String>(user?.profilePicPath ?: "")
    }

    //Log.d("SettingsContent", "currentFilePath $currentFilePath")

    var text by remember {
        mutableStateOf<String>(user?.userName ?: "Nickname")
    }


    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = PickVisualMedia(),
        onResult = { uri ->



            if (uri != null) {

                //delete old filepath so there is only one pic at the time
                currentFilePath?.let { oldFilePath ->
                    val oldFile = File(oldFilePath)
                    if (oldFile.exists()) {
                        oldFile.delete()
                    }
                }

                val resolver = applicationContext.contentResolver
                try {
                    resolver
                        .openInputStream(uri)
                        ?.use { stream ->
                            stream.copyTo(file.outputStream())
                        }
                } catch (e: Exception) {
                    Log.e("SettingsContent", "Error processing URI: $uri", e)
                }


                currentFilePath = file.absolutePath

                Thread {
                    updatedUser(User(1, text, file.absolutePath))
                }.start()
            }
        }
    )




    LazyColumn(
        modifier = Modifier
            .padding(top = innerPaddingValues.calculateTopPadding())
    ) {
        item {
            Text(
                text = "User:",
                modifier = Modifier.padding(all=8.dp)
            )
        }



        item {
            AsyncImage(
                model = currentFilePath?.let { File(it) },
                contentDescription = null,
                modifier = Modifier
                    .clickable(onClick = {
                        singlePhotoPickerLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    })
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        item {
            TextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    updatedUser(User(1, text, currentFilePath)) },
                modifier = Modifier.padding(all=8.dp)
            )
        }
    }


}

@Composable
fun SettingsScreen(onNavigateToLandingPage: () -> Unit, applicationContext: Context, user: User?, updatedUser: (User) -> Unit) {
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
        SettingsContent(innerPadding, applicationContext, user, updatedUser)
    }
}


@Composable
fun LandingScreen(onNavigateToSettings: () -> Unit, user: User?) {
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
                    IconButton(onClick = {onNavigateToSettings()}) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings button")
                    }
                }
            )
        },
    ) { innerPadding ->
        Conversation(SampleData.conversationSample, innerPadding, user)
    }


}


@Composable
fun MyApp(applicationContext: Context, userDao: UserDao) {
    val navController = rememberNavController()
    var user by remember {
        mutableStateOf<User?>(null)
    }

    LaunchedEffect(Unit) {
        user = userDao.findById(1)
    }


    NavHost(navController, startDestination = "landingScreen" ) {
        composable("landingScreen") { LandingScreen(onNavigateToSettings = { navController.navigate("settingsScreen")},
            user,
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
            }) }
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