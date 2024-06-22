package com.darekbx.lightlauncher.ui.userapplications

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.Loading
import com.darekbx.lightlauncher.ui.theme.fontFamily
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserApplicationsScreen(
    onSettingsClick: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    HorizontalPager(modifier = Modifier.fillMaxSize(), state = pagerState) { page ->
        when (page) {
            0 -> UserApplicationsList()
            1 -> AllApplicationsList(onSettingsClick = onSettingsClick)
        }
    }
}

@Composable
fun AllApplicationsList(
    userApplicationsViewModel: UserApplicationsViewModel = koinViewModel(),
    onSettingsClick: () -> Unit = { }
) {
    val state by userApplicationsViewModel.uiState

    LaunchedEffect(Unit) {
        userApplicationsViewModel.loadAllApplications()
        userApplicationsViewModel.listenForPackageChanges()
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
                        .clickable {
                            userApplicationsViewModel.increaseClickCount(item.packageName)
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
                .align(Alignment.TopStart)
                .padding(16.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "forward"
        )

        TextButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = { onSettingsClick() }) {
            Text(
                text = "Settings",
                fontFamily = fontFamily
            )
        }
    }
}

@Composable
fun UserApplicationsList(
    userApplicationsViewModel: UserApplicationsViewModel = koinViewModel()
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
                        .clickable {
                            userApplicationsViewModel.increaseClickCount(item.packageName)
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
                .padding(16.dp),
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
            fontFamily = fontFamily
        )
    }
}
