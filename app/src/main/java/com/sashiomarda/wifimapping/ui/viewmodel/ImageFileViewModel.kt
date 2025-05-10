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

package com.sashiomarda.wifimapping.ui.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sashiomarda.imageFilemapping.data.ImageFileRepository
import com.sashiomarda.wifimapping.data.ImageFile
import com.sashiomarda.wifimapping.ui.downloadMap.DownloadMapDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * ViewModel to validate and insert wifi in the Room database.
 */
@RequiresApi(Build.VERSION_CODES.O)
class ImageFileViewModel(
    savedStateHandle: SavedStateHandle,
    private val imageFileRepository: ImageFileRepository,
) : ViewModel() {

    val _allImageFileList = MutableStateFlow<List<ImageFile>>(emptyList())
    val allImageFileList: StateFlow<List<ImageFile>> = _allImageFileList

    private val idHistory: Int = checkNotNull(savedStateHandle[DownloadMapDestination.idHistory])

    init {
        viewModelScope.launch {
            _allImageFileList.value = imageFileRepository.getImageFileByIdHistory(idHistory)
        }
    }

    private var updateAllImageJob: Job? = null

    fun startUpdateJob() {
        updateAllImageJob = viewModelScope.launch {
            while (isActive) {
                _allImageFileList.value = imageFileRepository.getImageFileByIdHistory(idHistory)
                delay(500)
            }
        }
    }

    suspend fun saveImageFile(imageFile: ImageFile){
        imageFileRepository.insertImageFile(imageFile)
    }
}