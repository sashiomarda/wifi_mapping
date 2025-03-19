package com.example.wifimapping.data

import kotlinx.coroutines.flow.Flow

interface GridsRepository {
    /**
     * Retrieve all the grids from the the given data source.
     */
    fun getAllGridsStream(): Flow<List<Grid>>

    fun getGridsByLayerStream(idLayer: Int): Flow<List<Grid>>

    /**
     * Retrieve an grid from the given data source that matches with the [id].
     */
    fun getGridStream(id: Int): Flow<Grid?>

    /**
     * Insert grid in the data source
     */
    suspend fun insertGrid(grid: Grid)

    /**
     * Delete grid from the data source
     */
    suspend fun deleteGrid(grid: Grid)

    /**
     * Update grid in the data source
     */
    suspend fun updateGrid(grid: Grid)
}