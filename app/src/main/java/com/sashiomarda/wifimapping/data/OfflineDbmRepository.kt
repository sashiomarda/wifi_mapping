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

import com.sashiomarda.wifimapping.data.Dbm
import com.sashiomarda.wifimapping.data.DbmDao
import kotlinx.coroutines.flow.Flow

class OfflineDbmRepository(private val dbmDao: DbmDao) : DbmRepository {

    override fun getDbmByIdHistory(idHistory: Int): Flow<List<Dbm>> =
        dbmDao.getDbmByIdHistory(idHistory)

    override suspend fun getDbmByIdHistoryLayerNo(idHistory: Int, layerNo: Int): List<Dbm> =
        dbmDao.getDbmByIdHistoryLayerNo(idHistory, layerNo)

    override suspend fun insertDbm(dbm: Dbm) = dbmDao.insert(dbm)

    override suspend fun deleteDbm(dbm: Dbm) = dbmDao.delete(dbm)

    override suspend fun updateDbm(dbm: Dbm) = dbmDao.update(dbm)

    override suspend fun getLastDbm(): Dbm? = dbmDao.getLastDbm()

}
