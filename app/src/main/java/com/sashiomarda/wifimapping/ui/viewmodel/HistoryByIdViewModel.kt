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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sashiomarda.wifimapping.data.History
import com.sashiomarda.wifimapping.data.HistoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * ViewModel to validate and insert roomParams in the Room database.
 */
@RequiresApi(Build.VERSION_CODES.O)
class HistoryByIdViewModel(
    savedStateHandle: SavedStateHandle,
    private val historyRepository: HistoryRepository) : ViewModel() {


    private var _historyByIdUiState = MutableStateFlow<HistoryDetails>(HistoryDetails())
    var historyByIdUiState: StateFlow<HistoryDetails> = _historyByIdUiState

    private var _idHistory = MutableStateFlow<Int>(0)
    val idHistory: StateFlow<Int> = _idHistory

    suspend fun getHistoryByIdHistory(idHistory: Int){
        _historyByIdUiState.value = historyRepository.getHistoryByIdStream(idHistory)
            .filterNotNull()
            .first()
            .toHistoryDetails()
    }

    suspend fun updateHistoryDb(history: History){
        historyRepository.updateHistory(history)
    }

    fun updateIdHistory(idHistory: Int){
        _idHistory.value = idHistory
    }
    private var updateGridJob: Job? = null

    fun startUpdateHistoryJob() {
        updateGridJob = viewModelScope.launch {
            while (isActive) {
                getHistoryByIdHistory(_idHistory.value)
                delay(100)
            }
        }
    }

    init {
        viewModelScope.launch {
            getHistoryByIdHistory(_idHistory.value)
        }
    }

}