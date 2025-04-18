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

package com.example.gridmapping.data

import com.example.wifimapping.data.Grid
import com.example.wifimapping.data.GridDao
import kotlinx.coroutines.flow.Flow

class OfflineGridRepository(private val gridDao: GridDao) : GridRepository {

    override fun getGridByIdCollectDataStream(idCollectData: Int): Flow<List<Grid>> =
        gridDao.getGridByIdCollectData(idCollectData)

    override suspend fun insertGrid(grid: Grid) = gridDao.insert(grid)

    override suspend fun deleteGrid(grid: Grid) = gridDao.delete(grid)

    override suspend fun updateGrid(grid: Grid) = gridDao.update(grid)

    override suspend fun resetInputGrid(): Int = gridDao.resetInputGrid()

    override suspend fun getLastGridInputId(): Grid? = gridDao.getLastGridInputId()

}
