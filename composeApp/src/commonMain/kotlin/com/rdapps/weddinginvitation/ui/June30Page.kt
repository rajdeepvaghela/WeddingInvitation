package com.rdapps.weddinginvitation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.rdapps.weddinginvitation.util.VerticalSpacer

@Composable
fun June30Page() {
    Column {
        EventItem("Var Yatra", place = Place.KumbharWada, time = "9:00 AM")

        VerticalSpacer(40)

        EventItem("Hast Melap", place = Place.AmarWadi, time = "11:30 AM")

        VerticalSpacer(40)

        EventItem("Lunch", place = Place.AmarWadi, time = "12:00 PM")
    }
}