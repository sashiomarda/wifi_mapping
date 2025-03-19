package com.example.wifimapping.data

import kotlinx.coroutines.flow.Flow

class OfflineGridsRepository (private val gridDao: GridDao) : GridsRepository {
    override fun getAllGridsStream(): Flow<List<Grid>> = gridDao.getAllGrids()

    override fun getGridsByLayerStream(idLayer: Int): Flow<List<Grid>> = gridDao.getAllGridsByLayer(idLayer)

    override fun getGridStream(id: Int): Flow<Grid?> = gridDao.getGrid(id)

    override suspend fun insertGrid(grid: Grid) = gridDao.insert(grid)

    override suspend fun deleteGrid(grid: Grid) = gridDao.delete(grid)

    override suspend fun updateGrid(grid: Grid) = gridDao.update(grid)
}
