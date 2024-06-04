package com.rdapps.weddinginvitation

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rdapps.weddinginvitation.model.SourcePlatform
import com.rdapps.weddinginvitation.ui.GenerateLinkScreen

class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}

enum class AppPage {
    Kankotri, GenerateLink
}

private const val TAG = "App.android"

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.d(TAG, "onCreate: ${intent.data}")

        setContent {
            Surface(
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .imePadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    var currentPage by remember {
                        mutableStateOf(AppPage.Kankotri)
                    }

                    var hash by remember {
                        mutableStateOf("")
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        when (currentPage) {
                            AppPage.Kankotri -> {
                                App(
                                    hash = hash,
                                    sourcePlatform = SourcePlatform.Android
                                )
                            }

                            AppPage.GenerateLink -> {
                                GenerateLinkScreen()
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                currentPage = when (currentPage) {
                                    AppPage.Kankotri -> {
                                        AppPage.GenerateLink
                                    }

                                    AppPage.GenerateLink -> {
                                        AppPage.Kankotri
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = when (currentPage) {
                                    AppPage.Kankotri -> "Generate Link"
                                    AppPage.GenerateLink -> "Kankotri"
                                }
                            )
                        }

                        var showDialog by remember {
                            mutableStateOf(false)
                        }

                        Button(
                            onClick = {
                                showDialog = true
                            }
                        ) {
                            Text(
                                text = "Preview Link"
                            )
                        }

                        if (showDialog)
                            DialogWithImage(
                                onDismissRequest = { showDialog = false },
                                onConfirmation = {
                                    hash = it.split("#")[1]
                                    Log.d(TAG, "onCreate: hash: $hash")
                                    showDialog = false
                                }
                            )
                    }
                }
            }
        }
    }
}

@Composable
fun DialogWithImage(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var url by remember {
                    mutableStateOf("")
                }

                OutlinedTextField(value = url, onValueChange = { url = it })

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirmation(url) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}

internal actual fun openUrl(url: String?) {
    val uri = url?.let { Uri.parse(it) } ?: return
    val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = uri
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    AndroidApp.INSTANCE.startActivity(intent)
}