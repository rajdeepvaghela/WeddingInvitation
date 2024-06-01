package com.rdapps.weddinginvitation.util

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSpacer(heightInDp: Int, modifier: Modifier = Modifier) {
    Spacer(modifier.height(heightInDp.dp))
}

@Composable
fun HorizontalSpacer(widthInDp: Int, modifier: Modifier = Modifier) {
    Spacer(modifier.width(widthInDp.dp))
}