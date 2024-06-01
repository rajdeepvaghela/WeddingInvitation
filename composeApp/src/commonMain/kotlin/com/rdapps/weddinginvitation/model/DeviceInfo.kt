package com.rdapps.weddinginvitation.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    val userAgent: String = "",
    val vendor: String = "",
    val platform: String = ""
)
