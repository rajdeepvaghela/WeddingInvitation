package com.rdapps.weddinginvitation.ui

import androidx.compose.runtime.*
import com.rdapps.weddinginvitation.mapper.toClientInfo
import com.rdapps.weddinginvitation.model.ClientInfo
import com.rdapps.weddinginvitation.model.DeviceInfo
import com.rdapps.weddinginvitation.model.HashData
import com.rdapps.weddinginvitation.model.IpResponse
import com.rdapps.weddinginvitation.util.createHttpClient
import com.rdapps.weddinginvitation.util.createJson
import com.rdapps.weddinginvitation.util.sendEvent
import com.russhwolf.settings.Settings
import io.github.aakira.napier.log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.util.*
import org.jetbrains.compose.resources.stringResource
import wedding_invitation.composeapp.generated.resources.Res
import wedding_invitation.composeapp.generated.resources.ip_info_token
import wedding_invitation.composeapp.generated.resources.sb_key
import wedding_invitation.composeapp.generated.resources.sb_url

@Composable
fun InitializeAndTrackData(
    hash: String,
    deviceInfo: DeviceInfo,
    onSupabaseInitialized: (SupabaseClient) -> Unit,
    onUserIdFetch: (String) -> Unit,
    onHashDataChange: (HashData) -> Unit
) {
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

    LaunchedEffect(hashData) {
        onHashDataChange(hashData)
    }

    var userId by remember {
        mutableStateOf(settings.getString("id", ""))
    }

    LaunchedEffect(userId) {
        if (userId.isNotBlank())
            onUserIdFetch(userId)
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

    LaunchedEffect(supabase) {
        if (supabase != null)
            onSupabaseInitialized(supabase)
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

                clientInfoResponse.id?.let {
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
                    }.decodeSingleOrNull<HashData>()

                    data?.let {
                        hashData = it
                        settings.putString("config", json.encodeToString(HashData.serializer(), it))
                    }
                } catch (e: Exception) {
                    log { "exception: $e" }
                    e.printStackTrace()
                }
            }
        }
    }
}
