package com_story.example.lib.lib_model

data class StoryNew(
    val docs: ArrayList<StoryDocsAPI>,
    val total: Int,
    val limit: Int,
    val page: Int,
    val pages: Int
)