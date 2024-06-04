package com.rdapps.weddinginvitation.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.rdapps.weddinginvitation.R
import com.rdapps.weddinginvitation.model.Config
import com.rdapps.weddinginvitation.util.HorizontalSpacer
import com.rdapps.weddinginvitation.util.VerticalSpacer
import com.rdapps.weddinginvitation.util.createJson
import io.ktor.util.*
import java.io.File
import java.net.URLEncoder

@Composable
fun GenerateLinkScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        val context = LocalContext.current

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

        var number by remember {
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
                    Config.serializer(),
                    Config(
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

        val request = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { data ->

                    val projection = arrayOf(CommonDataKinds.Phone.NUMBER, CommonDataKinds.Phone.DISPLAY_NAME)
                    val cursor = context.contentResolver.query(data, projection, null, null, null)

                    if (cursor != null && cursor.moveToFirst()) {
                        val numberIndex = cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER)
                        val nameIndex = cursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME)
                        number = cursor.getString(numberIndex)
                        name = cursor.getString(nameIndex)
                        generateHash()
                        cursor.close()
                    }
                }
            }
        }

        Button(
            onClick = {
                request.launch(
                    Intent(Intent.ACTION_PICK).apply {
                        type = CommonDataKinds.Phone.CONTENT_TYPE
                    }
                )
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Pick Contact")
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
        VerticalSpacer(heightInDp = 20)

        Text(text = url, style = MaterialTheme.typography.bodyLarge)

        VerticalSpacer(heightInDp = 20)

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, url)
                    }

                    context.startActivity(Intent.createChooser(intent, "Share"))
                }
            ) {
                Text(text = "Share")
            }

            HorizontalSpacer(widthInDp = 20)

            Button(
                onClick = {
                    val file = File(context.externalCacheDir!!.absolutePath, "kankotri_wedding_invite.pdf")

                    if (!file.exists()) {
                        context.resources.openRawResource(R.raw.kankotri_new).use { inputStream ->
                            inputStream.copyTo(file.outputStream())
                        }
                    }

                    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_TEXT, url)
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    context.startActivity(Intent.createChooser(intent, "Share"))
                }
            ) {
                Text(text = "Share Pdf")
            }
        }

        VerticalSpacer(heightInDp = 40)

        OutlinedTextField(
            value = number,
            onValueChange = {
                number = it
            },
            label = {
                Text(text = "Number")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier.fillMaxWidth()
        )

        VerticalSpacer(heightInDp = 20)

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = {

//                val intent = Intent(Intent.ACTION_VIEW).apply {
//                    val waUrl = "https://api.whatsapp.com/send?phone=$number&text=" +
//                            URLEncoder.encode(url, "UTF-8")
//                    `package` = "com.whatsapp"
//                    data = Uri.parse(waUrl)
//                    putExtra(Intent.EXTRA_STREAM, uri)
//                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                }

//                val intent = Intent(Intent.ACTION_SENDTO).apply {
//                    data = Uri.parse("smsto:$number")
//                    `package` = "com.whatsapp"
//                    putExtra(Intent.EXTRA_TEXT, url)
//                    putExtra(Intent.EXTRA_SUBJECT, url)
//                    putExtra(Intent.EXTRA_STREAM, uri)
//                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                }

                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/$number?text=${URLEncoder.encode(url, "UTF-8")}")
                        putExtra(Intent.EXTRA_TEXT, url)
                        putExtra(Intent.EXTRA_HTML_TEXT, url)
                        `package` = "com.whatsapp"
                    }

                    context.startActivity(intent)
                }
            ) {
                Text(text = "Whatsapp Link")
            }

            HorizontalSpacer(widthInDp = 20)

            Button(
                onClick = {

                    val file = File(context.externalCacheDir!!.absolutePath, "kankotri.pdf")

                    if (!file.exists()) {
                        context.resources.openRawResource(R.raw.kankotri).use { inputStream ->
                            inputStream.copyTo(file.outputStream())
                        }
                    }

                    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_TEXT, url)
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        `package` = "com.whatsapp"
                    }

                    context.startActivity(intent)
                }
            ) {
                Text(text = "Whatsapp Pdf")
            }
        }

        Button(
            onClick = {

                val file = File(context.externalCacheDir!!.absolutePath, "reception_card.pdf")

                if (!file.exists()) {
                    context.resources.openRawResource(R.raw.reception_card).use { inputStream ->
                        inputStream.copyTo(file.outputStream())
                    }
                }

                val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_TEXT, url)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                context.startActivity(intent)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Share Reception Card")
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

@Preview(showBackground = true)
@Composable
fun GenerateLinkScreenPreview() {
    GenerateLinkScreen()
}