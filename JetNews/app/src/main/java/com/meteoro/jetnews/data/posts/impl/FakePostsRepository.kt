package com.meteoro.jetnews.data.posts.impl

import android.content.res.Resources
import com.meteoro.jetnews.data.Result
import com.meteoro.jetnews.data.posts.PostsRepository
import com.meteoro.jetnews.model.Post
import com.meteoro.jetnews.utils.addOrRemove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Implementation of PostsRepository that returns a hardcoded list of
 * posts with resources after some delay in a background thread.
 * */
@OptIn(ExperimentalCoroutinesApi::class)
class FakePostsRepository(private val resources: Resources) : PostsRepository {

    // for now, store these in memory
    private val favorites = MutableStateFlow<Set<String>>(setOf())

    // Used to make suspend functions that read and update state safe to call from any thread
    private val mutex = Mutex()

    override suspend fun getPost(postId: String): Result<Post> {
        return withContext(Dispatchers.IO) {
            val post = posts.find { it.id == postId }
            if (post == null) {
                Result.Error(IllegalArgumentException("Post not found"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPosts(): Result<List<Post>> {
        return withContext(Dispatchers.IO) {
            delay(800) // pretend we're on a slow network
            if (shouldRandomlyFail()) {
                Result.Error(IllegalStateException())
            } else {
                Result.Success(posts)
            }
        }
    }

    override fun observeFavorites(): Flow<Set<String>> = favorites

    override suspend fun toggleFavorite(postId: String) {
        mutex.withLock {
            val set = favorites.value.toMutableSet()
            set.addOrRemove(postId)
            favorites.value = set.toSet()
        }
    }

    // used to drive "random" failure in a predictable pattern, making the first request always
    // succeed
    private var requestCount = 0

    /**
     * Randomly fail some loads to simulate a real network.
     *
     * This will fail deterministically every 5 requests
     * */
    private fun shouldRandomlyFail(): Boolean = ++requestCount % 5 == 0
}