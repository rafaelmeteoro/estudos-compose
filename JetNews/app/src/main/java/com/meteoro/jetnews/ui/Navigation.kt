package com.meteoro.jetnews.ui

/**
 * Screen names (used for serialization)
 * */
enum class ScreenName { HOME, INTERESTS, ARTICLE }

/**
 * Class defining the screens we have in the app: home, article details and interests
 * */
sealed class Screen(val id: ScreenName) {
    object Home : Screen(ScreenName.HOME)
    object Interests : Screen(ScreenName.INTERESTS)
    data class Article(val postId: String) : Screen(ScreenName.ARTICLE)
}

/**
 * Helpers for saving and loading a [Screen] object to a [Bundle].
 *
 * This allows us to persist navigation across process death, for example caused by a long video
 * call.
 * */
private const val SIS_SCREEN = "sis_screen"
private const val SIS_NAME = "screen_name"
private const val SIS_POST = "post"