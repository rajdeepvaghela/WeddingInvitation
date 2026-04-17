package com.rdapps.weddinginvitation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rdapps.weddinginvitation.model.DeviceInfo
import com.rdapps.weddinginvitation.model.Page
import com.rdapps.weddinginvitation.model.SourcePlatform
import com.rdapps.weddinginvitation.theme.AppTheme
import com.rdapps.weddinginvitation.ui.BlockScreen
import com.rdapps.weddinginvitation.ui.HomePage
import com.rdapps.weddinginvitation.ui.HomeViewModel
import com.rdapps.weddinginvitation.ui.InitializeAndTrackData
import com.rdapps.weddinginvitation.ui.July3Page
import com.rdapps.weddinginvitation.ui.June28Page
import com.rdapps.weddinginvitation.ui.June29Page
import com.rdapps.weddinginvitation.ui.June30Page
import com.rdapps.weddinginvitation.util.LocalSupabaseClient
import com.rdapps.weddinginvitation.util.LocalUserId
import io.github.jan.supabase.SupabaseClient
import org.jetbrains.compose.resources.painterResource
import wedding_invitation.composeapp.generated.resources.Res
import wedding_invitation.composeapp.generated.resources.design
import wedding_invitation.composeapp.generated.resources.red_bg_1
import wedding_invitation.composeapp.generated.resources.red_bg_2

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun App(
    hash: String = "",
    deviceInfo: DeviceInfo = DeviceInfo(),
    sourcePlatform: SourcePlatform = SourcePlatform.Web
) = AppTheme {

    val viewModel = viewModel<HomeViewModel>()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .systemBarsPadding()
    ) {
        Image(
            painter = painterResource(
                when (sourcePlatform) {
                    SourcePlatform.Android -> Res.drawable.red_bg_1
                    SourcePlatform.Web -> Res.drawable.red_bg_2
                }
            ),
            contentDescription = "background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        var supabase by remember {
            mutableStateOf<SupabaseClient?>(null)
        }

        val config by viewModel.configFlow.collectAsState()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            var selectedPage by remember {
                mutableStateOf(Page.Home)
            }

            if (sourcePlatform == SourcePlatform.Web) {
                viewModel.init(
                    hash = hash,
                    deviceInfo = deviceInfo
                )
            }

//            InitializeAndTrackData(
//                hash = hash,
//                deviceInfo = deviceInfo,
//                onSupabaseInitialized = {
//                    supabase = it
//                },
//                onHashDataChange = {
//                    config = it
//                },
//                sourcePlatform = sourcePlatform
//            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                contentAlignment = Alignment.Center
            ) {
                val density = LocalDensity.current

                val size by derivedStateOf {
                    with(density) {
                        (this@BoxWithConstraints.constraints.maxWidth * 40f / 100f).toDp()
                    }.coerceAtMost(600.dp)
                }

                Image(
                    painter = painterResource(Res.drawable.design),
                    contentDescription = "design",
                    modifier = Modifier.align(Alignment.TopStart)
                        .size(size)
                        .offset(x = -(size / 2), y = -(size / 2))
                )

                Image(
                    painter = painterResource(Res.drawable.design),
                    contentDescription = "design",
                    modifier = Modifier.align(Alignment.TopEnd)
                        .size(size)
                        .offset(x = size / 2, y = -(size / 2))
                )

                Image(
                    painter = painterResource(Res.drawable.design),
                    contentDescription = "design",
                    modifier = Modifier.align(Alignment.BottomStart)
                        .size(size)
                        .offset(x = -(size / 2), y = size / 2)
                )

                Image(
                    painter = painterResource(Res.drawable.design),
                    contentDescription = "design",
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .size(size)
                        .offset(x = size / 2, y = size / 2)
                )

                CompositionLocalProvider(
                    LocalSupabaseClient provides supabase,
                    LocalUserId provides config.userId
                ) {
                    AnimateIf(selectedPage == Page.Home) {
                        HomePage(config = config)
                    }

                    AnimateIf(selectedPage == Page.June28) {
                        June28Page()
                    }

                    AnimateIf(selectedPage == Page.June29) {
                        June29Page()
                    }

                    AnimateIf(selectedPage == Page.June30) {
                        June30Page()
                    }

                    AnimateIf(selectedPage == Page.July3) {
                        July3Page()
                    }
                }
            }

            val modifier = if (this@BoxWithConstraints.maxWidth > 600.dp) {
                // desktop
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 10.dp)
                    .shadow(elevation = 10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(24.dp)
                    )
            } else {
                // mobile
                Modifier.fillMaxWidth()
                    .shadow(elevation = 10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            }

            FlowRow(
                modifier = modifier
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            ) {
                MenuItem(isSelected = selectedPage == Page.Home, text = "Home") {
                    selectedPage = Page.Home
                }

                MenuItem(isSelected = selectedPage == Page.June28, text = "28 June") {
                    selectedPage = Page.June28
                }

                MenuItem(isSelected = selectedPage == Page.June29, text = "29 June") {
                    selectedPage = Page.June29
                }

                MenuItem(isSelected = selectedPage == Page.June30, text = "30 June") {
                    selectedPage = Page.June30
                }

                if (config.showReceptionDetails) {
                    MenuItem(isSelected = selectedPage == Page.July3, text = "3 July") {
                        selectedPage = Page.July3
                    }
                }
            }
        }

        if (config.isDown) {
            BlockScreen()
        }
    }
}

@Composable
fun BoxScope.AnimateIf(condition: Boolean, content: @Composable BoxScope.() -> Unit) {
    AnimatedVisibility(
        visible = condition,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut(animationSpec = tween(500)),
        modifier = Modifier
            .align(Alignment.Center)
            .fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.Center, content = content)
    }
}

@Composable
fun MenuItem(
    isSelected: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        shape = CircleShape,
        modifier = modifier
    )
}


internal expect fun openUrl(url: String?)