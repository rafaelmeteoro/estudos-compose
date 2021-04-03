package com.meteoro.jetnews

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.meteoro.jetnews.ui.home.HomeScreen
import com.meteoro.jetnews.ui.state.UiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

/**
 * Checks that the Snackbar is shown when the HomeScreen data contains an error.
 * */
class HomeScreenSnackbarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(
        ExperimentalMaterialApi::class,
        ExperimentalComposeApi::class
    )
    // @Ignore("TODO Investigate why this passes locally but fail on CI")
    @Test
    fun postsContainError_snackbarShown() {
        val snackbarHostState = SnackbarHostState()
        composeTestRule.setContent {
            val scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState)

            // When the Home Screen receives data with an error
            HomeScreen(
                posts = UiState(exception = IllegalStateException()),
                favorites = emptySet(),
                onToggleFavorite = {},
                onRefreshPosts = {},
                onErrorDismiss = {},
                navigateTo = {},
                scaffoldState = scaffoldState
            )

            // Then the first message received in the Snackbar is an error message
            val snacbarText = InstrumentationRegistry.getInstrumentation()
                .targetContext.resources.getString(R.string.load_error)
            runBlocking {
                // snapshotFlow converts a State to a Kotlin Flow so we can observe it
                // wait for the first a non-null `currentSnackbarData`
                snapshotFlow { snackbarHostState.currentSnackbarData }.filterNotNull().first()
                composeTestRule.onNodeWithText(snacbarText, false, false).assertIsDisplayed()
            }
        }
    }
}