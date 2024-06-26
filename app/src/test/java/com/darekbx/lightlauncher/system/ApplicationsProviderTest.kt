package com.darekbx.lightlauncher.system

import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationsProviderTest {

    @Test
    fun `ResolveInfo was mapped to Application`() {
        // given
        val drawable: Drawable = mockk()
        val resolveInfo: ResolveInfo = mockk {
            every { loadLabel(any()) } returns "Label"
            every { loadIcon(any()) } returns drawable
        }
        val packageManager: PackageManager = mockk {
            every { queryIntentActivities(any(), any<Int>()) } returns listOf(resolveInfo)
        }
        val packageManagerWrapper: PackageManagerWrapper = spyk(PackageManagerWrapper(packageManager)) {
            every { getApplicationLabel(any()) } answers { callOriginal() }
            every { getApplicationIcon(any()) } answers { callOriginal() }
        }
        val applicationsProvider = spyk(ApplicationsProvider(packageManagerWrapper)) {
            every { launcherIntent() } returns mockk()
            every { getPackageName(any()) } returns "com.package.name"
            every { getActivityName(any()) } returns "Activity name"
        }

        // when
        val apps = applicationsProvider.listInstalledApplications()

        // then
        assertEquals(1, apps.size)
        with(apps.first()) {
            assertEquals("Label", label)
            assertEquals("Activity name", activityName)
            assertEquals("com.package.name", packageName)
        }
    }
}
