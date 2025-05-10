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

package com.sashiomarda.wifimapping.ui

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sashiomarda.wifimapping.WifiMappingApplication
import com.sashiomarda.wifimapping.ui.viewmodel.DbmViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.GridViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.HistoryByIdViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.HistoryViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.ImageFileViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.RoomParamsEntryViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.RoomParamsViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.WifiScannerViewModel
import com.sashiomarda.wifimapping.ui.viewmodel.WifiViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    val Factory = viewModelFactory {
        initializer {
            RoomParamsEntryViewModel(WifiMappingApplication().container.roomParamsRepository)
        }

        initializer {
            RoomParamsViewModel(
                this.createSavedStateHandle(),
                WifiMappingApplication().container.roomParamsRepository,
                WifiMappingApplication().container.historyRepository
            )
        }

        initializer {
            WifiViewModel(WifiMappingApplication().container.wifiRepository)
        }

        initializer {
            GridViewModel(
                this.createSavedStateHandle(),
                WifiMappingApplication().container.gridRepository,
                WifiMappingApplication().container.dbmRepository,
            )
        }

        initializer {
            DbmViewModel(
                this.createSavedStateHandle(),
                WifiMappingApplication().container.dbmRepository
            )
        }

        initializer {
            HistoryViewModel(
                this.createSavedStateHandle(),
                WifiMappingApplication().container.historyRepository
            )
        }

        initializer {
            WifiScannerViewModel(WifiMappingApplication())
        }

        initializer {
            HistoryByIdViewModel(
                this.createSavedStateHandle(),
                WifiMappingApplication().container.historyRepository
            )
        }

        initializer {
            ImageFileViewModel(
                this.createSavedStateHandle(),
                WifiMappingApplication().container.imageFileRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [WifiMappingApplication].
 */
fun CreationExtras.WifiMappingApplication(): WifiMappingApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as WifiMappingApplication)
