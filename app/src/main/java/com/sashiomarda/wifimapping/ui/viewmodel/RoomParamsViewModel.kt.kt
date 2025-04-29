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
import com.sashiomarda.wifimapping.data.RoomParams
import com.sashiomarda.wifimapping.data.RoomParamsRepository
import com.sashiomarda.wifimapping.ui.previewGrid.PreviewGridDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update an item from the [com.sashiomarda.wifimapping.data.RoomParamsRepository]'s data source.
 */
@RequiresApi(Build.VERSION_CODES.O)
class RoomParamsViewModel(
    savedStateHandle: SavedStateHandle,
    private val roomParamsRepository: RoomParamsRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var roomParamsUiState by mutableStateOf(RoomParamsUiState())
        private set

    var roomParamByIdsUiState by mutableStateOf(RoomParamsUiState())
        private set

    var historyByIdUiState by mutableStateOf(HistoryByIdUiState())
        private set

    private val idHistory: Int = checkNotNull(savedStateHandle[PreviewGridDestination.idHistory])

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

            historyByIdUiState = historyRepository.getHistoryByIdStream(idHistory)
                .filterNotNull()
                .first()
                .toHistoryByIdUiState()

            roomParamByIdsUiState = roomParamsRepository.getRoomParamsStream(historyByIdUiState.historyDetails.idRoom)
                .filterNotNull()
                .first()
                .toRoomParamsUiState(true)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

@RequiresApi(Build.VERSION_CODES.O)
data class RoomParamsList(val roomParamList: List<RoomParams> = listOf())

@RequiresApi(Build.VERSION_CODES.O)
data class HistoryByIdUiState(
    val historyDetails: HistoryDetails = HistoryDetails()
)

@RequiresApi(Build.VERSION_CODES.O)
fun History.toHistoryByIdUiState(): HistoryByIdUiState = HistoryByIdUiState(
    historyDetails = this.toHistoryDetails()
)