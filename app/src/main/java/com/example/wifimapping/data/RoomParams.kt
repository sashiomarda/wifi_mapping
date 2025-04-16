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

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "room_params")
data class RoomParams(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: LocalDateTime,
    val roomName: String,
    val length: Int,
    val width: Int,
    val gridDistance: Int,
)
