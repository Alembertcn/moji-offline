package com.demo.mojioffline.feature.history

import androidx.lifecycle.viewModelScope
import com.demo.mojioffline.core.mvi.MviViewModel
import com.demo.mojioffline.core.mvi.UiEffect
import com.demo.mojioffline.core.mvi.UiIntent
import com.demo.mojioffline.core.mvi.UiState
import com.demo.mojioffline.core.result.AppError
import com.demo.mojioffline.core.result.AppResult
import com.demo.mojioffline.domain.model.HistoryEntry
import com.demo.mojioffline.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = true,
    val entries: List<HistoryEntry> = emptyList(),
    val selected: HistoryEntry? = null,
    val error: AppError? = null,
) : UiState {
    val isEmpty: Boolean get() = !isLoading && error == null && entries.isEmpty()
}

sealed interface HistoryIntent : UiIntent {
    data object Refresh : HistoryIntent
    data object AddDemoEntries : HistoryIntent
    data object ClearAll : HistoryIntent
    data class Remove(val id: String) : HistoryIntent
    data class OpenDetails(val entry: HistoryEntry) : HistoryIntent
    data object DismissDetails : HistoryIntent
}

sealed interface HistoryEffect : UiEffect {
    data class Message(val text: String) : HistoryEffect
}

class HistoryViewModel(
    private val historyRepository: HistoryRepository,
) : MviViewModel<HistoryUiState, HistoryIntent, HistoryEffect>(HistoryUiState()) {

    init {
        historyRepository.observeHistory()
            .onEach(::reduce)
            .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: HistoryIntent) {
        when (intent) {
            HistoryIntent.Refresh -> {
                setState { copy(isLoading = true, error = null) }
                historyRepository.refresh()
            }

            HistoryIntent.AddDemoEntries -> viewModelScope.launch {
                when (val result = historyRepository.addSampleLookups()) {
                    is AppResult.Success -> sendEffect(HistoryEffect.Message("Demo entries added"))
                    is AppResult.Failure -> setState { copy(error = result.error, isLoading = false) }
                }
            }

            HistoryIntent.ClearAll -> viewModelScope.launch {
                when (val result = historyRepository.clear()) {
                    is AppResult.Success -> sendEffect(HistoryEffect.Message("History cleared"))
                    is AppResult.Failure -> setState { copy(error = result.error) }
                }
            }

            is HistoryIntent.Remove -> viewModelScope.launch {
                when (val result = historyRepository.remove(intent.id)) {
                    is AppResult.Success -> Unit
                    is AppResult.Failure -> setState { copy(error = result.error) }
                }
            }

            is HistoryIntent.OpenDetails -> setState { copy(selected = intent.entry) }
            HistoryIntent.DismissDetails -> setState { copy(selected = null) }
        }
    }

    private fun reduce(result: AppResult<List<HistoryEntry>>) {
        when (result) {
            is AppResult.Success -> setState {
                copy(isLoading = false, entries = result.data, error = null)
            }
            is AppResult.Failure -> setState {
                copy(isLoading = false, entries = emptyList(), error = result.error)
            }
        }
    }
}
