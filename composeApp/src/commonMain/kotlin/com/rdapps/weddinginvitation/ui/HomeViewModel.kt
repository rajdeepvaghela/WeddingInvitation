package com.rdapps.weddinginvitation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rdapps.weddinginvitation.BuildKonfig
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
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.decodeBase64String
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val json by lazy {
        createJson()
    }

    private val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = BuildKonfig.sbUrl,
            supabaseKey = BuildKonfig.sbKey
        ) {
            defaultSerializer = KotlinXSerializer(json)

            install(Postgrest)
        }
    }

    private val settings by lazy {
        Settings()
    }

    private val nwClient by lazy {
        createHttpClient(json)
    }

    private val mutableConfigFlow = MutableStateFlow(Config())
    val configFlow = mutableConfigFlow.asStateFlow()

    init {

    }

    fun init(hash: String, deviceInfo: DeviceInfo) = viewModelScope.launch {
        val decodedHash = hash.removePrefix("#").decodeBase64String()
        val decodedConfig = try {
            json.decodeFromString(Config.serializer(), decodedHash)
        } catch (e: Exception) {
            Config()
        }

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
                nwClient.get("https://ipinfo.io/?token=${BuildKonfig.ipInfoToken}")
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

            userResponse.id ?: return@launch

            supabase.sendEvent("Visited", userResponse.id)

            val newConfig = decodedConfig.copy(userId = userResponse.id)

            try {
                val config = supabase.from("config").insert(newConfig) {
                    select()
                }.decodeSingle<Config>()

                mutableConfigFlow.update {
                    config
                }

                settings.putString("config", json.encodeToString(Config.serializer(), config))
            } catch (e: Exception) {
                e.printStackTrace()
                log { "Exception: $e" }
            }

        } else {
            // return uuid of user
            val userId = localConfig?.userId ?: return@launch

            supabase.sendEvent("Visited", userId)

            // fetch new config data
            try {
                val newConfig = supabase.from("config").select {
                    filter {
                        eq("userId", userId)
                    }
                }.decodeSingleOrNull<Config>() ?: return@launch

                mutableConfigFlow.update {
                    newConfig
                }
                settings.putString("config", json.encodeToString(Config.serializer(), newConfig))
            } catch (e: Exception) {
                log { "exception: $e" }
                e.printStackTrace()
            }
        }

        if (settings.hasKey("id"))
            settings.remove("id")
    }

    private fun getConfigFromSettings(): Config? {
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
}