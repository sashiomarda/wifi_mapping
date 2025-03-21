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

package com.example.wifimapping.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.wifimapping.data.RoomParams
import com.example.wifimapping.data.RoomParamsRepository
import kotlin.text.isNotBlank
import kotlin.text.toIntOrNull

/**
 * ViewModel to validate and insert roomParams in the Room database.
 */
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
    fun updateUiState(roomParamsDetails: RoomParamsDetails) {
        roomParamsUiState =
            RoomParamsUiState(roomParamsDetails = roomParamsDetails, isEntryValid = validateInput(roomParamsDetails))
    }

    private fun validateInput(uiState: RoomParamsDetails = roomParamsUiState.roomParamsDetails): Boolean {
        return with(uiState) {
            roomName.isNotBlank() && length.isNotBlank() && width.isNotBlank()
                    && gridDistance.isNotBlank() && layerDistance.isNotBlank()
        }
    }
    suspend fun saveRoomParams() {
        if (validateInput()) {
            roomParamsRepository.insertRoomParams(roomParamsUiState.roomParamsDetails.toRoomParams())
        }
    }
}

/**
 * Represents Ui State for an RoomParams.
 */
data class RoomParamsUiState(
    val roomParamsDetails: RoomParamsDetails = RoomParamsDetails(),
    val isEntryValid: Boolean = false
)

data class RoomParamsDetails(
    val id: Int = 0,
    val roomName: String = "",
    val length: String = "",
    val width: String = "",
    val gridDistance: String = "",
    val layerDistance: String = "",
)

/**
 * Extension function to convert [RoomParamsDetails] to [RoomParams]. If the value of [RoomParamsDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [RoomParamsDetails.quantity] is not a valid [Int], then the quantity will be set to 0
 */
fun RoomParamsDetails.toRoomParams(): RoomParams = RoomParams(
    id = id,
    roomName = roomName,
    length = length.toIntOrNull() ?: 0,
    width = width.toIntOrNull() ?: 0,
    gridDistance = gridDistance.toIntOrNull() ?: 0,
    layerDistance = layerDistance.toIntOrNull() ?: 0,
)

//fun RoomParams.formatedPrice(): String {
//    return NumberFormat.getCurrencyInstance().format(price)
//}

/**
 * Extension function to convert [RoomParams] to [RoomParamsUiState]
 */
fun RoomParams.toRoomParamsUiState(isEntryValid: Boolean = false): RoomParamsUiState = RoomParamsUiState(
    roomParamsDetails = this.toRoomParamsDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [RoomParams] to [RoomParamsDetails]
 */
fun RoomParams.toRoomParamsDetails(): RoomParamsDetails = RoomParamsDetails(
    id = id,
    roomName = roomName,
    length = length.toString(),
    width = width.toString(),
    gridDistance = gridDistance.toString(),
    layerDistance = layerDistance.toString(),
)
