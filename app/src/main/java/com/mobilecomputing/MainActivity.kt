@file:OptIn(ExperimentalMaterial3Api::class)

package com.mobilecomputing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileComputingTheme {
                MyApp()
            }
        }
    }
}

data class Message(val author: String, val body: String)



@Composable
fun MessageCard(msg: Message) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.profile_picture),
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
                text = msg.author,
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
fun Conversation(message: List<Message>, innerPaddingValues: PaddingValues) {
    Row(modifier = Modifier.padding(top = innerPaddingValues.calculateTopPadding())) {
        LazyColumn {
            items(message) { message ->
                MessageCard(message)
            }
        }

    }
}

@Composable
fun SettingsContent(innerPaddingValues: PaddingValues) {
    Row(modifier = Modifier.padding(top = innerPaddingValues.calculateTopPadding())) {
        Text(text = "Settings screen is coming here...")

    }

}

@Composable
fun SettingsScreen(onNavigateToLandingPage: () -> Unit) {
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
        SettingsContent(innerPadding)
    }
}


@Composable
fun LandingScreen(onNavigateToSettings: () -> Unit) {
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
        Conversation(SampleData.conversationSample, innerPadding)
    }


}


@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "landingScreen" ) {
        composable("landingScreen") { LandingScreen(onNavigateToSettings = { navController.navigate("settingsScreen")})}
        composable("settingsScreen") { SettingsScreen(onNavigateToLandingPage = {
            navController.navigate("landingScreen") {
                popUpTo("landingScreen") {
                    inclusive = true
                }
            }
        }) }
    }
}

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
