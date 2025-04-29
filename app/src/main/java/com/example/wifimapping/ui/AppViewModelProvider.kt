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

package com.example.wifimapping.ui

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.wifimapping.WifiMappingApplication
import com.example.wifimapping.ui.viewmodel.DbmViewModel
import com.example.wifimapping.ui.viewmodel.GridViewModel
import com.example.wifimapping.ui.viewmodel.HistoryViewModel
import com.example.wifimapping.ui.viewmodel.RoomParamsEntryViewModel
import com.example.wifimapping.ui.viewmodel.RoomParamsViewModel
import com.example.wifimapping.ui.viewmodel.WifiViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    val Factory = viewModelFactory {
        // Initializer for ItemEntryViewModel
        initializer {
            RoomParamsEntryViewModel(inventoryApplication().container.roomParamsRepository)
        }

        initializer {
            RoomParamsViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.roomParamsRepository,
                inventoryApplication().container.historyRepository
            )
        }

        // Initializer for ItemEntryViewModel
        initializer {
            WifiViewModel(inventoryApplication().container.wifiRepository)
        }

        initializer {
            GridViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.gridRepository,
            )
        }

        initializer {
            DbmViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.dbmRepository
            )
        }

        initializer {
            HistoryViewModel(
                this.createSavedStateHandle(),
                inventoryApplication().container.historyRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [WifiMappingApplication].
 */
fun CreationExtras.inventoryApplication(): WifiMappingApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as WifiMappingApplication)
