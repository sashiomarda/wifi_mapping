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
import androidx.lifecycle.ViewModel
import com.sashiomarda.wifimapping.data.RoomParams
import com.sashiomarda.wifimapping.data.RoomParamsRepository
import kotlin.text.isNotBlank
import kotlin.text.toIntOrNull

/**
 * ViewModel to validate and insert roomParams in the Room database.
 */
@RequiresApi(Build.VERSION_CODES.O)
class RoomParamsEntryViewModel(private val roomParamsRepository: RoomParamsRepository) : ViewModel() {

    /**
     * Holds current roomParams ui state
     */
    var roomParamsUiState by mutableStateOf(RoomParamsUiState())
        private set

    /**
     * Updates the [roomParamsUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUiState(roomParamsDetails: RoomParamsDetails) {
        roomParamsUiState =
            RoomParamsUiState(roomParamsDetails = roomParamsDetails, isEntryValid = validateInput(roomParamsDetails))
    }

    private fun validateInput(uiState: RoomParamsDetails = roomParamsUiState.roomParamsDetails): Boolean {
        return with(uiState) {
            roomName.isNotBlank() && length.isNotBlank() && width.isNotBlank()
                    && gridDistance.isNotBlank() && layerCount.isNotBlank()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveRoomParams() {
        if (validateInput()) {
            roomParamsRepository.insertRoomParams(roomParamsUiState.roomParamsDetails.toRoomParams())
        }
    }
}

/**
 * Represents Ui State for an RoomParams.
 */
@RequiresApi(Build.VERSION_CODES.O)
data class RoomParamsUiState(
    val roomParamsDetails: RoomParamsDetails = RoomParamsDetails(),
    val isEntryValid: Boolean = false
)
@RequiresApi(Build.VERSION_CODES.O)
data class RoomParamsDetails(
    val id: Int = 0,
    val roomName: String = "",
    val length: String = "",
    val width: String = "",
    val gridDistance: String = "",
    val layerCount: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@RequiresApi(Build.VERSION_CODES.O)
fun RoomParamsDetails.toRoomParams(): RoomParams = RoomParams(
    id = id,
    roomName = roomName,
    length = length.toIntOrNull() ?: 0,
    width = width.toIntOrNull() ?: 0,
    gridDistance = gridDistance.toIntOrNull() ?: 0,
    timestamp = timestamp,
    layerCount = layerCount.toIntOrNull() ?: 0,
)

//fun RoomParams.formatedPrice(): String {
//    return NumberFormat.getCurrencyInstance().format(price)
//}

/**
 * Extension function to convert [RoomParams] to [RoomParamsUiState]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun RoomParams.toRoomParamsUiState(isEntryValid: Boolean = false): RoomParamsUiState = RoomParamsUiState(
    roomParamsDetails = this.toRoomParamsDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [RoomParams] to [RoomParamsDetails]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun RoomParams.toRoomParamsDetails(): RoomParamsDetails = RoomParamsDetails(
    id = id,
    roomName = roomName,
    length = length.toString(),
    width = width.toString(),
    gridDistance = gridDistance.toString(),
)
