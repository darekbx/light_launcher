package com.darekbx.lightlauncher.ui.userapplications

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.PIXEL_6A
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.lightlauncher.di.appModule
import com.darekbx.lightlauncher.di.viewModelModule
import com.darekbx.lightlauncher.system.ActivityStarter
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.Loading
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AllApplicationsListCloud(
    userApplicationsViewModel: UserApplicationsViewModel = koinViewModel(),
    activityStarter: ActivityStarter = koinInject(),
    onSettingsClick: () -> Unit = { },
    onStatisticsClick: () -> Unit = { },
    onArrowClick: () -> Unit = { }
) {
    val state by userApplicationsViewModel.uiState

    LaunchedEffect(Unit) {
        userApplicationsViewModel.loadAllApplications()
    }

    if (state is UserApplicationsUiState.Idle) {
        return Loading()
    }

    val applications = (state as UserApplicationsUiState.Done).applications

    ApplicationsListCloud(
        applications = applications,
        arrowView = { ArrowLeft(onArrowClick) },
        onSettingsClick = onSettingsClick,
        onStatisticsClick = onStatisticsClick,
        onRefreshClick = { userApplicationsViewModel.loadAllApplications() },
        onAppClick = {
            userApplicationsViewModel.increaseClickCount(it)
            activityStarter.startApplication(it)
        },
        onAppLongClick = { activityStarter.openSettings(it) },
        shouldGoBack = { onArrowClick() }
    )
}

@Composable
fun UserApplicationsListCloud(
    userApplicationsViewModel: UserApplicationsViewModel = koinViewModel(),
    activityStarter: ActivityStarter = koinInject(),
    onArrowClick: () -> Unit = { }
) {
    val state by userApplicationsViewModel.uiState

    LaunchedEffect(Unit) {
        userApplicationsViewModel.loadApplications()
    }

    if (state is UserApplicationsUiState.Idle) {
        return Loading()
    }

    val applications = (state as UserApplicationsUiState.Done).applications
    ApplicationsListCloud(
        applications = applications.sortedBy { it.label },
        displayTarget = true,
        arrowView = { ArrowRight(onArrowClick) },
        onAppClick = {
            userApplicationsViewModel.increaseClickCount(it)
            activityStarter.startApplication(it)
        },
        onAppLongClick = {
            activityStarter.openSettings(it)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun ApplicationsListCloud(
    applications: List<Application>,
    displayTarget: Boolean = false,
    arrowView: @Composable BoxScope.() -> Unit = {},
    onSettingsClick: (() -> Unit)? = null,
    onStatisticsClick: (() -> Unit)? = null,
    onRefreshClick: (() -> Unit)? = null,
    onAppClick: (Application) -> Unit = { },
    onAppLongClick: (Application) -> Unit = { },
    shouldGoBack: () -> Unit = { }
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (applications.isEmpty()) {
            Text(
                modifier = Modifier
                    .padding(64.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                text = "There's nothing, select favourite applications from settings."
            )
        }

        BackHandler {
            shouldGoBack()
        }

        FlowRow(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 23.dp, end = 23.dp),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center
        ) {
            applications.forEach {
                UserApplicationView(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 4.dp, top = 4.dp)
                        .combinedClickable(
                            onClick = { onAppClick(it) },
                            onLongClick = { onAppLongClick(it) }
                        ),
                    it
                )
            }
        }

        NavigationArrows(
            pagerState = null,
            { arrowView() },
            onSettingsClick,
            onStatisticsClick,
            onRefreshClick
        )

        if (displayTarget) {
            YearTargets(modifier = Modifier.align(Alignment.BottomStart).padding(8.dp))
        }
    }
}

data class Tag(val name: String, val count: Int)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagCloud(tags: List<Tag>) {
    val minCount = tags.minOfOrNull { it.count } ?: 0
    val maxCount = tags.maxOfOrNull { it.count } ?: 1
    FlowRow(
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center
    ) {
        tags.forEach { tag ->
            val fontSize = (12 + (tag.count - minCount).toFloat() / (maxCount - minCount) * 12).sp

            val fontWeightValue = ((tag.count - minCount).toFloat() / (maxCount - minCount) * 999 + 1).toInt()
            val fontWeight = FontWeight(fontWeightValue)

            val color = Color.White

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = tag.name,
                    style = TextStyle(
                        color = color,
                        fontSize = fontSize,
                        fontWeight = fontWeight
                    ),
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp, bottom = 8.dp, top = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTagCloud() {
    val tags = listOf(
        Tag("Compose", 5),
        Tag("Jetpack", 12),
        Tag("Kotlin", 7),
        Tag("UI", 3),
        Tag("Android", 25),
        Tag("Development", 9),
        Tag("Open Source", 2),
        Tag("Mobile", 6),
        Tag("Performance", 8),
        Tag("Material Design", 4),
        Tag("Widgets", 10),
        Tag("Themes", 11),
        Tag("Testing", 6),
        Tag("Accompanist", 3),
        Tag("Flow", 7),
        Tag("Coroutines", 9),
        Tag("Navigation", 8),
        Tag("State Management", 12),
        Tag("Compose Preview", 4),
        Tag("Animations", 10),
        Tag("Design System", 5),
        Tag("Layouts", 8),
        Tag("Accessibility", 6),
        Tag("Gradle", 7),
        Tag("Proguard", 2),
        Tag("Libraries", 9),
        Tag("Community", 6),
        Tag("Optimization", 8),
        Tag("Lifecycle", 5),
        Tag("Tutorials", 7)
    )
    Box(
        modifier = Modifier
            .size(400.dp, 500.dp)
            .background(Color.Black)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        TagCloud(tags)
    }
}
