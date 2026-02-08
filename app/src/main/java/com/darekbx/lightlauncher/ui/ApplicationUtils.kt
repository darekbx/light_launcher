package com.darekbx.lightlauncher.ui

import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFilter
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.repository.local.dto.ApplicationDto
import com.darekbx.lightlauncher.repository.local.dto.ClickCountDto


suspend fun getMaxCount(
    clickCountDao: ClickCountDao,
    savedApps: List<ApplicationDto>,
    condition: (ApplicationDto, ClickCountDto) -> Boolean
): Int {
    try {
        val maxCount = clickCountDao.getMaxCount()
            .takeIf { it.isNotEmpty() } ?: return 0
        var count = maxCount
            .fastFilter { clickCount ->
                savedApps.fastAny { application ->
                    condition(application, clickCount)
                }
            }
            .maxBy { it.count }
            .count
        if (count > 400) {
            count = (count + 0.7).toInt()
        }
        return count
    } catch (e: Exception) {
        return 0
    }
}

fun calculateFontWeight(clickCount: Int, maxClicks: Int): Int {
    val minWeight = 1
    val maxWeight = 1000
    if (maxClicks == 0) {
        return 400 // Default font weight
    }
    val normalizedClickCount = clickCount.coerceAtMost(maxClicks)
    return minWeight + ((maxWeight - minWeight) * normalizedClickCount / maxClicks)
}

fun mapToScale(
    value: Int,
    minInput: Int = 1,
    maxInput: Int = 1000,
    minScale: Float = 0.7F,
    maxScale: Float = 1.4F
): Float {
    val clampedValue = value.coerceIn(minInput, maxInput)
    return minScale + (maxScale - minScale) * (clampedValue - minInput) / (maxInput - minInput)
}

fun mapToFontSize(
    value: Int,
    minInput: Int = 1,
    maxInput: Int = 1000,
    minSize: Int = 10,
    maxSize: Int = 48
): Int {
    val clampedValue = value.coerceIn(minInput, maxInput)
    return minSize + (maxSize - minSize) * (clampedValue - minInput) / (maxInput - minInput)
}