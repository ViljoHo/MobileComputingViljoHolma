package com.mobilecomputing.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.mobilecomputing.data.Note
import com.mobilecomputing.data.User
import java.io.File

data class Message(val author: String, val body: String)



//: R.drawable.profile_picture
@Composable
fun MessageCard(note: Note, user: User?) {
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
                text = user?.userName ?: note.author,
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
                    text = note.body,
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

}

@Composable
fun Conversation(
    message: List<Message>,
    innerPaddingValues: PaddingValues,
    user: User?,
    notes: List<Note>?,
    addNote: (Note) -> Unit
) {

    var newNote by remember {
        mutableStateOf<String>("new note...")
    }

    Row(modifier = Modifier.padding(top = innerPaddingValues.calculateTopPadding())) {
        LazyColumn {
            item {
                TextField(
                    value = newNote,
                    onValueChange = { newText ->
                        newNote = newText
                         },
                    modifier = Modifier.padding(all=8.dp)
                )
            }
            item {
                Button(
                    onClick = {
                        addNote(Note(0, user?.userName ?: "Nickname", newNote))
                    }
                ) {
                    Text("Add new note")
                }
            }
            if (notes.isNullOrEmpty()) {
                item {
                    Text("No notes available", modifier = Modifier.padding(16.dp))
                }
            } else {
                items(notes) { note ->
                    MessageCard(note, user)
                }
            }
        }

    }
}