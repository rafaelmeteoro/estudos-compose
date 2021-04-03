package com.meteoro.jetnews

import android.content.Context
import com.meteoro.jetnews.data.AppContainer
import com.meteoro.jetnews.data.interests.InterestsRepository
import com.meteoro.jetnews.data.interests.impl.FakeInterestsRepository
import com.meteoro.jetnews.data.posts.PostsRepository
import com.meteoro.jetnews.data.posts.impl.BlockingFakePostsRepository

class TestAppContainer(private val context: Context) : AppContainer {

    override val postsRepository: PostsRepository by lazy {
        BlockingFakePostsRepository(context)
    }

    override val interestsRepository: InterestsRepository by lazy {
        FakeInterestsRepository()
    }
}