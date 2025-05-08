/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sashiomarda.wifimapping.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sashiomarda.wifimapping.data.History
import com.sashiomarda.wifimapping.data.HistoryRepository
import com.sashiomarda.wifimapping.data.HistoryRoom
import com.sashiomarda.wifimapping.ui.history.HistoryDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to validate and insert roomParams in the Room database.
 */
@RequiresApi(Build.VERSION_CODES.O)
class HistoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val historyRepository: HistoryRepository) : ViewModel() {

    /**
     * Holds current roomParams ui state
     */
    var historyUiState by mutableStateOf(HistoryUiState())
        private set

    val historyAllUiStateList: StateFlow<HistoryRoomUiStateList> =
        historyRepository.getAllHistoryStream()
            .map { HistoryRoomUiStateList(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HistoryRoomUiStateList()
            )

    private val idRoom: Int = checkNotNull(savedStateHandle[HistoryDestination.idRoom])

    val historyByIdRoomUiStateList: StateFlow<HistoryRoomUiStateList> =
        historyRepository.getHistoryRoomByIdRoomStream(idRoom = idRoom)
            .map { HistoryRoomUiStateList(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HistoryRoomUiStateList()
            )
    /**
     * Updates the [historyUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUiState(historyDetails: HistoryDetails) {
        historyUiState =
            HistoryUiState(historyDetails = historyDetails)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveHistory(historyDetails: HistoryDetails) {
        historyRepository.insertHistory(historyDetails.toHistory())
    }

    suspend fun lastHistory() : History? {
        return historyRepository.getLastHistoryIdStream()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun getIdRoom():Int{
        return idRoom
    }
}

/**
 * Represents Ui State for an RoomParams.
 */
@RequiresApi(Build.VERSION_CODES.O)
data class HistoryUiState(
    val historyDetails: HistoryDetails = HistoryDetails()
)
@RequiresApi(Build.VERSION_CODES.O)
data class HistoryDetails(
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val idRoom: Int = 0,
    val isComplete: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
fun HistoryDetails.toHistory(): History = History(
    id = id,
    timestamp = timestamp,
    idRoom = idRoom,
    isComplete = isComplete
)

@RequiresApi(Build.VERSION_CODES.O)
fun History.toHistoryDetails(): HistoryDetails = HistoryDetails(
    id = id,
    timestamp = timestamp,
    idRoom = idRoom,
    isComplete = isComplete
)

data class HistoryUiStateList(val historyList: List<History> = listOf())

data class HistoryRoomUiStateList(val historyList: List<HistoryRoom> = listOf())