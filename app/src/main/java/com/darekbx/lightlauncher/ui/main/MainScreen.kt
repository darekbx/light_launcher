package com.darekbx.lightlauncher.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darekbx.lightlauncher.system.ActivityStarter
import com.darekbx.lightlauncher.system.BatteryCycles.getBatteryCycles
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.mathgame.MathGamePage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.forEach

@Composable
fun MainScreen(
    mainScreenViewModel: MainScreenViewModel = koinViewModel(),
    activityStarter: ActivityStarter = koinInject(),
    onOpenSettings: () -> Unit
) {
    val applicationsData by mainScreenViewModel.applicationsData

    LaunchedEffect(Unit) {
        mainScreenViewModel.loadApplications(forceRefresh = false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (applicationsData.isNotEmpty()) {
            LauncherPages(
                pages = applicationsData,
                onRefresh = { mainScreenViewModel.loadApplications(forceRefresh = true) },
                onOpenSettings = onOpenSettings,
                onAppClick = {
                    mainScreenViewModel.increaseClickCount(it)
                    activityStarter.startApplication(it)
                },
                onAppLongClick = { activityStarter.openSettings(it) },
            )
        }
        BatteryHealth(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
fun LauncherPages(
    pages: List<List<Application>>,
    pageSize: Int = 8,
    onRefresh: () -> Unit,
    onOpenSettings: () -> Unit,
    onAppClick: (Application) -> Unit,
    onAppLongClick: (Application) -> Unit
) {
    val scope = rememberCoroutineScope()
    val horizontalPagerState = rememberPagerState(initialPage = 1, pageCount = { 4 })
    val backEnabled by remember { derivedStateOf { horizontalPagerState.currentPage > 1 } }

    BackHandler(enabled = backEnabled) {
        val page = horizontalPagerState.currentPage
        if (page > 1) {
            scope.launch { horizontalPagerState.animateScrollToPage(page - 1) }
        }
    }

    HorizontalPager(
        state = horizontalPagerState,
        beyondViewportPageCount = 0,
        userScrollEnabled = false,
        modifier = Modifier.fillMaxSize()
    ) { horizontalPage ->

        if (horizontalPage == 0) {
            MathGamePage(onBack = {
                scope.launch {
                    horizontalPagerState.animateScrollToPage(1)
                }
            })
        } else {

            val applicationsChunks = pages[horizontalPage - 1].chunked(pageSize)
            val verticalPagerState = rememberPagerState(pageCount = { applicationsChunks.size })
            val pagesAlphabet = remember {
                if (horizontalPage == 1) {
                    extractAlphabetUnSorted(applicationsChunks)
                } else {
                    extractAlphabet(applicationsChunks)
                }
            }

            val backEnabledVert by remember { derivedStateOf { verticalPagerState.currentPage > 0 } }
            BackHandler(enabled = backEnabledVert) {
                if (verticalPagerState.canScrollBackward) {
                    scope.launch {
                        verticalPagerState.animateScrollToPage(verticalPagerState.currentPage - 1)
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                VerticalPager(
                    state = verticalPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { verticalPage ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        applicationsChunks[verticalPage].forEach {
                            ApplicationView(it, onAppClick, onAppLongClick)
                        }
                    }
                }

                PageIndicator(verticalPagerState, pagesAlphabet)
                PageActions(
                    pagerState = horizontalPagerState,
                    onRefresh = onRefresh,
                    onSettings = onOpenSettings
                )
            }
        }
    }
}

private fun extractAlphabet(applicationsChunks: List<List<Application>>): List<List<Char>> =
    applicationsChunks
        .mapIndexed { _, chunk ->
            chunk.map { it.label.first().uppercaseChar() }.distinct().sorted()
        }

private fun extractAlphabetUnSorted(applicationsChunks: List<List<Application>>): List<List<Char>> =
    applicationsChunks
        .mapIndexed { _, chunk ->
            chunk.map { it.label.first().uppercaseChar() }.distinct()
        }

@Composable
private fun BoxScope.PageActions(
    pagerState: PagerState,
    onRefresh: () -> Unit,
    onSettings: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.align(Alignment.BottomEnd),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "back"
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
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "forward"
        )
        Icon(
            modifier = Modifier
                .clickable { onSettings() }
                .padding(24.dp),
            imageVector = Icons.Default.Settings,
            contentDescription = "settings"
        )
        Icon(
            modifier = Modifier
                .clickable { onRefresh() }
                .padding(24.dp),
            imageVector = Icons.Default.Refresh,
            contentDescription = "refresh"
        )
    }
}

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
                pagesAlphabet[page].forEach { text += "$it\n" }
                Text(
                    text = text,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    color =
                        if (page == pagerState.currentPage) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3F),
                    lineHeight = 13.sp,
                    fontWeight = if (page == pagerState.currentPage) FontWeight.ExtraBold else null
                )
            }
        }
    }
}

@Composable
private fun ApplicationView(
    application: Application,
    onAppClick: (Application) -> Unit = { },
    onAppLongClick: (Application) -> Unit = { }
) {
    UserApplicationView(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.Cyan),
                onClick = { onAppClick(application) },
                onLongClick = { onAppLongClick(application) }
            )
            .background(Color.Black, RoundedCornerShape(8.dp))
            .padding(start = 24.dp, end = 24.dp)
            .padding(top = 16.dp, bottom = 16.dp),
        application = application
    )
}

@Composable
fun UserApplicationView(
    modifier: Modifier = Modifier,
    application: Application
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val weight =
            if (application.isFromHome) FontWeight.W500
            else FontWeight.W300

        Text(
            modifier = Modifier,
            text = application.label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = weight,
            letterSpacing = 2.sp,
            fontSize = 22.sp
        )
    }
}

@Preview
@Composable
fun BatteryHealth(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var cycleCount by remember { mutableIntStateOf(-1) }
    var refreshKey by remember { mutableIntStateOf(0) }
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    LaunchedEffect(refreshKey) {
        cycleCount = getBatteryCycles(context)
    }

    Text(
        modifier = modifier
            .height(10.dp)
            .padding(end = 4.dp)
            .clickable { refreshKey++ },
        text = "${if (cycleCount != -1) cycleCount.toString() else "N/A"} cycles (${
            formatter.format(
                Date()
            )
        })",
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.End,
        letterSpacing = 0.sp,
        lineHeight = 9.sp,
        fontSize = 9.sp
    )
}
