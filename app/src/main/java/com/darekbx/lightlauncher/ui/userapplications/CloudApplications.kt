package com.darekbx.lightlauncher.ui.userapplications

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.system.ActivityStarter
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.Loading
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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
    isSelfOrganizedCloud: Boolean,
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
        applications = applications,
        displayTarget = true,
        isSelfOrganizedCloud = isSelfOrganizedCloud,
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
    isSelfOrganizedCloud: Boolean = false,
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

        if (isSelfOrganizedCloud) {
            OrganizedCloud(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                applications = applications,
                onAppClick = onAppClick,
                onAppLongClick = onAppLongClick
            )
        } else {
            FlowRow(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 40.dp, end = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center
            ) {
                applications.forEach { application ->
                    UserApplicationView(
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp, bottom = 8.dp, top = 8.dp)
                            .combinedClickable(
                                onClick = { onAppClick(application) },
                                onLongClick = { onAppLongClick(application) }
                            ),
                        application = application
                    )
                }
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
            YearTargets(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrganizedCloud(
    modifier: Modifier,
    applications: List<Application>,
    onAppClick: (Application) -> Unit = { },
    onAppLongClick: (Application) -> Unit = { },
) {
    Box(modifier) {
        applications.forEach { application ->
            UserApplicationView(
                modifier = Modifier
                    .offset { IntOffset(application.x, application.y) }
                    .padding(8.dp)
                    .combinedClickable(
                        onClick = { onAppClick(application) },
                        onLongClick = { onAppLongClick(application) }
                    ),
                application = application
            )

        }
    }
    /*
    // TODO - fix this
    ApplicationsBoard(modifier, application = { index -> applications[index] }) {
        applications.forEach { application ->
            UserApplicationView(
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp, bottom = 8.dp, top = 8.dp)
                    .combinedClickable(
                        onClick = { onAppClick(application) },
                        onLongClick = { onAppLongClick(application) }
                    ),
                application = application
            )
        }
    }*/
}

@Composable
fun ApplicationsBoard(
    modifier: Modifier,
    application: (Int) -> Application,
    mainContent: @Composable () -> Unit
) {
    SubcomposeLayout(modifier) { constraints ->
        val mainPlaceables = subcompose(slotId = "main_slot", mainContent).map {
            it.measure(Constraints())
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            mainPlaceables.forEachIndexed { index, placeable ->
                val app = application(index)
                placeable.place(app.x, app.y)
            }
        }
    }
}
