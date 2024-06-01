package com.rdapps.weddinginvitation.model

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val userId: String,
    val name: String
)
