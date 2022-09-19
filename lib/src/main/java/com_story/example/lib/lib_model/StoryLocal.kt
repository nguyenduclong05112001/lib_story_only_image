package com_story.example.lib.lib_model

data class StoryLocal(
    val image: String,
    val video: String,
    val description: String,
    var duration: Long = 2000L
)