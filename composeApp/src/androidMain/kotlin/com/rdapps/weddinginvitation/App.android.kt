package com.rdapps.weddinginvitation

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    var currentPage by remember {
                        mutableStateOf(AppPage.Kankotri)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        when (currentPage) {
                            AppPage.Kankotri -> {
                                App(
                                    hash = "#eyJuYW1lIjogIlZhZ2hlbGEiLCAic2hvd0NvbnRhY3ROdW1iZXIiOiB0cnVlLCAic2hvd1JlY2VwdGlvbkRldGFpbHMiOiB0cnVlLCAic2hvd1N0YXlEZXRhaWxzIjogdHJ1ZX0=",
                                    sourcePlatform = SourcePlatform.Android
                                )
                            }

                            AppPage.GenerateLink -> {
                                GenerateLinkScreen()
                            }
                        }
                    }
                    Button(onClick = {
                        currentPage = when (currentPage) {
                            AppPage.Kankotri -> {
                                AppPage.GenerateLink
                            }

                            AppPage.GenerateLink -> {
                                AppPage.Kankotri
                            }
                        }
                    }) {
                        Text(
                            text = when (currentPage) {
                                AppPage.Kankotri -> "Generate Link"
                                AppPage.GenerateLink -> "Kankotri"
                            }
                        )
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