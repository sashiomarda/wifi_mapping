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

package com.example.wifimapping.ui.previewGrid

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wifimapping.data.RoomParamsRepository
import com.example.wifimapping.ui.home.RoomParamsUiState
import com.example.wifimapping.ui.home.toRoomParamsUiState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update an item from the [RoomParamsRepository]'s data source.
 */
class PreviewGridViewModel(
    savedStateHandle: SavedStateHandle,
    private val roomParamsRepository: RoomParamsRepository
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var roomParamsUiState by mutableStateOf(RoomParamsUiState())
        private set

    init {
        viewModelScope.launch {
            roomParamsUiState = roomParamsRepository.getLastRoomParamsIdStream()
                .filterNotNull()
                .first()
                .toRoomParamsUiState(true)
        }
    }
}
