package com.meteoro.jetchat.profile

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.meteoro.jetchat.data.meProfile

@Immutable
data class ProfileScreenState(
    val userId: String,
    @DrawableRes val photo: Int?,
    val name: String,
    val status: String,
    val displayName: String,
    val position: String,
    val twitter: String = "",
    val timeZone: String?, // Null if me
    val commonChannels: String? // Null if me
) {
    fun isMe() = userId == meProfile.userId
}