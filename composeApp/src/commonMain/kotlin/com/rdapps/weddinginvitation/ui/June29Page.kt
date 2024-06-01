package com.rdapps.weddinginvitation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.rdapps.weddinginvitation.util.VerticalSpacer

@Composable
fun June29Page() {
    Column {
        EventItem("Mandvo", place = Place.AmarWadi, time = "9:00 AM")

        VerticalSpacer(40)

        EventItem("Lunch", place = Place.AmarWadi, time = "12:00 PM")

        VerticalSpacer(40)

        EventItem("Pithi", place = Place.AmarWadi, time = "3:00 PM")
    }
}