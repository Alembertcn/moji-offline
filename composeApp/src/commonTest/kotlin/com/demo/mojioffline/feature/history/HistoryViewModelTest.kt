package com.demo.mojioffline.feature.history

import app.cash.turbine.test
import com.demo.mojioffline.core.result.AppError
import com.demo.mojioffline.core.result.AppResult
import com.demo.mojioffline.domain.model.HistoryEntry
import com.demo.mojioffline.domain.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeHistoryRepository
    private lateinit var viewModel: HistoryViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeHistoryRepository()
        viewModel = HistoryViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsEntriesIntoState() = runTest {
        repository.emit(AppResult.Success(listOf(sampleEntry())))
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.entries.size)
            assertEquals("勉強", state.entries.first().word)
            assertEquals(null, state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun mapsStorageFailureToErrorState() = runTest {
        repository.emit(AppResult.Failure(AppError.Storage))
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(AppError.Storage, state.error)
            assertTrue(state.entries.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun clearAllDelegatesToRepository() = runTest {
        repository.emit(AppResult.Success(listOf(sampleEntry())))
        advanceUntilIdle()

        viewModel.onIntent(HistoryIntent.ClearAll)
        advanceUntilIdle()

        assertTrue(repository.cleared)
    }

    @Test
    fun emptyListProducesEmptyStateFlag() = runTest {
        repository.emit(AppResult.Success(emptyList()))
        advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isEmpty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun sampleEntry() = HistoryEntry(
        id = "1",
        word = "勉強",
        reading = "べんきょう",
        meaningZh = "学习",
        meaningEn = "study",
        meaningJa = "学ぶこと",
        lookedUpAtEpochMs = 1_000L,
    )
}

private class FakeHistoryRepository : HistoryRepository {
    private val flow = MutableStateFlow<AppResult<List<HistoryEntry>>>(AppResult.Success(emptyList()))
    var cleared: Boolean = false
        private set

    fun emit(result: AppResult<List<HistoryEntry>>) {
        flow.value = result
    }

    override fun observeHistory(): Flow<AppResult<List<HistoryEntry>>> = flow
    override fun refresh() = Unit
    override suspend fun addSampleLookups(): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun remove(id: String): AppResult<Unit> = AppResult.Success(Unit)
    override suspend fun clear(): AppResult<Unit> {
        cleared = true
        flow.value = AppResult.Success(emptyList())
        return AppResult.Success(Unit)
    }
}
