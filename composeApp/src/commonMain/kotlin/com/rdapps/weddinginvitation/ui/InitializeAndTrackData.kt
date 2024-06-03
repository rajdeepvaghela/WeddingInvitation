package com.rdapps.weddinginvitation.ui

import androidx.compose.runtime.*
import com.rdapps.weddinginvitation.mapper.toUser
import com.rdapps.weddinginvitation.model.Config
import com.rdapps.weddinginvitation.model.DeviceInfo
import com.rdapps.weddinginvitation.model.IpResponse
import com.rdapps.weddinginvitation.model.User
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
    onHashDataChange: (Config) -> Unit
) {
    val json = remember {
        createJson()
    }

    val decodedHash = hash.removePrefix("#").decodeBase64String()
    val decodedConfig = try {
        json.decodeFromString(Config.serializer(), decodedHash)
    } catch (e: Exception) {
        Config()
    }

    val settings = remember {
        Settings()
    }

    fun getConfigFromSettings(): Config? {
        if (!settings.hasKey("config"))
            return null

        settings.getStringOrNull("config")?.let {
            if (it.isNotBlank()) {
                return json.decodeFromString(Config.serializer(), it)
            } else {
                return null
            }
        }

        return null
    }

    var config by remember {
        val storedConfig = getConfigFromSettings()

        mutableStateOf(
            if (storedConfig != null && (storedConfig.name == decodedConfig.name || decodedConfig.name.isBlank()))
                storedConfig
            else
                decodedConfig
        )
    }

    LaunchedEffect(config) {
        onHashDataChange(config)
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
            var localConfig = getConfigFromSettings()

            if (decodedConfig.name.isNotBlank() && localConfig?.name != decodedConfig.name) {
                localConfig?.userId?.let { userId ->
                    supabase.from("users").update(
                        {
                            set("isOverridden", true)
                        }
                    ) {
                        filter {
                            eq("id", userId)
                        }
                    }

                    settings.remove("config")
                    localConfig = null
                }
            }

            if (localConfig == null) {
                // fetch details and create user

                val ipResponse = try {
                    client.get("https://ipinfo.io/?token=$ipInfoToken")
                        .body<IpResponse>()
                } catch (e: Exception) {
                    log { "Exception: $e" }
                    IpResponse()
                }

                val user = ipResponse.toUser(
                    name = decodedConfig.name,
                    userAgent = deviceInfo.userAgent,
                    vendor = deviceInfo.vendor,
                    platform = deviceInfo.platform
                )

                val userResponse = supabase.from("users").insert(user) {
                    select()
                }.decodeSingle<User>()

                userResponse.id ?: return@let

                supabase.sendEvent("Visited", userResponse.id)

                val newConfig = decodedConfig.copy(userId = userResponse.id)

                try {
                    supabase.from("config").insert(newConfig)
                    settings.putString("config", json.encodeToString(Config.serializer(), newConfig))
                } catch (e: Exception) {
                    e.printStackTrace()
                    log { "Exception: $e" }
                }

            } else {
                // return uuid of user
                val userId = localConfig?.userId ?: return@let

                supabase.sendEvent("Visited", userId)

                // fetch new config data
                try {
                    val newConfig = supabase.from("config").select {
                        filter {
                            eq("userId", userId)
                        }
                    }.decodeSingleOrNull<Config>() ?: return@let

                    config = newConfig
                    settings.putString("config", json.encodeToString(Config.serializer(), newConfig))
                } catch (e: Exception) {
                    log { "exception: $e" }
                    e.printStackTrace()
                }
            }

            if (settings.hasKey("id"))
                settings.remove("id")
        }
    }
}
