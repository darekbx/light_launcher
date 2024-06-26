package com.darekbx.lightlauncher.ui.userapplications

import androidx.lifecycle.ViewModel
import com.darekbx.lightlauncher.repository.local.dao.NotificationDao

class NotificationViewModel(
    private val notificationDao: NotificationDao
) : ViewModel() {

    fun fetchNotifications() = notificationDao.fetch()
}
