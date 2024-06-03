package com.rdapps.weddinginvitation.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String?,
    val name: String,
    val userAgent: String,
    val vendor: String,
    val platform: String,
    val ip: String = "",
    val city: String = "",
    val region: String = "",
    val country: String = "",
    val lat: String = "",
    val lng: String = "",
    val org: String = "",
    val postal: String = "",
    val timezone: String = "",
    val isOverridden: Boolean = false
)