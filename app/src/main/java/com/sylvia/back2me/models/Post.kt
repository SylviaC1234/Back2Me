package com.sylvia.back2me.models

import com.sylvia.back2me.ui.screens.posts.Post


data class PostItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "", // e.g. "lost" or "found"
    val post: Post
)
