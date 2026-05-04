package com.sylvia.back2me.models

data class PostItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "",     // "Lost" or "Found"
    val location: String = "",
    val contact: String = "",
    val imageUrl: String = "",
    val userId: String = ""    // This is the one you need for the "My Posts" filter!
)