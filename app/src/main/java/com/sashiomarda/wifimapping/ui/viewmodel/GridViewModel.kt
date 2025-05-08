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
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sashiomarda.gridmapping.data.GridRepository
import com.sashiomarda.wifimapping.data.Grid
import com.sashiomarda.wifimapping.ui.previewGrid.PreviewGridDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * ViewModel to validate and insert wifi in the Room database.
 */
@RequiresApi(Build.VERSION_CODES.O)
class GridViewModel(
    savedStateHandle: SavedStateHandle,
    private val gridRepository: GridRepository,
) : ViewModel() {

    var gridUiState by mutableStateOf(GridUiState())
        private set

    var previousGrid by mutableStateOf(GridDetails())
        private set

    var currentGrid by mutableStateOf(GridDetails())
        private set

    private var _selectedLayer = MutableStateFlow<Int>(1)
    val selectedLayer: StateFlow<Int> = _selectedLayer

    private val _gridList = MutableStateFlow<List<Grid>>(emptyList())
    val gridList: StateFlow<List<Grid>> = _gridList

    private val idHistory: Int = checkNotNull(savedStateHandle[PreviewGridDestination.idHistory])

    var gridUiStateList: StateFlow<GridUiStateList> =
        gridRepository.getGridByIdHistoryLayerNoStream(idHistory = idHistory, layerNo = 1)
            .map { GridUiStateList(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = GridUiStateList()
            )

    suspend fun lastGridInputIdHistory() : Grid? {
        return gridRepository.getLastGridInputId()
    }

    suspend fun saveGrid(gridDetails: GridDetails) {
        gridRepository.insertGrid(gridDetails.toGrid())
    }

    suspend fun deleteGrid() {
        gridRepository.deleteGrid(gridUiState.gridDetails.toGrid())
    }

    suspend fun updateGrid() {
        gridRepository.updateGrid(gridUiState.gridDetails.toGrid())
    }

    suspend fun updateChosenGrid(prevGrid: Grid, currGrid: Grid) {
        previousGrid = prevGrid.toGridDetails()
        currentGrid = currGrid.toGridDetails()
        gridRepository.updateGrid(previousGrid.toGrid())
        gridRepository.updateGrid(currentGrid.toGrid())
    }

    suspend fun resetInputGrid() {
        gridRepository.resetInputGrid()
    }

    suspend fun resetIsClicked() {
        gridRepository.resetIsClicked()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateUiState(gridDetails: GridDetails) {
        gridUiState =
            GridUiState(gridDetails = gridDetails)
    }

    fun getIdHistory(): Int {
        return idHistory
    }

    suspend fun getGridByLayerNo(layerNo: Int): List<Grid> {
        return gridRepository.getGridByLayerNo(idHistory, layerNo).map { it }
    }

    init {
        viewModelScope.launch {
            _gridList.value =  getGridByLayerNo(_selectedLayer.value)
            resetIsClicked()
            updateSelectedLayer(_selectedLayer.value, true)
        }
    }

    suspend fun updateSelectedLayer(selectedLayer: Int, isUpdateCurrentGrid: Boolean){
        val oldGridList = _gridList.value
        _selectedLayer.value = selectedLayer
        _gridList.value =  getGridByLayerNo(selectedLayer)
        if (isUpdateCurrentGrid){
            gridRepository.updateGrid(_gridList.value[0].copy(isClicked = true))
            updateChosenGrid(oldGridList[0], _gridList.value[0].copy(isClicked = true))
        }
    }

    private var updateGridJob: Job? = null

    fun startUpdateGridJob() {
        updateGridJob = viewModelScope.launch {
            while (isActive) {
                updateSelectedLayer(_selectedLayer.value, false)
                delay(100)
            }
        }
    }
}
data class GridUiState(
    val gridDetails: GridDetails = GridDetails()
)

data class GridDetails(
    val id: Int = 0,
    val idRoom: Int = 0,
    val idHistory: Int = 0,
    val idWifi: Int = 0,
    val layerNo: Int = 1,
    val isClicked: Boolean = false,
)

fun GridDetails.toGrid(): Grid = Grid(
    id = id,
    idRoom = idRoom,
    idWifi = idWifi,
    layerNo = layerNo,
    isClicked = isClicked,
    idHistory = idHistory
)

/**
 * Extension function to convert [Grid] to [GridUiState]
 */
fun Grid.toGridUiState(): GridUiState = GridUiState(
    gridDetails = this.toGridDetails()
)


/**
 * Extension function to convert [Grid] to [GridDetails]
 */
fun Grid.toGridDetails(): GridDetails = GridDetails(
    id = id,
    idRoom = idRoom,
    idWifi = idWifi,
    layerNo = layerNo,
    isClicked = isClicked,
    idHistory = idHistory
)

data class GridUiStateList(val gridList: List<Grid> = listOf())