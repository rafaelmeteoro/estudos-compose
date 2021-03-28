package com.meteoro.jetnews.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meteoro.jetnews.R
import com.meteoro.jetnews.data.Result
import com.meteoro.jetnews.data.posts.PostsRepository
import com.meteoro.jetnews.data.posts.impl.BlockingFakePostsRepository
import com.meteoro.jetnews.model.Post
import com.meteoro.jetnews.ui.AppDrawer
import com.meteoro.jetnews.ui.Screen
import com.meteoro.jetnews.ui.SwipeToRefreshLayout
import com.meteoro.jetnews.ui.ThemedPreview
import com.meteoro.jetnews.ui.state.UiState
import com.meteoro.jetnews.utils.produceUiState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Home,
                closeDrawer = { coroutineScope.launch { scaffoldState.drawerState.close() } },
                navigateTo = navigateTo
            )
        },
        topBar = {
            val title = stringResource(id = R.string.app_name)
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_jetnews_logo),
                            contentDescription = stringResource(id = R.string.cd_open_navigation_drawer)
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            LoadingContent(
                empty = posts.initialLoad,
                emptyContent = { FullScreenLoading() },
                loading = posts.loading,
                onRefresh = onRefreshPosts,
                content = {
                    HomeScreenErrorAndContent(
                        posts = posts,
                        onRefresh = { onRefreshPosts() },
                        navigateTo = navigateTo,
                        favorites = favorites,
                        onToggleFavorite = onToggleFavorite,
                        modifier = modifier
                    )
                }
            )
        }
    )
}

/**
 * Display an initial empty state or swipe to refresh content.
 *
 * @param empty (state) when true, display [emptyContent]
 * @param emptyContent (slot) the content to display for the empty state
 * @param loading (state) when true, display a loading spinner over [content]
 * @param onRefresh (event) event to request refresh
 * @param content (slot) the main content to show
 * */
@Composable
private fun LoadingContent(
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    loading: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    if (empty) {
        emptyContent()
    } else {
        SwipeToRefreshLayout(
            refreshingState = loading,
            onRefresh = onRefresh,
            refreshIndicator = {
                Surface(elevation = 10.dp, shape = CircleShape) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(36.dp)
                            .padding(4.dp)
                    )
                }
            },
            content = content
        )
    }
}

/**
 * Responsible for displaying any error conditions around [PostList].
 *
 * @param posts (state) list of posts and error state to diplay
 * @param onRefresh (event) request to refresh data
 * @param navigateTo (event) request navigation to [Screen]
 * @param favorites (state) all favorites
 * @param onToggleFavorite (event) request a single favorite be toggled
 * @param modifier modifier for root element
 * */
@Composable
private fun HomeScreenErrorAndContent(
    posts: UiState<List<Post>>,
    onRefresh: () -> Unit,
    navigateTo: (Screen) -> Unit,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (posts.data != null) {
        PostList(posts.data, navigateTo, favorites, onToggleFavorite, modifier)
    } else if (!posts.hasError) {
        // if there are no posts, and no error, let the user refresh manually
        TextButton(onClick = onRefresh, Modifier.fillMaxSize()) {
            Text(text = "Tap to load content", textAlign = TextAlign.Center)
        }
    } else {
        // there's currently an error showing, don't show any content
        Box(Modifier.fillMaxSize()) { /* empty screen */ }
    }
}

/**
 * Display a list of posts.
 *
 * When a post is clicked on, [navigateTo] will be called to navigate to the detail screen for that
 * post.
 *
 * @param posts (state) the list to display
 * @param navigateTo (event) request navigation to [Screen]
 * @param modifier modifier for the root element
 * */
@Composable
private fun PostList(
    posts: List<Post>,
    navigateTo: (Screen) -> Unit,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val postTop = posts[3]
    val postsSimple = posts.subList(0, 2)
    val postsPopular = posts.subList(2, 7)
    val postHistory = posts.subList(7, 10)

    postsPopular.size
    postHistory.size

    LazyColumn(modifier = modifier) {
        item { PostListTopSection(postTop, navigateTo) }
        item { PostListSimpleSection(postsSimple, navigateTo, favorites, onToggleFavorite) }
    }
}

/**
 * Full screen circular progress indicator
 * */
@Composable
private fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Top section of [PostList]
 *
 * @param post (state) highlighted post to display
 * @param navigateTo (event) request navigation to [Screen]
 * */
@Composable
private fun PostListTopSection(post: Post, navigateTo: (Screen) -> Unit) {
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
        text = "Top stories for you",
        style = MaterialTheme.typography.subtitle1
    )
    PostCardTop(
        post = post,
        modifier = Modifier.clickable(onClick = { navigateTo(Screen.Article(post.id)) })
    )
    PostListDivider()
}

/**
 * Full-width list items for [PostList]
 *
 * @param posts (state) to display
 * @param navigateTo (event) request navigation to [Screen]
 * */
@Composable
private fun PostListSimpleSection(
    posts: List<Post>,
    navigateTo: (Screen) -> Unit,
    favorites: Set<String>,
    onToggleFavorite: (String) -> Unit
) {
    Column {
        posts.forEach { post ->
            PostCardSimple(
                post = post,
                navigateTo = navigateTo,
                isFavorite = favorites.contains(post.id),
                onToggleFavorite = { onToggleFavorite(post.id) }
            )
            PostListDivider()
        }
    }
}

/**
 * Full-width divider with padding for [PostList]
 * */
@Composable
private fun PostListDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

@Preview("Home screen, open drawer")
@Composable
private fun PreviewDrawerOpen() {
    ThemedPreview {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Open)
        )
        HomeScreen(
            navigateTo = {},
            scaffoldState = scaffoldState,
            postsRepository = BlockingFakePostsRepository(LocalContext.current)
        )
    }
}

@Preview("Home screen dark theme")
@Composable
private fun PreviewHomeScreenBodyDark() {
    ThemedPreview(darkTheme = true) {
        val posts = loadFakePosts()
        PostList(posts, {}, setOf(), {})
    }
}

@Composable
private fun loadFakePosts(): List<Post> {
    val context = LocalContext.current
    val posts = runBlocking {
        BlockingFakePostsRepository(context).getPosts()
    }
    return (posts as Result.Success).data
}

@Preview("Home screen, open drawer dark theme")
@Composable
private fun PreviewDrawerOpenDark() {
    ThemedPreview(darkTheme = true) {
        val scaffoldState = rememberScaffoldState(
            drawerState = rememberDrawerState(DrawerValue.Open)
        )
        HomeScreen(
            navigateTo = {},
            scaffoldState = scaffoldState,
            postsRepository = BlockingFakePostsRepository(LocalContext.current)
        )
    }
}