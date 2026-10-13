package com.demo.mojioffline.data.repository

import com.demo.mojioffline.core.result.AppError
import com.demo.mojioffline.core.result.AppResult
import com.demo.mojioffline.core.result.StorageException
import com.demo.mojioffline.core.result.runCatchingApp
import com.demo.mojioffline.data.local.HistoryLocalStore
import com.demo.mojioffline.data.local.HistorySeed
import com.demo.mojioffline.data.local.PreferencesLocalStore
import com.demo.mojioffline.domain.model.AppPreferences
import com.demo.mojioffline.domain.model.HistoryEntry
import com.demo.mojioffline.domain.repository.HistoryRepository
import com.demo.mojioffline.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlin.random.Random

class DefaultHistoryRepository(
    private val localStore: HistoryLocalStore,
    private val preferencesRepository: PreferencesRepository,
    private val clockMs: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) : HistoryRepository {

    private val mutex = Mutex()
    private val refreshTick = MutableStateFlow(0)

    init {
        ensureSeeded()
    }

    override fun observeHistory(): Flow<AppResult<List<HistoryEntry>>> =
        combine(
            refreshTick.asStateFlow(),
            preferencesRepository.observePreferences(),
        ) { _, preferences ->
            if (preferences.forceHistoryError) {
                AppResult.Failure(AppError.Storage)
            } else {
                runCatchingApp {
                    localStore.readAll().sortedByDescending { it.lookedUpAtEpochMs }
                }
            }
        }

    override fun refresh() {
        refreshTick.update { it + 1 }
    }

    override suspend fun addSampleLookups(): AppResult<Unit> = mutate {
        val existing = localStore.readAll().associateBy { it.word }
        val now = clockMs()
        val additions = HistorySeed.sampleEntries(now)
            .mapIndexed { index, entry ->
                entry.copy(
                    id = newId(),
                    lookedUpAtEpochMs = now - index * 1_000L,
                )
            }
            .filterNot { existing.containsKey(it.word) }
        localStore.writeAll(additions + localStore.readAll())
    }

    override suspend fun remove(id: String): AppResult<Unit> = mutate {
        localStore.writeAll(localStore.readAll().filterNot { it.id == id })
    }

    override suspend fun clear(): AppResult<Unit> = mutate {
        localStore.clear()
    }

    private fun ensureSeeded() {
        try {
            if (localStore.readAll().isEmpty()) {
                localStore.writeAll(HistorySeed.sampleEntries(clockMs()))
            }
        } catch (_: StorageException) {
            // Leave empty; UI will show error/empty after first observe.
        }
    }

    private suspend fun mutate(block: () -> Unit): AppResult<Unit> =
        mutex.withLock {
            val result = runCatchingApp(block)
            refreshTick.update { it + 1 }
            result
        }

    private fun newId(): String = "${clockMs()}-${Random.nextInt(100_000, 999_999)}"
}

class DefaultPreferencesRepository(
    private val localStore: PreferencesLocalStore,
) : PreferencesRepository {

    private val preferences = MutableStateFlow(localStore.read())

    override fun observePreferences(): Flow<AppPreferences> = preferences.asStateFlow()

    override suspend fun update(transform: (AppPreferences) -> AppPreferences) {
        val next = transform(preferences.value)
        localStore.write(next)
        preferences.value = next
    }
}
