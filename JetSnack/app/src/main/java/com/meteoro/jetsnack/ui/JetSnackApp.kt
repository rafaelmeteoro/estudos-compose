package com.meteoro.jetsnack.ui

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.Crossfade
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.google.accompanist.insets.ProvideWindowInsets
import com.meteoro.jetsnack.ui.home.Home
import com.meteoro.jetsnack.ui.theme.JetSnackTheme
import com.meteoro.jetsnack.ui.utils.Navigator

@Composable
fun JetSnackApp(backDispatcher: OnBackPressedDispatcher) {
    val navigator: Navigator<Destination> = rememberSaveable(
        saver = Navigator.saver(backDispatcher)
    ) {
        Navigator(Destination.Home, backDispatcher)
    }
    val actions = remember(navigator) { Actions(navigator) }
    ProvideWindowInsets {
        JetSnackTheme {
            Crossfade(navigator.current) { destination ->
                when (destination) {
                    Destination.Home -> Home(actions.selectSnack)
                    is Destination.SnackDetail -> Text(text = "SnackDetail")
                }
            }
        }
    }
}