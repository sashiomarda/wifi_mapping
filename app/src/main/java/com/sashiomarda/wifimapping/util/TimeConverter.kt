package com.sashiomarda.wifimapping.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class TimeConverter {
    @SuppressLint("SimpleDateFormat")
    val FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun fromTimestamp(timeStamp: Long?): String? {
        return timeStamp?.let { FORMATTER.format(timeStamp) }
    }

    fun strToTimestamp(timeStamp: String?): Long? {
        return timeStamp?.let { FORMATTER.parse(it).time }
    }
}