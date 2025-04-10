package com.example.wifimapping.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wifimapping.data.RoomParamsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update an item from the [com.example.wifimapping.data.RoomParamsRepository]'s data source.
 */
class RoomParamsViewModel(
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