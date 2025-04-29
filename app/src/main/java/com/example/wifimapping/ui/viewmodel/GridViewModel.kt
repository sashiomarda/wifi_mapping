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

package com.example.wifimapping.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gridmapping.data.GridRepository
import com.example.wifimapping.data.Grid
import com.example.wifimapping.ui.previewGrid.PreviewGridDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

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

    private val idHistory: Int = checkNotNull(savedStateHandle[PreviewGridDestination.idHistory])

    val gridUiStateList: StateFlow<GridUiStateList> =
        gridRepository.getGridByIdHistoryStream(idHistory = idHistory)
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