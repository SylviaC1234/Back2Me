package com.sylvia.back2me.models

data class LostItem(
    var id: String? = "",
    var title: String = "",
    var type: String = "Lost",
    var location: String = "",
    var description: String = "",
    var imageUrl: String? = "",
    var contact: String = "",
    var timestamp: Long = System.currentTimeMillis()



) {
    val userId: Any
        get() {
            TODO()
        }
}

