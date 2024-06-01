package com.rdapps.weddinginvitation.model

import kotlinx.serialization.Serializable

@Serializable
data class HashData(
    val userId: String? = null,
    val name: String = "",
    val showContactNumber: Boolean = true,
    val showReceptionDetails: Boolean = true,
    val showStayDetails: Boolean = true,
    val from: From = From.Rajdeep
)

enum class From {
    Rajdeep,
    Reema
}
