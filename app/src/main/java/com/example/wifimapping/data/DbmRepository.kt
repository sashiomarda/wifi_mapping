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

import com.example.wifimapping.data.Dbm
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Dbm] from a given data source.
 */
/**
 * Repository that provides insert, update, delete, and retrieve of [Dbm] from a given data source.
 */
interface DbmRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getDbmByIdHistory(idHistory: Int): Flow<List<Dbm>>

    suspend fun getLastDbm(): Dbm?
    /**
     * Insert item in the data source
     */
    suspend fun insertDbm(dbm: Dbm)

    /**
     * Delete item from the data source
     */
    suspend fun deleteDbm(dbm: Dbm)

    /**
     * Update item in the data source
     */
    suspend fun updateDbm(dbm: Dbm)
}