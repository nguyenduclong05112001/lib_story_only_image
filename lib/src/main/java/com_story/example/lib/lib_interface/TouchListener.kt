package com_story.example.lib.lib_interface

interface TouchListener {
    fun touchPull()
    fun touchDown(xValue: Float, yValue: Float)
    fun touchUp()
}