package com.meteoro.jetnews.data

import android.content.Context
import com.meteoro.jetnews.data.interests.InterestsRepository
import com.meteoro.jetnews.data.interests.impl.FakeInterestsRepository
import com.meteoro.jetnews.data.posts.PostsRepository
import com.meteoro.jetnews.data.posts.impl.FakePostsRepository

/**
 * Dependency Injection container at the application level.
 * */
interface AppContainer {
    val postsRepository: PostsRepository
    val interestsRepository: InterestsRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the hole app.
 * */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val postsRepository: PostsRepository by lazy {
        FakePostsRepository(
            resources = applicationContext.resources
        )
    }

    override val interestsRepository: InterestsRepository by lazy {
        FakeInterestsRepository()
    }
}