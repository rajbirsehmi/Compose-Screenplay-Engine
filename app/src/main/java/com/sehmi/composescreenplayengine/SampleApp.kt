package com.sehmi.composescreenplayengine

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Sample Compose UI for end-to-end Screenplay verification featuring explicit TestTags.
 */
@Composable
fun SampleLoginScreen() {
    var username by remember { mutableStateOf("") }
    var loggedIn by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (!loggedIn) {
                Text(text = "Please Login", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_username")
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { if (username.isNotBlank()) loggedIn = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_login")
                ) {
                    Text("Submit")
                }
            } else {
                Text(
                    text = "Welcome, $username!",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.testTag("txt_welcome")
                )
            }
        }
    }
}
