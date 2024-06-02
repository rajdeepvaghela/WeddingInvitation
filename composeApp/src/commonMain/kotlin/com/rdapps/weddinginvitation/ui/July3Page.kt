package com.rdapps.weddinginvitation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun July3Page() {
    Column {
        EventItem("Reception", place = Place.BalajiThal, time = "12:30 PM")
    }
}