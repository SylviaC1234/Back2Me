package com.sylvia.back2me.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties // Tells Firebase to ignore any extra data it finds in the DB


data class LostItem(
    var id: String? = "",
    val title: String = "",
    val type: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val contact: String = "",
    val userId: String = "" // <--- Add this field
)