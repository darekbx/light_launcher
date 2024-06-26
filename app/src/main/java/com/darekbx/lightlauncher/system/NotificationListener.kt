package com.darekbx.lightlauncher.system

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.darekbx.lightlauncher.repository.local.dao.NotificationDao
import com.darekbx.lightlauncher.repository.local.dto.NotificationDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class NotificationListener : NotificationListenerService() {

    private val ioDispatcher: CoroutineDispatcher by inject(named("io_dispatcher"))
    private val notificationDao: NotificationDao by inject()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        CoroutineScope(ioDispatcher).launch {
            notificationDao.add(NotificationDto(null, sbn.packageName))
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        CoroutineScope(ioDispatcher).launch {
            notificationDao.delete(sbn.packageName)
        }
    }
}
