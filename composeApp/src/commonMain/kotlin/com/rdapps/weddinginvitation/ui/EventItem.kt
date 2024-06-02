package com.rdapps.weddinginvitation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.rdapps.weddinginvitation.openUrl
import com.rdapps.weddinginvitation.util.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import wedding_invitation.composeapp.generated.resources.Res
import wedding_invitation.composeapp.generated.resources.external_link
import wedding_invitation.composeapp.generated.resources.round_location_on_24
import wedding_invitation.composeapp.generated.resources.round_time_filled_24

@Composable
fun EventItem(eventName: String, place: Place, time: String) {
    Column(
        modifier = Modifier
    ) {
        val density = LocalDensity.current

        var titleSize by remember {
            mutableStateOf(40.dp)
        }

        Text(
            text = eventName,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.onGloballyPositioned {
                titleSize = with(density) { it.size.width.toDp() }
            }
        )

        HorizontalDivider(modifier = Modifier.width(titleSize))

        VerticalSpacer(10)

        PlaceAndTime(
            place = place,
            time = time
        )
    }
}

enum class Place(val displayName: String, val location: String) {
    AmarWadi(
        "Vannza Ghnati Amarvadi",
        "https://www.google.com/maps/place/shree+vannza+ghnati+amarvadi/@20.7132359,70.9790717,19.21z/data=!4m17!1m10!3m9!1s0x3be31cdd79c175ab:0x87e61d2fef78c05b!2sHotel+Hemal+Garden!5m2!4m1!1i2!8m2!3d20.7137912!4d70.9789062!16s%2Fg%2F1th1sj06!3m5!1s0x3be31cdd7252d8db:0xe60df887c85e898b!8m2!3d20.7129968!4d70.979721!16s%2Fg%2F11fy1_ng18?entry=ttu"
    ),
    KumbharWada(
        "From Kumbhar Wada",
        "https://www.google.com/maps/place/20%C2%B042'47.8%22N+70%C2%B059'02.0%22E/@20.7132679,70.9790101,17z/data=!3m1!4b1!4m13!1m8!3m7!1s0x3be31ce77c7a67bf:0x4664503a0396202!2sDiu,+Dadra+and+Nagar+Haveli+and+Daman+and+Diu+362520!3b1!8m2!3d20.7180924!4d70.9857903!16s%2Fm%2F0h1fl2d!3m3!8m2!3d20.713268!4d70.983881?entry=ttu"
    ),
    BalajiThal(
        "Balaji Thal Banquet",
        "https://www.google.com/maps/place/Balaji+Thal/@22.2984816,70.7688347,19.55z/data=!4m6!3m5!1s0x3959cbe2814dbb79:0x97e8b6533e91567c!8m2!3d22.298531!4d70.769249!16s%2Fg%2F11rkdh6qqq?entry=ttu"
    )
}

@Composable
fun PlaceAndTime(place: Place, time: String) {
    val coroutineScope = rememberCoroutineScope()
    val supabase = LocalSupabaseClient.current
    val userId = LocalUserId.current
    Column {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                    openUrl(place.location)
                    coroutineScope.launch {
                        userId?.let { supabase?.sendEvent("${place.displayName} Clicked", it) }
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconText(
                iconRes = Res.drawable.round_location_on_24,
                text = place.displayName
            )

            HorizontalSpacer(10)

            Icon(
                imageVector = vectorResource(Res.drawable.external_link),
                contentDescription = "link",
                modifier = Modifier.size(18.dp)
            )

            HorizontalSpacer(5)
        }

        VerticalSpacer(10)

        IconText(iconRes = Res.drawable.round_time_filled_24, time)
    }
}

@Composable
fun IconText(iconRes: DrawableResource, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = vectorResource(iconRes),
            contentDescription = "icon",
            modifier = Modifier.size(20.dp)
        )

        HorizontalSpacer(10)

        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge
        )
    }
}