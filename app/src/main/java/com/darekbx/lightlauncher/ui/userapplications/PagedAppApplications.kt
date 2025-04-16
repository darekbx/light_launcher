package com.darekbx.lightlauncher.ui.userapplications

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.lightlauncher.R
import com.darekbx.lightlauncher.di.appModule
import com.darekbx.lightlauncher.di.viewModelModule
import com.darekbx.lightlauncher.system.ActivityStarter
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.Loading
import com.darekbx.lightlauncher.ui.settings.SettingsViewModel
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import com.darekbx.lightlauncher.ui.theme.fontFamily
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import kotlin.math.ceil
import kotlin.math.min
import kotlin.random.Random

@Composable
fun UserApplicationsListPaged(
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
    ApplicationsListPaged(
        applications = applications,
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

@Composable
fun AllApplicationsListPaged(
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
    ApplicationsListPaged(
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ApplicationsListPaged(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    applications: List<Application>,
    arrowView: @Composable BoxScope.() -> Unit = {},
    onSettingsClick: (() -> Unit)? = null,
    onStatisticsClick: (() -> Unit)? = null,
    onRefreshClick: (() -> Unit)? = null,
    onAppClick: (Application) -> Unit = { },
    onAppLongClick: (Application) -> Unit = { },
    shouldGoBack: () -> Unit = { }
) {
    val coroutineScope = rememberCoroutineScope()
    var pageSize by remember { mutableIntStateOf(12) }

    val pagesAlphabet = applications
        .chunked(pageSize)
        .mapIndexed { index, chunk ->
            val firstLetters = chunk.map { it.label.first().uppercaseChar() }.distinct().sorted()
            firstLetters
        }

    LaunchedEffect(Unit) {
        settingsViewModel.load { _, _, _, pageSizeValue ->
            pageSize = pageSizeValue
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (applications.isEmpty()) {
            Text(
                modifier = Modifier
                    .padding(64.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                text = "There's nothing, select favourite applications from settings."
            )
        }

        val pages = ceil(applications.size / pageSize.toFloat()).toInt()
        val pagerState = rememberPagerState(pageCount = { pages })

        BackHandler {
            if (pagerState.canScrollBackward) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            } else {
                shouldGoBack()
            }
        }

        VerticalPager(state = pagerState) { page ->
            val items = applications.subList(
                page * pageSize,
                min(applications.size, (page + 1) * pageSize)
            )
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (pagerState.currentPage != 0) {
                        Icon(
                            modifier = Modifier.padding(bottom = 16.dp),
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "up"
                        )
                    }

                    items.forEach {
                        UserApplicationView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(color = Color.Cyan),
                                    onClick = { onAppClick(it) },
                                    onLongClick = { onAppLongClick(it) }
                                )
                                .background(Color.Black, RoundedCornerShape(8.dp))
                                .padding(start = 48.dp, end = 48.dp)
                                .padding(top = 16.dp, bottom = 16.dp),
                            application = it,
                            staticSize = true
                        )
                    }

                    if (pagerState.currentPage != pages - 1) {
                        Icon(
                            modifier = Modifier.padding(top = 16.dp),
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "down"
                        )
                    }
                }
            }
        }

        PageIndicator(pagerState, if (onSettingsClick != null) pagesAlphabet else emptyList())
        NavigationArrows(
            pagerState,
            { arrowView() },
            onSettingsClick,
            onStatisticsClick,
            onRefreshClick
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxScope.NavigationArrows(
    pagerState: PagerState? = null,
    arrowView: @Composable () -> Unit,
    onSettingsClick: (() -> Unit)? = null,
    onStatisticsClick: (() -> Unit)? = null,
    onRefreshClick: (() -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.align(Alignment.BottomEnd),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        onRefreshClick?.let {
            Icon(
                modifier = Modifier
                    .clickable { onRefreshClick() }
                    .padding(24.dp),
                imageVector = Icons.Default.Refresh,
                contentDescription = "refresh"
            )
        }
        onSettingsClick?.let {
            Icon(
                modifier = Modifier
                    .clickable { onSettingsClick() }
                    .padding(24.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = "settings"
            )
        }
        onStatisticsClick?.let {
            Icon(
                modifier = Modifier
                    .clickable { onStatisticsClick() }
                    .padding(24.dp),
                painter = painterResource(id = R.drawable.ic_chart),
                contentDescription = "statistics"
            )
        }
        arrowView()
        if (pagerState != null) {
            Icon(
                modifier = Modifier
                    .clickable {
                        scope.launch {
                            if (pagerState.currentPage > 0) {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    }
                    .padding(24.dp),
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "up"
            )
            Icon(
                modifier = Modifier
                    .clickable {
                        scope.launch {
                            if (pagerState.currentPage < pagerState.pageCount - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                    .padding(24.dp),
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "down"
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.PageIndicator(pagerState: PagerState, pagesAlphabet: List<List<Char>>) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (pagesAlphabet.isNotEmpty()) {
            (0 until pagerState.pageCount).forEach { page ->
                var text = ""
                pagesAlphabet[page].forEach {
                    text += "$it\n"
                }
                Text(
                    text = text,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    color =
                    if (page == pagerState.currentPage) MaterialTheme.colorScheme.onBackground
                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4F),
                    lineHeight = 13.sp,
                    fontWeight = if (page == pagerState.currentPage) FontWeight.ExtraBold else null
                )
            }
        }

        val backgroundModifier =
            Modifier.background(MaterialTheme.colorScheme.onBackground, CircleShape)
        (0 until pagerState.pageCount).forEach { index ->
            Spacer(
                modifier = Modifier
                    .padding(8.dp)
                    .size(8.dp)
                    .then(if (index == pagerState.currentPage) backgroundModifier else Modifier)
                    .border(1.dp, MaterialTheme.colorScheme.onBackground, CircleShape),
            )
        }
    }
}

@Composable
fun ArrowRight(onArrowClick: () -> Unit) {
    Icon(
        modifier = Modifier
            .clickable { onArrowClick() }
            .padding(24.dp),
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "forward"
    )
}

@Composable
fun ArrowLeft(onArrowClick: () -> Unit) {
    Icon(
        modifier = Modifier
            .clickable { onArrowClick() }
            .padding(24.dp),
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "back"
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(device = Devices.PIXEL_6A, showSystemUi = true)
@Composable
fun UserApplicationsListPagedPreview() {
    val names = listOf(
        "70mai",
        "Alior Mobile",
        "Allegro",
        "Assistant",
        "Authenticator",
        "Backup",
        "Books",
        "Calculator",
        "Calendar",
        "Camera",
        "Chrome",
        "Clock",
        "Phone",
        "Photos",
        "Google Earth",
        "Google Pay",
        "Google Play",
        "Google Play Movies",
        "Contacts",
        "Drive",
        "Duo",
        "Files",
        "Find Device",
        "Gmail",
        "Google",
        "Keep",
        "Maps",
        "Messages",
        "Music",
        "News",
        "Phone",
        "Photos",
        "Google Earth",
        "Google Pay",
        "Google Play",
        "Google Play Movies",
        "Google Play Music",
        "Play Store",
        "Settings",
        "Spotify",
        "Translate",
        "YouTube",
        "Zoom",
        "Google Earth",
        "Google Pay",
        "Google Play",
        "Google Play Movies",
        "Google Play Music"
    )
    val applications = names.map { Application("", "", it, null) }
    LightLauncherTheme {
        KoinApplication(application = { modules(appModule, viewModelModule) }) {
            FlowRow(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(start = 48.dp, end = 48.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center
            ) {
                applications.forEach { application ->
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = application.label,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight(Random.nextInt(100, 900)),
                        textDecoration = if (application.isFromHome) TextDecoration.Underline else null,
                        fontFamily = fontFamily,
                        fontSize = 18.sp
                    )
                }

            }
        }
    }
}
