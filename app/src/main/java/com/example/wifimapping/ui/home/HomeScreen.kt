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

package com.example.wifimapping.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wifimapping.InventoryTopAppBar
import com.example.wifimapping.R
import com.example.wifimapping.ui.AppViewModelProvider
import com.example.wifimapping.ui.navigation.NavigationDestination
import com.example.wifimapping.ui.theme.WifiMappingTheme
import com.example.wifimapping.ui.viewmodel.RoomParamsDetails
import com.example.wifimapping.ui.viewmodel.RoomParamsEntryViewModel
import com.example.wifimapping.ui.viewmodel.RoomParamsUiState
import kotlinx.coroutines.launch

object ItemEntryDestination : NavigationDestination {
    override val route = "roomparams_entry"
    override val titleRes = R.string.room_params_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEntryScreen(
    navigateToPreviewGrid: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = false,
    viewModel: RoomParamsEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(ItemEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        RoomParamsEntryBody(
            roomParamsUiState = viewModel.roomParamsUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveRoomParams()
                    navigateToPreviewGrid()
                }
            },
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding()
                )
        )
    }
}

@Composable
fun RoomParamsEntryBody(
    roomParamsUiState: RoomParamsUiState,
    onItemValueChange: (RoomParamsDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text("Input Data",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(bottom = 15.dp))
        RoomParamsInputForm(
            roomParamsDetails = roomParamsUiState.roomParamsDetails,
            onValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth(),
            showWarning = !roomParamsUiState.isEntryValid
        )
        Button(
            onClick = onSaveClick,
            enabled = roomParamsUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun RoomParamsInputForm(
    roomParamsDetails: RoomParamsDetails,
    modifier: Modifier = Modifier,
    onValueChange: (RoomParamsDetails) -> Unit = {},
    enabled: Boolean = true,
    showWarning : Boolean
) {
    Column(
    ) {
        OutlinedTextField(
            value = roomParamsDetails.roomName,
            onValueChange = { onValueChange(roomParamsDetails.copy(roomName = it)) },
            label = { Text(stringResource(R.string.room_name)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = roomParamsDetails.length,
            onValueChange = { onValueChange(roomParamsDetails.copy(length = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.room_length)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = roomParamsDetails.width,
            onValueChange = { onValueChange(roomParamsDetails.copy(width = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.room_width)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = roomParamsDetails.gridDistance,
            onValueChange = { onValueChange(roomParamsDetails.copy(gridDistance = it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text(stringResource(R.string.grid_distance)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        if (showWarning) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemEntryScreenPreview() {
    WifiMappingTheme {
        RoomParamsEntryBody(roomParamsUiState = RoomParamsUiState(
            RoomParamsDetails(
                roomName = "Item name", length = "10.00", width = "5"
            )
        ), onItemValueChange = {}, onSaveClick = {})
    }
}
