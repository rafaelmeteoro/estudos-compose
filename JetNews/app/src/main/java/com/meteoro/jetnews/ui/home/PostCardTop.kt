package com.meteoro.jetnews.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meteoro.jetnews.data.posts.impl.post2
import com.meteoro.jetnews.data.posts.impl.posts
import com.meteoro.jetnews.model.Post
import com.meteoro.jetnews.ui.ThemedPreview

@Composable
fun PostCardTop(post: Post, modifier: Modifier = Modifier) {
    // TUTORIAL CONTENT STARTS HERE
    val typography = MaterialTheme.typography
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val imageModifier = Modifier
            .heightIn(min = 180.dp)
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
        Image(
            painter = painterResource(post.imageId),
            contentDescription = null, // decorative
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = post.title,
            style = typography.h6
        )
        Text(
            text = post.metadata.author.name,
            style = typography.body2
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = "${post.metadata.date} - ${post.metadata.readTimeMinutes} min read",
                style = typography.body2
            )
        }
    }
}

@Preview("Default colors")
@Composable
fun TutorialPreview() {
    TutorialPreviewTemplate()
}

@Preview("Dark theme")
@Composable
fun TutorialPreviewDark() {
    TutorialPreviewTemplate(darkTheme = true)
}

@Preview("Font scaling 1.5", fontScale = 1.5f)
@Composable
fun TutorialPreviewFontScale() {
    TutorialPreviewTemplate()
}

@Composable
fun TutorialPreviewTemplate(
    darkTheme: Boolean = false
) {
    val previewsPosts = posts.subList(1, 2)
    val post = previewsPosts[0]

    ThemedPreview(darkTheme) {
        PostCardTop(post)
    }
}

@Preview("Post card top")
@Composable
fun PreviewPostCardTop() {
    ThemedPreview {
        PostCardTop(post = post2)
    }
}

@Preview("Post card top dark theme")
@Composable
fun PreviewPostCardTopDark() {
    ThemedPreview(darkTheme = true) {
        PostCardTop(post = post2)
    }
}