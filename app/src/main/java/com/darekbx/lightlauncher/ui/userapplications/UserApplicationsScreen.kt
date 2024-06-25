package com.darekbx.lightlauncher.ui.userapplications

import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.R
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.Loading
import com.darekbx.lightlauncher.ui.theme.fontFamily
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserApplicationsScreen(
    onSettingsClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
    val isPaged = true
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })

    HorizontalPager(modifier = Modifier.fillMaxSize(), state = pagerState) { page ->
        when (page) {
            0 -> {
                if (isPaged) {
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
                if (isPaged) {
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


@Composable
fun AllApplicationsList(
    userApplicationsViewModel: UserApplicationsViewModel = koinViewModel(),
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
    val context = LocalContext.current

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
                        .clickable {
                            userApplicationsViewModel.increaseClickCount(item)
                            val intent = Intent().apply {
                                setComponent(
                                    ComponentName(
                                        item.packageName,
                                        item.activityName
                                    )
                                )
                            }
                            context.startActivity(intent)
                        },
                    item
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

@Composable
fun UserApplicationsList(
    userApplicationsViewModel: UserApplicationsViewModel = koinViewModel(),
    onArrowClick: () -> Unit = { }
) {
    val applications by userApplicationsViewModel
        .loadApplications()
        .collectAsState(initial = emptyList())

    if (applications.isEmpty()) {
        return Loading()
    }

    val context = LocalContext.current
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
                        .clickable {
                            userApplicationsViewModel.increaseClickCount(item)
                            val intent = context
                                .packageManager
                                .getLaunchIntentForPackage(item.packageName)
                            context.startActivity(intent)
                        },
                    item
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
    application: Application
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
            .semantics { testTag = "favourite_application_view" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = application.label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textDecoration = if (application.isFromHome) TextDecoration.Underline else null,
            fontFamily = fontFamily
        )
    }
}
