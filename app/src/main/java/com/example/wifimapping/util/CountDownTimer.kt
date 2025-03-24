package com.example.wifimapping.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

class CountDownTimer(
    private val initialTime: Int = 30,
    val minTime: Int = 0,
    private val timeDecrement: Int = 1,
    val timeDelayMillis: Long = 1000L,
) {
    var currentTime by mutableStateOf(initialTime)
        private set

    suspend fun run() {
        while (currentTime > minTime) {
            delay(timeDelayMillis)
            currentTime -= timeDecrement
        }
    }

    fun reset() {
        currentTime = initialTime
    }
}

val CountDownTimer.getCountDown: Int
    get() = currentTime