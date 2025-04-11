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

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gridmapping.data.DbmRepository
import com.example.wifimapping.data.Dbm
import com.example.wifimapping.screens.locateRouter.LocateRouterDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to validate and insert wifi in the Room database.
 */
class DbmViewModel(
    savedStateHandle: SavedStateHandle,
    private val dbmRepository: DbmRepository,
) : ViewModel() {

    var dbmUiState by mutableStateOf(DbmUiState())
        private set

    private val idCollectData: Int = checkNotNull(savedStateHandle[LocateRouterDestination.idCollectData])

    val dbmUiStateList: StateFlow<DbmUiStateList> =
        dbmRepository.getDbmByIdCollectData(idCollectData = idCollectData)
            .map { DbmUiStateList(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DbmUiStateList()
            )

    suspend fun saveDbm(dbmDetails: DbmDetails) {
        Log.d("gridDetails", dbmDetails.toString())
        dbmRepository.insertDbm(dbmDetails.toDbm())
    }
    suspend fun updateDbm(dbm: Dbm) {
        Log.d("dbm", dbm.toString())
        dbmRepository.updateDbm(dbm)
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}
data class DbmUiState(
    val dbmDetails: DbmDetails = DbmDetails()
)

data class DbmDetails(
    val id: Int = 0,
    val idCollectData: Int = 0,
    val idGrid: Int = 0,
    val layerNo: Int = 0,
    val dbm: Int = 0
)
//
///**
// * Extension function to convert [GridDetails] to [Grid]. If the value of [GridDetails.price] is
// * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
// * [GridDetails.quantity] is not a valid [Int], then the quantity will be set to 0
// */
fun DbmDetails.toDbm(): Dbm = Dbm(
    id = id,
    idCollectData = idCollectData,
    idGrid = idGrid,
    layerNo = layerNo,
    dbm = dbm
)
data class DbmUiStateList(val dbmList: List<Dbm> = listOf())