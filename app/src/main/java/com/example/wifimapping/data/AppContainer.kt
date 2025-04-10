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

package com.example.wifimapping.data

import android.content.Context
import com.example.gridmapping.data.DbmRepository
import com.example.gridmapping.data.GridRepository
import com.example.gridmapping.data.OfflineDbmRepository
import com.example.gridmapping.data.OfflineGridRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val roomParamsRepository : RoomParamsRepository
    val wifiRepository : WifiRepository
    val gridRepository : GridRepository
    val dbmRepository : DbmRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineRoomParamsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [RoomParamsRepository]
     */
    override val roomParamsRepository : RoomParamsRepository by lazy {
        OfflineRoomParamsRepository(WifiMappingDatabase.getDatabase(context).roomParamsDao())
    }

    override val wifiRepository : WifiRepository by lazy {
        OfflineWifiRepository(WifiMappingDatabase.getDatabase(context).wifiDao())
    }

    override val gridRepository : GridRepository by lazy {
        OfflineGridRepository(WifiMappingDatabase.getDatabase(context).gridDao())
    }

    override val dbmRepository : DbmRepository by lazy {
        OfflineDbmRepository(WifiMappingDatabase.getDatabase(context).dbmDao())
    }
}
