package com.rdapps.weddinginvitation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rdapps.weddinginvitation.util.VerticalSpacer

@Composable
fun June28Page() {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        EventItem("Vavisal - Sagai", place = Place.AmarWadi, time = "9:00 AM")

        VerticalSpacer(40)

        EventItem("Lunch", place = Place.AmarWadi, time = "12:00 PM")

        VerticalSpacer(40)

        EventItem("Dandiya Raas", place = Place.AmarWadi, time = "7:00 PM")
    }
}