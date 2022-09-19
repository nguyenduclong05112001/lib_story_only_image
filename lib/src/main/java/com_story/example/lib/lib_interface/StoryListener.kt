package com_story.example.lib.lib_interface

interface StoryListener {
    fun startStories()
    fun pauseStories()
    fun nextStory()
    fun onDescriptionClickListener(position: Int)
}