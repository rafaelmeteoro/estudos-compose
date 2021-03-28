package com.meteoro.jetnews.ui

import androidx.compose.animation.Crossfade
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.meteoro.jetnews.data.AppContainer
import com.meteoro.jetnews.data.interests.InterestsRepository
import com.meteoro.jetnews.data.posts.PostsRepository
import com.meteoro.jetnews.ui.home.HomeScreen
import com.meteoro.jetnews.ui.theme.JetNewsTheme

@Composable
fun JetNewsApp(
    appContainer: AppContainer,
    navigationViewModel: NavigationViewModel
) {
    JetNewsTheme {
        AppContent(
            navigationViewModel = navigationViewModel,
            postsRepository = appContainer.postsRepository,
            interestsRepository = appContainer.interestsRepository
        )
    }
}

@Composable
private fun AppContent(
    navigationViewModel: NavigationViewModel,
    postsRepository: PostsRepository,
    interestsRepository: InterestsRepository
) {
    Crossfade(navigationViewModel.currentScreen) { screen ->
        interestsRepository.also { }
        Surface(color = MaterialTheme.colors.background) {
            when (screen) {
                is Screen.Home -> HomeScreen(
                    navigateTo = navigationViewModel::navigateTo,
                    postsRepository = postsRepository
                )
            }
        }
    }
}
