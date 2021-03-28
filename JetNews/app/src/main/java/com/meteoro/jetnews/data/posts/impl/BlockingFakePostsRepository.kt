package com.meteoro.jetnews.data.posts.impl

import android.content.Context
import com.meteoro.jetnews.data.Result
import com.meteoro.jetnews.data.posts.PostsRepository
import com.meteoro.jetnews.model.Post
import com.meteoro.jetnews.utils.addOrRemove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

/**
 * Implementation of PostsRepository that returns a hardcoded list of
 * posts with resources synchronously.
 * */
@OptIn(ExperimentalCoroutinesApi::class)
class BlockingFakePostsRepository(private val context: Context) : PostsRepository {

    // for now, keep the favorites in memory
    private val favorites = MutableStateFlow<Set<String>>(setOf())

    override suspend fun getPost(postId: String): Result<Post> {
        return withContext(Dispatchers.IO) {
            val post = posts.find { it.id == postId }
            if (post == null) {
                Result.Error(IllegalArgumentException("Unable to find post"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPosts(): Result<List<Post>> {
        return Result.Success(posts)
    }

    override fun observeFavorites(): Flow<Set<String>> = favorites

    override suspend fun toggleFavorite(postId: String) {
        val set = favorites.value.toMutableSet()
        set.addOrRemove(postId)
        favorites.value = set
    }
}