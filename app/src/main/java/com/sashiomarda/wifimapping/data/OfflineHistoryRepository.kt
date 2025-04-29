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

class OfflineHistoryRepository(private val historyDao: HistoryDao) : HistoryRepository {
    override fun getAllHistoryStream(): Flow<List<History>> = historyDao.getAllHistory()

    override suspend fun getLastHistoryIdStream(): History? = historyDao.getLastHistoryId()

    override suspend fun getHistoryByIdStream(id: Int): Flow<History?> = historyDao.getHistoryById(id)

    override fun getHistoryByIdRoomStream(idRoom: Int): Flow<List<History>> = historyDao.getHistoryByIdRoom(idRoom)

    override suspend fun insertHistory(history: History) = historyDao.insert(history)

    override suspend fun deleteHistory(history: History) = historyDao.delete(history)

    override suspend fun updateHistory(history: History) = historyDao.update(history)
}
