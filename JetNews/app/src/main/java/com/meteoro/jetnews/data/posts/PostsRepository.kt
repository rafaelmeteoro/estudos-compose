package com.meteoro.jetnews.data.posts

import com.meteoro.jetnews.data.Result
import com.meteoro.jetnews.model.Post
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the Posts data layer.
 * */
interface PostsRepository {

    /**
     * Get a specific JetNews post.
     * */
    suspend fun getPost(postId: String): Result<Post>

    /**
     * Get JetNews posts
     * */
    suspend fun getPosts(): Result<List<Post>>

    /**
     * Observe the current favorites
     * */
    fun observeFavorites(): Flow<Set<String>>

    /**
     * Toggle a postId to be a favorite or not.
     * */
    suspend fun toggleFavorite(postId: String)
}