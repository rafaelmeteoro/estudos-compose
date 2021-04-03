package com.meteoro.jetnews

import android.content.Context
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.lifecycle.SavedStateHandle
import com.meteoro.jetnews.ui.JetNewsApp
import com.meteoro.jetnews.ui.NavigationViewModel

/**
 * Launches the app from a test context
 * */
fun ComposeContentTestRule.launchJetNewsApp(context: Context) {
    setContent {
        JetNewsApp(
            appContainer = TestAppContainer(context),
            navigationViewModel = remember { NavigationViewModel(SavedStateHandle()) }
        )
    }
}