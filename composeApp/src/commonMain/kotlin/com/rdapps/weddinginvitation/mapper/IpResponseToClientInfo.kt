package com.rdapps.weddinginvitation.mapper

import com.rdapps.weddinginvitation.model.User
import com.rdapps.weddinginvitation.model.IpResponse

fun IpResponse.toUser(
    id: String? = null,
    name: String,
    userAgent: String,
    vendor: String,
    platform: String
) = User(
    id = id,
    name = name,
    userAgent = userAgent,
    vendor = vendor,
    platform = platform,
    ip = this.ip,
    city = this.city,
    region = this.region,
    country = this.country,
    lat = this.lat,
    lng = this.lng,
    org = this.org,
    postal = this.postal,
    timezone = this.timezone
)
