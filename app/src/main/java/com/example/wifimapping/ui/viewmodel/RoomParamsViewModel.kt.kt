package com.example.wifimapping.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wifimapping.data.RoomParams
import com.example.wifimapping.data.RoomParamsRepository
import com.example.wifimapping.ui.roomList.RoomListDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update an item from the [com.example.wifimapping.data.RoomParamsRepository]'s data source.
 */
@RequiresApi(Build.VERSION_CODES.O)
class RoomParamsViewModel(
    savedStateHandle: SavedStateHandle,
    private val roomParamsRepository: RoomParamsRepository
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var roomParamsUiState by mutableStateOf(RoomParamsUiState())
        private set

    var roomParamByIdsUiState by mutableStateOf(RoomParamsUiState())
        private set

    private val idCollectData: Int = checkNotNull(savedStateHandle[RoomListDestination.idCollectData])

    val allRoomUiStateList: StateFlow<RoomParamsList> =
        roomParamsRepository.getAllRoomParamsStream().map { RoomParamsList(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = RoomParamsList()
            )

    init {
        viewModelScope.launch {
            roomParamsUiState = roomParamsRepository.getLastRoomParamsIdStream()
                .filterNotNull()
                .first()
                .toRoomParamsUiState(true)

            roomParamByIdsUiState = roomParamsRepository.getRoomParamsStream(idCollectData)
                .filterNotNull()
                .first()
                .toRoomParamsUiState(true)
        }
    }

    fun getIdCollectData():Int{
        return  idCollectData
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

@RequiresApi(Build.VERSION_CODES.O)
data class RoomParamsList(val roomParamList: List<RoomParams> = listOf())