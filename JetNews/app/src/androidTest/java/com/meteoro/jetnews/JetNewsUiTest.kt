package com.meteoro.jetnews

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@MediumTest
class JetNewsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        // Using targetContext as the Context of the instrumentation code
        composeTestRule.launchJetNewsApp(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Ignore("TODO Investigate why this passes locally but failed on CI")
    @Test
    fun app_launches() {
        composeTestRule.onNodeWithText("JetNews").assertIsDisplayed()
    }

    @Ignore("TODO investigate why this passes locally but fail on CI")
    @Test
    fun app_opensArticle() {
        composeTestRule.onNodeWithText(text = "Manuel Vivo", substring = true).performClick()
        composeTestRule.onNodeWithText(text = "3 min read", substring = true).assertIsDisplayed()
    }
}