package com.meteoro.jetnews.ui.home

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import com.meteoro.jetnews.R
import com.meteoro.jetnews.data.posts.PostsRepository
import com.meteoro.jetnews.model.Post
import com.meteoro.jetnews.ui.Screen
import com.meteoro.jetnews.ui.state.UiState
import com.meteoro.jetnews.utils.produceUiState
import kotlinx.coroutines.launch

/**
 * Stateful HomeScreen which manages state using [produceUiState]
 *
 * @param navigateTo (event) request navigation to [Screen]
 * @param postsRepository data source for this screen
 * @param scaffoldState (state) state for the [Scaffold] component on this screen
 * */
@Composable
fun HomeScreen(
    navigateTo: (Screen) -> Unit,
    postsRepository: PostsRepository,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val (postUiState, refreshPost, clearError) = produceUiState(postsRepository) {
        getPosts()
    }

    // [collectAsState] will automatically collect a Flow<T> and return a State<T> object that
    // updates whenever the Flow emits a value. Collection is cancelled when [collectsAsState] is
    // removed from the composition tree.
    val favorites by postsRepository.observeFavorites().collectAsState(setOf())

    val coroutineScope = rememberCoroutineScope()

    HomeScreen(
        posts = postUiState.value,
        favorites = favorites,
        onToggleFavorite = {
            coroutineScope.launch { postsRepository.toggleFavorite(it) }
        },
        onRefreshPosts = refreshPost,
        onErrorDismiss = clearError,
        navigateTo = navigateTo,
        scaffoldState = scaffoldState
    )
}

/**
 * Responsible for displaying the Home Screen of this application.
 *
 * Stateless composable is not coupled to any specific state management.
 *
 * @param posts (state) the data to show on the screen
 * @param favorites (state) favorite posts
 * @param onToggleFavorite (event) toggles favorite for a post
 * @param onRefreshPosts (event) request a refresh of posts
 * @param onErrorDismiss (event) request the current error be dismissed
 * @param navigateTo (event) request navigation to [Screen]
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    posts: UiState<List<Post>>,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: () -> Unit,
    navigateTo: (Screen) -> Unit,
    scaffoldState: ScaffoldState
) {
    if (posts.hasError) {
        val errorMessage = stringResource(id = R.string.load_error)
        val retryMessage = stringResource(id = R.string.retry)

        // If onRefreshPosts or onErrorDismiss change while the LaunchedEffect is running,
        // don't restart the effect and use the latest lambda values.
        val onRefreshPostsState by rememberUpdatedState(onRefreshPosts)
        val onErrorDismissState by rememberUpdatedState(onErrorDismiss)

        // Show snackbar using a coroutine, when the coroutine is cancelled the snackbar will
        // automatically dismiss. This coroutine will cancel whenever posts.hasError is false
        // (thans o the surrounding if statement) or if scaffoldStates changes.
        LaunchedEffect(scaffoldState) {
            val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = retryMessage
            )
            when (snackbarResult) {
                SnackbarResult.ActionPerformed -> onRefreshPostsState()
                SnackbarResult.Dismissed -> onErrorDismissState()
            }
        }
    }

    onToggleFavorite("")
    navigateTo(Screen.Home)
//    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        content = {
            Text(text = "Eu: ${favorites.size}")
        }
    )
}