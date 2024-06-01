package com.rdapps.weddinginvitation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import com.rdapps.weddinginvitation.mapper.toClientInfo
import com.rdapps.weddinginvitation.model.ClientInfo
import com.rdapps.weddinginvitation.model.DeviceInfo
import com.rdapps.weddinginvitation.model.HashData
import com.rdapps.weddinginvitation.model.IpResponse
import com.rdapps.weddinginvitation.theme.AppTheme
import com.rdapps.weddinginvitation.ui.*
import com.rdapps.weddinginvitation.util.createHttpClient
import com.rdapps.weddinginvitation.util.createJson
import com.rdapps.weddinginvitation.util.sendEvent
import com.russhwolf.settings.Settings
import io.github.aakira.napier.log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import wedding_invitation.composeapp.generated.resources.*

enum class Page {
    Home,
    June28,
    June29,
    June30,
    July3
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun App(hash: String = "", deviceInfo: DeviceInfo = DeviceInfo()) = AppTheme {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .systemBarsPadding()
    ) {
        Image(
            painter = painterResource(Res.drawable.red_bg_2),
            contentDescription = "background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val coroutineScope = rememberCoroutineScope()

            var selectedPage by remember {
                mutableStateOf(Page.Home)
            }

            val json = remember {
                createJson()
            }

            val decodedHash = hash.removePrefix("#").decodeBase64String()
            val decodedHashData = try {
                json.decodeFromString(HashData.serializer(), decodedHash)
            } catch (e: Exception) {
                HashData()
            }

            val settings = remember {
                Settings()
            }

            var hashData by remember {
                mutableStateOf(
                    settings.getStringOrNull("config")?.let {
                        if (it.isNotBlank())
                            json.decodeFromString(HashData.serializer(), it)
                        else
                            decodedHashData
                    } ?: decodedHashData
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                contentAlignment = Alignment.Center
            ) {
                var userId by remember {
                    mutableStateOf(settings.getString("id", ""))
                }

                val supabaseUrl = stringResource(Res.string.sb_url)
                val supabaseKey = stringResource(Res.string.sb_key)

                val supabase = remember(supabaseUrl, supabaseKey) {
                    if (supabaseUrl.isNotBlank() && supabaseKey.isNotBlank()) {
                        createSupabaseClient(
                            supabaseUrl = supabaseUrl,
                            supabaseKey = supabaseKey
                        ) {
                            defaultSerializer = KotlinXSerializer(json)

                            install(Postgrest)
                        }
                    } else
                        null
                }

                val client = remember {
                    createHttpClient(json)
                }

                val ipInfoToken = stringResource(Res.string.ip_info_token)

                LaunchedEffect(supabase) {
                    supabase?.let { supabase ->
                        if (!settings.hasKey("id")) {
                            // fetch details and create user

                            val ipResponse = try {
                                client.get("https://ipinfo.io/?token=$ipInfoToken")
                                    .body<IpResponse>()
                            } catch (e: Exception) {
                                log { "Exception: $e" }
                                IpResponse()
                            }

                            val clientInfo = ipResponse.toClientInfo(
                                name = decodedHashData.name,
                                userAgent = deviceInfo.userAgent,
                                vendor = deviceInfo.vendor,
                                platform = deviceInfo.platform
                            )

                            val clientInfoResponse = supabase.from("users").insert(clientInfo) {
                                select()
                            }.decodeSingle<ClientInfo>()

                            clientInfoResponse?.id?.let {
                                settings.putString("id", it)
                                userId = it
                            }

                            supabase.sendEvent("Visited", userId)

                            val data = decodedHashData.copy(userId = userId)

                            try {
                                supabase.from("config").insert(data)
                                settings.putString("config", json.encodeToString(HashData.serializer(), data))
                            } catch (e: Exception) {
                                e.printStackTrace()
                                log { "Exception: $e" }
                            }

                        } else {
                            // return uuid of user
                            userId = settings.getString("id", "")

                            supabase.sendEvent("Visited", userId)
                        }
                    }
                }

                LaunchedEffect(userId, supabase) {
                    supabase?.let { supabase ->
                        if (userId.isNotBlank()) {
                            try {
                                val data = supabase.from("config").select {
                                    filter {
                                        eq("userId", userId)
                                    }
                                }.decodeSingle<HashData>()

                                hashData = data

                                settings.putString("config", json.encodeToString(HashData.serializer(), data))
                            } catch (e: Exception) {
                                log { "exception: $e" }
                                e.printStackTrace()
                            }
                        }
                    }
                }

                val density = LocalDensity.current

                val size by derivedStateOf {
                    with(density) {
                        (this@BoxWithConstraints.constraints.maxWidth * 40f / 100f).toDp()
                    }.coerceAtMost(600.dp)
                }

                Image(
                    painter = painterResource(Res.drawable.design),
                    contentDescription = "design",
                    modifier = Modifier.align(Alignment.TopStart)
                        .size(size)
                        .offset(x = -(size / 2), y = -(size / 2))
                )

                Image(
                    painter = painterResource(Res.drawable.design),
                    contentDescription = "design",
                    modifier = Modifier.align(Alignment.TopEnd)
                        .size(size)
                        .offset(x = size / 2, y = -(size / 2))
                )

                Image(
                    painter = painterResource(Res.drawable.design),
                    contentDescription = "design",
                    modifier = Modifier.align(Alignment.BottomStart)
                        .size(size)
                        .offset(x = -(size / 2), y = size / 2)
                )

                Image(
                    painter = painterResource(Res.drawable.design),
                    contentDescription = "design",
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .size(size)
                        .offset(x = size / 2, y = size / 2)
                )

                AnimateIf(selectedPage == Page.Home) {
                    HomePage(
                        hashData = hashData,
                        onAddToCalendar = {
                            coroutineScope.launch {
                                supabase?.sendEvent("Add to Calendar Clicked", userId)
                            }
                        },
                        onVenueClicked = {
                            coroutineScope.launch {
                                supabase?.sendEvent("Venue Clicked", userId)
                            }
                        },
                        onStayClicked = {
                            coroutineScope.launch {
                                supabase?.sendEvent("Stay Clicked", userId)
                            }
                        },
                        onCallClicked = {
                            coroutineScope.launch {
                                supabase?.sendEvent("Call Clicked", userId)
                            }
                        },
                        onDownloadKankotri = {
                            coroutineScope.launch {
                                supabase?.sendEvent("Gujarati Kankotri Clicked", userId)
                            }
                        }
                    )
                }

                AnimateIf(selectedPage == Page.June28) {
                    June28Page()
                }

                AnimateIf(selectedPage == Page.June29) {
                    June29Page()
                }

                AnimateIf(selectedPage == Page.June30) {
                    June30Page()
                }

                AnimateIf(selectedPage == Page.July3) {
                    July3Page()
                }
            }

            val modifier = if (this@BoxWithConstraints.maxWidth > 600.dp) {
                // desktop
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp)
                    .shadow(elevation = 10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(24.dp)
                    )
            } else {
                // mobile
                Modifier.fillMaxWidth()
                    .shadow(elevation = 10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            }

            FlowRow(
                modifier = modifier
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            ) {
                MenuItem(isSelected = selectedPage == Page.Home, text = "Home") {
                    selectedPage = Page.Home
                }

                MenuItem(isSelected = selectedPage == Page.June28, text = "28 June") {
                    selectedPage = Page.June28
                }

                MenuItem(isSelected = selectedPage == Page.June29, text = "29 June") {
                    selectedPage = Page.June29
                }

                MenuItem(isSelected = selectedPage == Page.June30, text = "30 June") {
                    selectedPage = Page.June30
                }

                if (hashData.showReceptionDetails) {
                    MenuItem(isSelected = selectedPage == Page.July3, text = "3 July") {
                        selectedPage = Page.July3
                    }
                }
            }
        }
    }
}

@Composable
fun BoxScope.AnimateIf(condition: Boolean, content: @Composable BoxScope.() -> Unit) {
    AnimatedVisibility(
        visible = condition,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500)),
        modifier = Modifier
            .align(Alignment.Center)
            .fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.Center, content = content)
    }
}

@Composable
fun MenuItem(isSelected: Boolean, text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        shape = CircleShape,
        modifier = modifier
    )
}


internal expect fun openUrl(url: String?)