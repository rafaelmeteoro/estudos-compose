package com.meteoro.jetnews.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.meteoro.jetnews.ui.theme.JetNewsTheme

@Composable
internal fun ThemedPreview(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    JetNewsTheme(darkTheme = darkTheme) {
        Surface {
            content()
        }
    }
}