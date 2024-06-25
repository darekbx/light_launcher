package com.darekbx.lightlauncher.ui.userapplications

import android.content.ComponentName
import android.content.Intent
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.R
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.Loading
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

    if (applications.isEmpty()) {
        return Loading()
    }
    ApplicationsListPaged(applications, additionalView = {
        ArrowRight(onArrowClick)
    }) {
        userApplicationsViewModel.increaseClickCount(it)
        val intent = Intent().apply { setComponent(ComponentName(it.packageName, it.activityName)) }
        context.startActivity(intent)
    }
}

@Composable
private fun BoxScope.ArrowRight(onArrowClick: () -> Unit) {
    Icon(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(16.dp)
            .clickable { onArrowClick() },
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "forward"
    )
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
    ApplicationsListPaged(applications, additionalView = {
        LeftMenu(onArrowClick, onSettingsClick, onStatisticsClick)
    }) {
        userApplicationsViewModel.increaseClickCount(it)
        val intent = Intent().apply { setComponent(ComponentName(it.packageName, it.activityName)) }
        context.startActivity(intent)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ApplicationsListPaged(
    applications: List<Application>,
    additionalView: @Composable BoxScope.() -> Unit = {},
    onAppClick: (Application) -> Unit = { },
) {
    val pageSize = 10

    Box(modifier = Modifier.fillMaxSize()) {
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

        PageIndicator(pages, pagerState.currentPage)
        additionalView()
    }
}

@Composable
private fun BoxScope.PageIndicator(pages: Int, currentPage: Int) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val backgroundModifier =
            Modifier.background(MaterialTheme.colorScheme.onBackground, CircleShape)
        (0 until pages).forEach { index ->
            Spacer(
                modifier = Modifier
                    .padding(8.dp)
                    .size(8.dp)
                    .then(if (index == currentPage) backgroundModifier else Modifier)
                    .border(1.dp, MaterialTheme.colorScheme.onBackground, CircleShape),
            )
        }
    }
}


@Composable
private fun BoxScope.LeftMenu(
    onArrowClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
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
