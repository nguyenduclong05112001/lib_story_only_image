package com_story.example.lib.lib_ui

import java.util.*

object Utils {
    fun getDurationBetweenDates(d1: Date, d2: Date): String {
        val diff = d1.time - d2.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val formattedDiff = StringBuilder()
        if (days != 0L) {
            return formattedDiff.append(Math.abs(days).toString() + "d").toString()
        }
        if (hours != 0L) {
            return formattedDiff.append(Math.abs(hours).toString() + "h").toString()
        }
        if (minutes != 0L) {
            return formattedDiff.append(Math.abs(minutes).toString() + "m").toString()
        }
        return if (seconds != 0L) {
            formattedDiff.append(Math.abs(seconds).toString() + "s").toString()
        } else ""
    }
}