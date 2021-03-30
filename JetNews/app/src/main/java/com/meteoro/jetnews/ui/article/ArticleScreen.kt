package com.meteoro.jetnews.ui.article

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.meteoro.jetnews.R
import com.meteoro.jetnews.data.posts.PostsRepository
import com.meteoro.jetnews.model.Post
import com.meteoro.jetnews.ui.home.BookmarkButton
import com.meteoro.jetnews.utils.produceUiState
import kotlinx.coroutines.launch

/**
 * Stateful Article Screen that manages state using [produceUiState]
 *
 * @param postId (state) the post to show
 * @param postsRepository data source for this screen
 * @param onBack (event) request back navigaton
 * */
@Composable
fun ArticleScreen(
    postId: String,
    postsRepository: PostsRepository,
    onBack: () -> Unit
) {
    val (post) = produceUiState(postsRepository, postId) {
        getPost(postId)
    }
    // TODO: handle errors when the repository is capable of creating them
    val postData = post.value.data ?: return

    // [collectAsState] will automatically collect a Flow<T> and return a State<T> object that
    // updates whenever the Flow emits a value. Collection is cancelled when [collectAsState] is
    // removed from the composition tree.
    val favorites by postsRepository.observeFavorites().collectAsState(setOf())
    val isFavorite = favorites.contains(postId)

    // Returns a [CoroutineScope] that is scoped to the lifecycle of [ArticleScreen]. When this
    // screen is removed from composition, the scope will be cancelled.
    val coroutineScope = rememberCoroutineScope()

    ArticleScreen(
        post = postData,
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = {
            coroutineScope.launch { postsRepository.toggleFavorite(postId) }
        }
    )
}

/**
 * Stateless Article Screen that displays a single post.
 *
 * @param post (state) item to display
 * @param onBack (event) request navigate back
 * @param isFavorite (state) is this item currently a favorite
 * @param onToggleFavorite (event) request that this post toggle it's favorite state
 * */
@Composable
fun ArticleScreen(
    post: Post,
    onBack: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    if (showDialog) {
        FunctionalityNotAvailablePopup { showDialog = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Published in: ${post.publication?.name}",
                        style = MaterialTheme.typography.subtitle2,
                        color = LocalContentColor.current
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_up)
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            val modifier = Modifier.padding(innerPadding)
            PostContent(post, modifier)
        },
        bottomBar = {
            BottomBar(
                post = post,
                onUnimplementedAction = { showDialog = true },
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite
            )
        }
    )
}

/**
 * Bottom bar for article screen
 *
 * @param post (state) used in share sheet to share the post
 * @param onUnimplementedAction (event) called when the user performs an unimplemented actioin
 * @param isFavorite (state) if this post is currently a favorite
 * @param onToggleFavorite (event) request this post toggle it's favorite status
 * */
@Composable
private fun BottomBar(
    post: Post,
    onUnimplementedAction: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    Surface(elevation = 2.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = onUnimplementedAction) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(R.string.cd_add_to_favorites)
                )
            }
            BookmarkButton(
                isBookmarked = isFavorite,
                onClick = onToggleFavorite
            )
            val context = LocalContext.current
            IconButton(onClick = { sharedPost(post, context) }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = stringResource(R.string.cd_share)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onUnimplementedAction) {
                Icon(
                    painter = painterResource(R.drawable.ic_text_settings),
                    contentDescription = stringResource(R.string.cd_text_settings)
                )
            }
        }
    }
}

/**
 * Display a popup explaining functionality not available.
 *
 * @param onDismiss (event) request the popup be dismissed
 * */
@Composable
private fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = "Functionality not available \uD83D\uDE48",
                style = MaterialTheme.typography.body2
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE")
            }
        }
    )
}

/**
 * Show a share sheet for post
 *
 * @param post to share
 * @param context Android context to show the share sheet in
 * */
private fun sharedPost(post: Post, context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, post.title)
        putExtra(Intent.EXTRA_TEXT, post.url)
    }
    context.startActivity(Intent.createChooser(intent, "Share post"))
}