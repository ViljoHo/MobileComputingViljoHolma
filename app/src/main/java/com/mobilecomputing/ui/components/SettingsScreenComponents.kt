package com.mobilecomputing.ui.components

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.mobilecomputing.data.User
import java.io.File


@Composable
fun SettingsContent(
    innerPaddingValues: PaddingValues,
    applicationContext: Context,
    user: User?,
    updatedUser: (User) -> Unit,
    sendNotification: (Context, String, String) -> Unit
) {

    val filename = "profile_pic_${System.currentTimeMillis()}"
    val file = File(applicationContext.filesDir, filename)


    var currentFilePath by remember {
        mutableStateOf<String>(user?.profilePicPath ?: "")
    }

    //Log.d("SettingsContent", "currentFilePath $currentFilePath")

    var text by remember {
        mutableStateOf<String>(user?.userName ?: "Nickname")
    }

    var hasNotificationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                applicationContext, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Log.d("SettingsContent", "Notification permission granted")
            sendNotification(applicationContext, "Application can send notifications now", "Notifications enabled")
        } else {
            Log.d("SettingsContent", "Notification permission denied")
        }
    }


    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = PickVisualMedia(),
        onResult = { uri ->



            if (uri != null) {

                //delete old filepath so there is only one pic at the time
                currentFilePath.let { oldFilePath ->
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
                model = currentFilePath.let { File(it) },
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

        if (!hasNotificationPermission){
            item {
                Button(
                    onClick = {
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    },
                ) {
                    Text("Enable notifications")
                }
            }
        }

        item {
            Button(
                onClick = {
                    sendNotification(applicationContext, "Notification from button", "Test Notification")
                }
            ) {
                Text("send notification")
            }
        }


    }


}