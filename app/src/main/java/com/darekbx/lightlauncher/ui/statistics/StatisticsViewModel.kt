package com.darekbx.lightlauncher.ui.statistics

import androidx.lifecycle.ViewModel
import com.darekbx.lightlauncher.repository.local.dao.ClickCountDao
import com.darekbx.lightlauncher.system.BaseApplicationsProvider
import com.darekbx.lightlauncher.system.model.ClickCount
import kotlinx.coroutines.flow.flow

class StatisticsViewModel(
    private val applicationsProvider: BaseApplicationsProvider,
    private val clickCountDao: ClickCountDao
) : ViewModel() {

    fun getClickCount() = flow {
        val applications = applicationsProvider.listInstalledApplications()
        val clickCounts = clickCountDao.fetch()
        emit(clickCounts.mapNotNull { clickCountDto ->
            applications
                .find { it.activityName == clickCountDto.activityName }
                ?.let { application ->  ClickCount(application.label, clickCountDto.count) }
        })
    }
}
