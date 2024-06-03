package com.rdapps.weddinginvitation.model

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val userId: String? = null,
    val name: String = "",
    val showContactNumber: Boolean = false,
    val showReceptionDetails: Boolean = false,
    val showStayDetails: Boolean = false,
    val from: From = From.Rajdeep,
    val isDown: Boolean = false
)

enum class From {
    Rajdeep,
    Reema
}
