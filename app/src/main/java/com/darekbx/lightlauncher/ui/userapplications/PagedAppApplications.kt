package com.darekbx.lightlauncher.ui.userapplications

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.R
import com.darekbx.lightlauncher.system.ActivityStarter
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.Loading
import com.darekbx.lightlauncher.ui.settings.SettingsViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.ceil
import kotlin.math.min

@Composable
fun UserApplicationsListPaged(
    userApplicationsViewModel: UserApplicationsViewModel = koinViewModel(),
    onArrowClick: () -> Unit = { }
) {
    val context = LocalContext.current
    val applications by userApplicationsViewModel
        .loadApplications()
        .collectAsState(initial = emptyList())

    ApplicationsListPaged(
        applications = applications,
        arrowView = { ArrowRight(onArrowClick) }
    ) {
        userApplicationsViewModel.increaseClickCount(it)
        ActivityStarter.startApplication(context, it)
    }
}

@Composable
fun AllApplicationsListPaged(
    userApplicationsViewModel: UserApplicationsViewModel = koinViewModel(),
    onSettingsClick: () -> Unit = { },
    onStatisticsClick: () -> Unit = { },
    onArrowClick: () -> Unit = { }
) {
    val state by userApplicationsViewModel.uiState
    val context = LocalContext.current

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
        onStatisticsClick = onStatisticsClick
    ) {
        userApplicationsViewModel.increaseClickCount(it)
        ActivityStarter.startApplication(context, it)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ApplicationsListPaged(
    settingsViewModel: SettingsViewModel = koinViewModel(),
    applications: List<Application>,
    arrowView: @Composable BoxScope.() -> Unit = {},
    onSettingsClick: (() -> Unit)? = null,
    onStatisticsClick: (() -> Unit)? = null,
    onAppClick: (Application) -> Unit = { }
) {
    var pageSize by remember { mutableStateOf(10) }

    LaunchedEffect(Unit) {
        settingsViewModel.load { _, pageSizeValue ->
            pageSize = pageSizeValue
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (applications.isEmpty()) {
            Text(
                modifier = Modifier.padding(64.dp).align(Alignment.Center),
                textAlign = TextAlign.Center,
                text = "There's nothing, select favourite applications from settings."
            )
        }

        val pages = ceil(applications.size / pageSize.toFloat()).toInt()
        val pagerState = rememberPagerState(pageCount = { pages })
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
                                .padding(start = 48.dp, end = 48.dp)
                                .clickable { onAppClick(it) },
                            it
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

        PageIndicator(pagerState)
        NavigationArrows(pagerState, { arrowView() }, onSettingsClick, onStatisticsClick)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.NavigationArrows(
    pagerState: PagerState,
    arrowView: @Composable () -> Unit,
    onSettingsClick: (() -> Unit)? = null,
    onStatisticsClick: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.align(Alignment.BottomEnd),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoxScope.PageIndicator(pagerState: PagerState) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
private fun ArrowRight(onArrowClick: () -> Unit) {
    Icon(
        modifier = Modifier
            .clickable { onArrowClick() }
            .padding(24.dp),
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "forward"
    )
}

@Composable
private fun ArrowLeft(onArrowClick: () -> Unit) {
    Icon(
        modifier = Modifier
            .clickable { onArrowClick() }
            .padding(24.dp),
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "back"
    )
}
