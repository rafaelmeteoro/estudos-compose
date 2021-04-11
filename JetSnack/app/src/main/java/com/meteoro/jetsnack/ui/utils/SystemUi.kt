package com.meteoro.jetsnack.ui.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

private val BlackScrim = Color(0f, 0f, 0f, 0.2f) // 20% opaque black
private val BlackScrimmed: (Color) -> Color = { original ->
    BlackScrim.compositeOver(original)
}