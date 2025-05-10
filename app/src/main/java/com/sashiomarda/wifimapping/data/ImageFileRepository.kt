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

package com.sashiomarda.imageFilemapping.data

import com.sashiomarda.wifimapping.data.ImageFile

/**
 * Repository that provides insert, update, delete, and retrieve of [ImageFile] from a given data source.
 */
/**
 * Repository that provides insert, update, delete, and retrieve of [ImageFile] from a given data source.
 */
interface ImageFileRepository {

    suspend fun getImageFileByIdHistory(idHistory: Int): List<ImageFile>
    
    suspend fun insertImageFile(imageFile: ImageFile)

    /**
     * Delete item from the data source
     */
    suspend fun deleteImageFile(imageFile: ImageFile)

    /**
     * Update item in the data source
     */
    suspend fun updateImageFile(imageFile: ImageFile)
}