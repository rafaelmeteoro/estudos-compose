package com.meteoro.jetchat.profile

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.insets.ProvideWindowInsets
import com.meteoro.jetchat.R
import com.meteoro.jetchat.data.meProfile
import com.meteoro.jetchat.ui.theme.JetChatTheme

@Composable
fun ProfileScreen(userData: ProfileScreenState, onNavIconPressed: () -> Unit = {}) {

}

@Composable
fun ProfileError() {
    Text(stringResource(id = R.string.profile_error))
}

@Preview(widthDp = 640, heightDp = 360)
@Composable
fun ConvPreviewLandsCapeMeDefault() {
    ProvideWindowInsets(consumeWindowInsets = false) {
        JetChatTheme {
            ProfileScreen(meProfile)
        }
    }
}