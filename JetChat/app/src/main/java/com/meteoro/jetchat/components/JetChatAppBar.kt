package com.meteoro.jetchat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meteoro.jetchat.R
import com.meteoro.jetchat.ui.theme.JetChatTheme
import com.meteoro.jetchat.ui.theme.elevatedSurface

@Composable
fun JetChatAppBar(
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = {},
    title: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    // This bar is translucent but elevation overlays are not applied to translucent colors.
    // Instead we manually calculate the elevated surface color from the opaque color,
    // then apply our alpha.
    //
    // We set the background on the Column rather than the TopAppBar,
    // so that the background is drawn behind any padding set on the app bar (i.e. status bar).
    val backgroundColor = MaterialTheme.colors.elevatedSurface(3.dp)
    Column(
        Modifier.background(backgroundColor.copy(alpha = 0.95f))
    ) {
        TopAppBar(
            modifier = modifier,
            backgroundColor = Color.Transparent,
            elevation = 0.dp, // No shadow needed
            contentColor = MaterialTheme.colors.onSurface,
            actions = actions,
            title = { Row { title() } }, // https://issuetracker.google.com/168793068
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_jetchat),
                    contentDescription = stringResource(id = R.string.back),
                    modifier = Modifier
                        .clickable(onClick = onNavIconPressed)
                        .padding(horizontal = 16.dp)
                )
            }
        )
        Divider()
    }
}

@Preview
@Composable
fun JetChatAppBarPreview() {
    JetChatTheme {
        JetChatAppBar(title = { Text("Preview!") })
    }
}

@Preview
@Composable
fun JetChatAppBarPreviewDark() {
    JetChatTheme(isDarkTheme = true) {
        JetChatAppBar(title = { Text("Preview!") })
    }
}