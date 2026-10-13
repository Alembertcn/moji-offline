package com.demo.mojioffline.data

import app.cash.turbine.test
import com.demo.mojioffline.core.result.AppError
import com.demo.mojioffline.core.result.AppResult
import com.demo.mojioffline.data.local.HistoryLocalStore
import com.demo.mojioffline.data.local.PreferencesLocalStore
import com.demo.mojioffline.data.repository.DefaultHistoryRepository
import com.demo.mojioffline.data.repository.DefaultPreferencesRepository
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DefaultHistoryRepositoryTest {

    @Test
    fun seedsOnFirstLaunch() = runTest {
        val settings = MapSettings()
        val preferences = DefaultPreferencesRepository(PreferencesLocalStore(settings))
        val repository = DefaultHistoryRepository(
            localStore = HistoryLocalStore(settings),
            preferencesRepository = preferences,
            clockMs = { 1_000_000L },
        )

        repository.observeHistory().test {
            val result = awaitItem()
            assertIs<AppResult.Success<*>>(result)
            val entries = (result as AppResult.Success).data
            assertTrue(entries.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun forceErrorPreferenceEmitsFailure() = runTest {
        val settings = MapSettings()
        val preferences = DefaultPreferencesRepository(PreferencesLocalStore(settings))
        val repository = DefaultHistoryRepository(
            localStore = HistoryLocalStore(settings),
            preferencesRepository = preferences,
            clockMs = { 1_000_000L },
        )

        preferences.update { it.copy(forceHistoryError = true) }

        repository.observeHistory().test {
            val result = awaitItem()
            assertIs<AppResult.Failure>(result)
            assertEquals(AppError.Storage, result.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun clearEmitsEmptySuccess() = runTest {
        val settings = MapSettings()
        val preferences = DefaultPreferencesRepository(PreferencesLocalStore(settings))
        val repository = DefaultHistoryRepository(
            localStore = HistoryLocalStore(settings),
            preferencesRepository = preferences,
            clockMs = { 1_000_000L },
        )

        repository.clear()

        repository.observeHistory().test {
            val result = awaitItem()
            assertIs<AppResult.Success<*>>(result)
            assertTrue((result as AppResult.Success).data.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
