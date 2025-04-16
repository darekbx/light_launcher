package com.darekbx.lightlauncher.ui.userapplications

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.lightlauncher.R
import com.darekbx.lightlauncher.system.ActivityStarter
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.Loading
import com.darekbx.lightlauncher.ui.settings.SettingsViewModel
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import com.darekbx.lightlauncher.ui.theme.fontFamily
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.Calendar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserApplicationsScreen(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    onSettingsClick: () -> Unit,
    onStatisticsClick: () -> Unit,
) {
    var isPaged by remember { mutableStateOf(true) }
    var isCloud by remember { mutableStateOf(false) }
    var isSelfOrganizedCloud by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        settingsViewModel.load { usePages, useCloud, uUseSelfOrganizedCloud, _ ->
            isPaged = usePages
            isCloud = useCloud
            isSelfOrganizedCloud = uUseSelfOrganizedCloud
        }
    }

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })

    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false,
        state = pagerState
    ) { page ->
        when (page) {
            0 -> {
                if (isCloud) {
                    UserApplicationsListCloud(
                        onArrowClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                        isSelfOrganizedCloud = isSelfOrganizedCloud
                    )
                } else if (isPaged) {
                    UserApplicationsListPaged(
                        onArrowClick = { scope.launch { pagerState.animateScrollToPage(1) } }
                    )
                } else {
                    UserApplicationsList(
                        onArrowClick = { scope.launch { pagerState.animateScrollToPage(1) } }
                    )
                }
            }

            1 -> {
                if (isCloud) {
                    AllApplicationsListCloud(
                        onSettingsClick = onSettingsClick,
                        onStatisticsClick = onStatisticsClick,
                        onArrowClick = { scope.launch { pagerState.animateScrollToPage(0) } }
                    )
                } else if (isPaged) {
                    AllApplicationsListPaged(
                        onSettingsClick = onSettingsClick,
                        onStatisticsClick = onStatisticsClick,
                        onArrowClick = { scope.launch { pagerState.animateScrollToPage(0) } }
                    )
                } else {
                    AllApplicationsList(
                        onSettingsClick = onSettingsClick,
                        onStatisticsClick = onStatisticsClick,
                        onArrowClick = { scope.launch { pagerState.animateScrollToPage(0) } }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllApplicationsList(
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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(applications) { item ->
                UserApplicationView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 48.dp, end = 48.dp)
                        .padding(top = 12.dp, bottom = 12.dp)
                        .combinedClickable(
                            onClick = {
                                userApplicationsViewModel.increaseClickCount(item)
                                activityStarter.startApplication(item)
                            },
                            onLongClick = {
                                activityStarter.openSettings(item)
                            }
                        ),
                    application = item,
                    staticSize = true
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.TopStart),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onArrowClick() },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "forward"
            )
            Icon(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onSettingsClick() },
                imageVector = Icons.Default.Settings,
                contentDescription = "settings"
            )
            Icon(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onStatisticsClick() },
                painter = painterResource(id = R.drawable.ic_chart),
                contentDescription = "statistics"
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserApplicationsList(
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
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(applications) { item ->
                UserApplicationView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 48.dp, end = 48.dp)
                        .padding(top = 12.dp, bottom = 12.dp)
                        .combinedClickable(
                            onClick = {
                                userApplicationsViewModel.increaseClickCount(item)
                                activityStarter.startApplication(item)
                            },
                            onLongClick = {
                                activityStarter.openSettings(item)
                            }
                        ),
                    application = item,
                    staticSize = true
                )
            }
        }

        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .clickable { onArrowClick() },
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "forward"
        )
    }
}

@Composable
fun UserApplicationView(
    modifier: Modifier = Modifier,
    application: Application,
    staticSize: Boolean = false,
    notificationViewModel: NotificationViewModel = koinViewModel()
) {
    val showAppIcon = false
    val notifications by notificationViewModel.fetchNotifications()
        .collectAsState(initial = emptyList())
    Row(
        modifier
            .ifTrue(!staticSize) { scale(application.scale) }
            .semantics { testTag = "favourite_application_view" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (showAppIcon) {
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp),
                painter = rememberDrawablePainter(application.icon),
                contentDescription = application.label
            )
        }

        val weight = if (staticSize) {
            if (application.isFromHome) FontWeight.W500
            else FontWeight.W300
        } else FontWeight(application.fontWeight)

        Text(
            modifier = Modifier,
            text = application.label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = weight,
            letterSpacing = 2.sp,
            //textDecoration = if (application.isFromHome) TextDecoration.Underline else null,
            //fontFamily = fontFamily,
            fontSize = 22.sp//application.scale.sp
        )

        if (notifications.any { it.packageName == application.packageName }) {
            Spacer(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.onBackground, CircleShape)
            )
        }
    }
}

@Composable
fun YearTargets(modifier: Modifier = Modifier) {
    val dayOfyear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    Column(modifier = modifier) {
        Target("Przejechać wał Obórki - Bielawa", completed = false)
        Target("10km piechotą", completed = false)
        Target("Jazda Tamiyą", completed = false)
        Target("Farma wiatrowa", completed = false)
        Text(
            modifier = Modifier.padding(start = 22.dp),
            text = "${365 - dayOfyear} days left",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun Target(name: String, completed: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (completed) Icons.Default.Done else Icons.Default.Clear,
            contentDescription = "target",
            tint = if (completed) Color.Green else Color.Red,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = name,
            modifier = Modifier.padding(start = 4.dp),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

    }
}

inline fun Modifier.ifTrue(value: Boolean, builder: Modifier.() -> Modifier): Modifier =
    if (value) this.builder() else this

@Preview
@Composable
fun YearTargetsPreview() {
    LightLauncherTheme {
        Box(Modifier
            .background(Color.Black)
            .padding(32.dp)) {
            YearTargets()
        }
    }
}