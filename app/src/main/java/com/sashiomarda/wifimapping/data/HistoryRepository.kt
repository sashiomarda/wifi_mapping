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

package com.sashiomarda.wifimapping.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [History] from a given data source.
 */
/**
 * Repository that provides insert, update, delete, and retrieve of [History] from a given data source.
 */
interface HistoryRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllHistoryStream(): Flow<List<History>>

    suspend fun getLastHistoryIdStream(): History?
    /**
     * Retrieve an item from the given data source that matches with the [idRoom].
     */
    fun getHistoryByIdRoomStream(idRoom: Int): Flow<List<History>>

    suspend fun getHistoryByIdStream(id: Int): Flow<History?>

    /**
     * Insert item in the data source
     */
    suspend fun insertHistory(roomParams: History)

    /**
     * Delete item from the data source
     */
    suspend fun deleteHistory(roomParams: History)

    /**
     * Update item in the data source
     */
    suspend fun updateHistory(roomParams: History)
}