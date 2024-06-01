package com.rdapps.weddinginvitation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rdapps.weddinginvitation.util.VerticalSpacer

@Composable
fun July3Page() {
    Column {
        EventItem("Reception", place = Place.BalajiThal, time = "12:30 PM")
    }
}