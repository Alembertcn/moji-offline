package com.demo.mojioffline.domain.repository

import com.demo.mojioffline.core.result.AppResult
import com.demo.mojioffline.domain.model.AppPreferences
import com.demo.mojioffline.domain.model.HistoryEntry
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun observeHistory(): Flow<AppResult<List<HistoryEntry>>>
    fun refresh()
    suspend fun addSampleLookups(): AppResult<Unit>
    suspend fun remove(id: String): AppResult<Unit>
    suspend fun clear(): AppResult<Unit>
}

interface PreferencesRepository {
    fun observePreferences(): Flow<AppPreferences>
    suspend fun update(transform: (AppPreferences) -> AppPreferences)
}
