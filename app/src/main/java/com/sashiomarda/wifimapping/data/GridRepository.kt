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

package com.sashiomarda.gridmapping.data

import com.sashiomarda.wifimapping.data.Grid
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Grid] from a given data source.
 */
/**
 * Repository that provides insert, update, delete, and retrieve of [Grid] from a given data source.
 */
interface GridRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getGridByIdRoomStream(idRoom: Int): Flow<List<Grid>>

    fun getGridByIdHistoryStream(idHistory: Int): Flow<List<Grid>>

    suspend fun getLastGridInputId(): Grid?
    /**
     * Insert item in the data source
     */
    suspend fun insertGrid(grid: Grid)

    /**
     * Delete item from the data source
     */
    suspend fun deleteGrid(grid: Grid)

    /**
     * Update item in the data source
     */
    suspend fun updateGrid(grid: Grid)

    suspend fun resetInputGrid(): Int

}