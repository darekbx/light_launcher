package com.darekbx.lightlauncher.ui.settings.favourites

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.darekbx.lightlauncher.system.model.FavouriteApplication
import com.darekbx.lightlauncher.ui.theme.LightLauncherTheme
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class FavouriteApplicationsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `should change favourite to unchecked on click`() = runTest {
        // given
        val application = FavouriteApplication("", "com.test.package", "Label", true)
        composeTestRule.setContent {
            LightLauncherTheme {
                FavouriteApplicationView(Modifier, application)
            }
        }

        // when
        composeTestRule.onNodeWithTag("favourite_application_view").performClick()

        // then
        composeTestRule.onNodeWithTag("favourite_application_unchecked", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("favourite_application_checked", useUnmergedTree = true)
            .assertIsNotDisplayed()
    }

    @Test
    fun `should change favourite to checked on click`() = runTest {
        // given
        val application = FavouriteApplication("", "com.test.package", "Label", false)
        composeTestRule.setContent {
            LightLauncherTheme {
                FavouriteApplicationView(Modifier, application)
            }
        }

        // when
        composeTestRule.onNodeWithTag("favourite_application_view").performClick()

        // then
        composeTestRule.onNodeWithTag("favourite_application_checked", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("favourite_application_unchecked", useUnmergedTree = true)
            .assertIsNotDisplayed()
    }
}
