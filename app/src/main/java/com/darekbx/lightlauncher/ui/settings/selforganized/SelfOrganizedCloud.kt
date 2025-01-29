package com.darekbx.lightlauncher.ui.settings.selforganized

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import com.darekbx.lightlauncher.system.model.Application
import com.darekbx.lightlauncher.ui.Loading
import com.darekbx.lightlauncher.ui.userapplications.UserApplicationView
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun SelfOrganizedCloud(
    selfOrganizedCloudViewModel: SelfOrganizedCloudViewModel = koinViewModel()
) {
    val state by selfOrganizedCloudViewModel.uiState
    LaunchedEffect(Unit) {
        selfOrganizedCloudViewModel.loadApplicationsOrder()
    }
    state.let {
        when (it) {
            is SelfOrganizedCloudUiState.Done -> {
                OrganizedCloud(
                    modifier = Modifier.fillMaxSize(),
                    applications = it.applications,
                    onPositionChanged = { application ->

                        // TODO update state

                    })
            }

            else -> Loading()
        }
    }
}

@Composable
fun OrganizedCloud(
    modifier: Modifier = Modifier,
    applications: List<Application>,
    onPositionChanged: (Application) -> Unit = { }
) {
    Box(modifier) {
        applications.forEach { application ->
            var offsetX by remember { mutableIntStateOf(application.x) }
            var offsetY by remember { mutableIntStateOf(application.y) }
            UserApplicationView(
                modifier = Modifier
                    .offset { IntOffset(offsetX, offsetY) }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = { onPositionChanged(application) },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x.roundToInt()
                                offsetY += dragAmount.y.roundToInt()
                                application.x = offsetX
                                application.y = offsetY
                            }
                        )
                    },
                application = application
            )
        }
    }
}
