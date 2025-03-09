package com.mobilecomputing.ui.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun ApiScreenContent(
    innerPaddingValues: PaddingValues,
    applicationContext: Context
) {
    var factText by remember { mutableStateOf("Press the button to get a random fact!") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(top = innerPaddingValues.calculateTopPadding())
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = factText,
            modifier = Modifier
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    factText = getFact() ?: "Failed to load fact."
                }
                      },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Get Fact")
        }

    }
}

suspend fun getFact(): String? {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://uselessfacts.jsph.pl/random.json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    val jsonObject = JSONObject(jsonData ?: "{}")
                    jsonObject.optString("text", "No fact found")
                } else {
                    "Error: ${response.message}"
                }
            }
        } catch (e: Exception) {
            "Exception: ${e.message}"
        }
    }
}

