package com.rdapps.weddinginvitation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import com.rdapps.weddinginvitation.model.Config
import com.rdapps.weddinginvitation.openUrl
import com.rdapps.weddinginvitation.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import wedding_invitation.composeapp.generated.resources.*

@Composable
fun HomePage(config: Config) {
    BoxWithConstraints {
        val totalWidth = maxWidth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(Res.drawable.ganesh),
                contentDescription = "ganesha",
                modifier = Modifier
                    .padding(20.dp)
                    .width((totalWidth * 30 / 100).coerceAtMost(150.dp)),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
            )

            VerticalSpacer(20)

            if (config.name.isNotBlank()) {
                Text(
                    text = config.name,
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center
                )
            }

            val welcomeText = if (config.name.isBlank())
                stringResource(Res.string.welcome_text_without_name)
            else
                stringResource(Res.string.welcome_text)

            Text(
                text = welcomeText,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            VerticalSpacer(20)

            Text(
                text = "Rajdeep & Reema",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )

            val mrgDate = remember {
                LocalDateTime(
                    LocalDate(2024, Month.JUNE, 30),
                    LocalTime(9, 0, 0)
                )
            }
            var currentTime by remember {
                mutableStateOf(LocalDateTime.now())
            }

            val diff = mrgDate.toMillis() - currentTime.toMillis()
            if (diff > 0) {
                VerticalSpacer(20)

                Text(
                    text = "On 30th June 2024, which is from now",
                    style = MaterialTheme.typography.titleLarge
                )

                VerticalSpacer(10)

                LaunchedEffect(Unit) {
                    launch {
                        while (true) {
                            delay(1000)
                            currentTime = LocalDateTime.now()

                            if (currentTime.compareTo(mrgDate) == 0) {
                                break
                                // TODO: write logic to show the message of congratulations
                            }
                        }
                    }
                }

                val circleWidth = ((totalWidth - 70.dp) / 4).coerceAtMost(120.dp)

                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    val seconds = diff / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60
                    val days = hours / 24

                    val remainingSeconds = seconds % 60
                    val remainingMinutes = minutes % 60
                    val remainingHours = hours % 24

                    CountdownItem(
                        days,
                        "Days",
                        modifier = Modifier.size(circleWidth)
                    )

                    CountdownItem(
                        remainingHours,
                        "Hours",
                        modifier = Modifier.size(circleWidth)
                    )

                    CountdownItem(
                        remainingMinutes,
                        "Minutes",
                        modifier = Modifier.size(circleWidth)
                    )

                    CountdownItem(
                        remainingSeconds,
                        "Seconds",
                        modifier = Modifier.size(circleWidth)
                    )

                }
            } else {
                VerticalSpacer(20)

                Text(
                    text = "On 30th June 2024",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            VerticalSpacer(20)

            val coroutineScope = rememberCoroutineScope()
            val supabase = LocalSupabaseClient.current

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val calendarEventUrl = stringResource(Res.string.calendar_event_url)
                Button(
                    onClick = {
                        openUrl(calendarEventUrl)
                        coroutineScope.launch {
                            config.userId?.let { supabase?.sendEvent("Add to Calendar Clicked", it) }
                        }
                    }
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.round_calendar_month_24),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    HorizontalSpacer(10)
                    Text("Add to Calendar", style = MaterialTheme.typography.bodyLarge)
                }

                val venueUrl = stringResource(Res.string.venue_url)
                Button(
                    onClick = {
                        openUrl(venueUrl)
                        coroutineScope.launch {
                            config.userId?.let { supabase?.sendEvent("Venue Clicked", it) }
                        }
                    }
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.round_location_on_24),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    HorizontalSpacer(10)
                    Text("Marriage Venue", style = MaterialTheme.typography.bodyLarge)
                }
            }

            if (config.showStayDetails || config.showContactNumber) {
                VerticalSpacer(12)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (config.showStayDetails) {
                        val stayUrl = stringResource(Res.string.stay_url)
                        Button(
                            onClick = {
                                openUrl(stayUrl)
                                coroutineScope.launch {
                                    config.userId?.let { supabase?.sendEvent("Stay Clicked", it) }
                                }
                            }
                        ) {
                            Icon(
                                vectorResource(Res.drawable.round_location_on_24),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            HorizontalSpacer(10)
                            Text("Your Stay", style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    if (config.showContactNumber) {
                        Button(
                            onClick = {
                                openUrl("tel:+919426785076")
                                coroutineScope.launch {
                                    config.userId?.let { supabase?.sendEvent("Call Clicked", it) }
                                }
                            }
                        ) {
                            Icon(
                                vectorResource(Res.drawable.round_call_24),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            HorizontalSpacer(10)
                            Text("Contact", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            VerticalSpacer(12)

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        openUrl("../kankotri.pdf")
                        coroutineScope.launch {
                            config.userId?.let { supabase?.sendEvent("Gujarati Kankotri Clicked", it) }
                        }
                    }
                ) {
                    Icon(
                        vectorResource(Res.drawable.om_symbol),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    HorizontalSpacer(10)
                    Text(
                        text = "Gujarati Kankotri",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (config.showReceptionDetails) {
                    Button(
                        onClick = {
                            openUrl("../reception-card.pdf")
                            coroutineScope.launch {
                                config.userId?.let { supabase?.sendEvent("Reception Card Clicked", it) }
                            }
                        }
                    ) {
                        Icon(
                            vectorResource(Res.drawable.party_horn),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        HorizontalSpacer(10)
                        Text(
                            text = "Reception Card",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

        }
    }
}