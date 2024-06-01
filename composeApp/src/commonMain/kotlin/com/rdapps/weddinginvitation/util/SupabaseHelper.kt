package com.rdapps.weddinginvitation.util

import com.rdapps.weddinginvitation.model.Event
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

suspend fun SupabaseClient.sendEvent(name: String, userId: String) {
    from("events").insert(
        Event(
            userId = userId,
            name = name
        )
    )
}