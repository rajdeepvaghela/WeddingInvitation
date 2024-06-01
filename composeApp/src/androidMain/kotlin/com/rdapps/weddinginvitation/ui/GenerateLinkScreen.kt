package com.rdapps.weddinginvitation.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.rdapps.weddinginvitation.model.HashData
import com.rdapps.weddinginvitation.util.HorizontalSpacer
import com.rdapps.weddinginvitation.util.VerticalSpacer
import com.rdapps.weddinginvitation.util.createJson
import io.ktor.util.*

@Composable
fun GenerateLinkScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 40.dp)
            .padding(20.dp)
    ) {
        val json = remember {
            createJson()
        }

        var hash by remember {
            mutableStateOf("")
        }

        var url by remember {
            mutableStateOf("https://reedeepwedding.site")
        }

        var name by remember {
            mutableStateOf("")
        }

        var showContactInfo by remember {
            mutableStateOf(true)
        }

        var showStayDetails by remember {
            mutableStateOf(false)
        }

        var showReceptionDetails by remember {
            mutableStateOf(false)
        }

        fun generateHash() {
            val jsonString = if (name.isNotBlank())
                json.encodeToString(
                    HashData.serializer(),
                    HashData(
                        name = name,
                        showContactNumber = showContactInfo,
                        showReceptionDetails = showReceptionDetails,
                        showStayDetails = showStayDetails
                    )
                )
            else
                ""
            if (jsonString.isNotBlank()) {
                hash = jsonString.encodeBase64()
                url = "https://reedeepwedding.site/#$hash"
            } else {
                url = "https://reedeepwedding.site"
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                generateHash()
            },
            label = {
                Text(text = "Name")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            modifier = Modifier.fillMaxWidth()
        )

        VerticalSpacer(heightInDp = 20)

        CheckBoxText(
            text = "Show Contact Info",
            isChecked = showContactInfo,
            onCheckedChange = {
                showContactInfo = it
                generateHash()
            }
        )

        VerticalSpacer(heightInDp = 20)

        CheckBoxText(
            text = "Show Stay Details",
            isChecked = showStayDetails,
            onCheckedChange = {
                showStayDetails = it
                generateHash()
            }
        )

        VerticalSpacer(heightInDp = 20)

        CheckBoxText(
            text = "Show Reception Details",
            isChecked = showReceptionDetails,
            onCheckedChange = {
                showReceptionDetails = it
                generateHash()
            }
        )
        VerticalSpacer(heightInDp = 40)

        Text(text = url)

        VerticalSpacer(heightInDp = 20)

        val context = LocalContext.current

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, url)
                }

                context.startActivity(Intent.createChooser(intent, "Share"))
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Share")
        }
    }
}

@Composable
fun CheckBoxText(text: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text)

        HorizontalSpacer(widthInDp = 20)

        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}