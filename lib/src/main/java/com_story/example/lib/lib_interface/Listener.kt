package com_story.example.lib.lib_interface

interface Listener {
    fun onDismissed()
    fun onShouldInterceptTouchEvent(): Boolean
}