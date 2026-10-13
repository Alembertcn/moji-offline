package com.demo.mojioffline.feature.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.mojioffline.domain.model.AppLanguage
import com.demo.mojioffline.domain.model.HistoryEntry
import com.demo.mojioffline.ui.components.EmptyState
import com.demo.mojioffline.ui.components.ErrorState
import com.demo.mojioffline.ui.components.LoadingState
import com.demo.mojioffline.ui.i18n.LocalAppStrings
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryRoute(
    language: AppLanguage,
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HistoryEffect.Message -> snackbarHostState.showSnackbar(effect.text)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.historyTitle) },
                actions = {
                    TextButton(onClick = { viewModel.onIntent(HistoryIntent.AddDemoEntries) }) {
                        Text(strings.actionAddDemo)
                    }
                    TextButton(onClick = { viewModel.onIntent(HistoryIntent.ClearAll) }) {
                        Text(strings.actionClearAll)
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        when {
            state.isLoading && state.entries.isEmpty() && state.error == null -> {
                LoadingState(Modifier.padding(padding))
            }

            state.error != null -> {
                ErrorState(
                    title = strings.historyErrorTitle,
                    body = strings.historyErrorBody,
                    retryLabel = strings.actionRetry,
                    onRetry = { viewModel.onIntent(HistoryIntent.Refresh) },
                    modifier = Modifier.padding(padding),
                )
            }

            state.isEmpty -> {
                EmptyState(
                    title = strings.historyEmptyTitle,
                    body = strings.historyEmptyBody,
                    actionLabel = strings.actionAddDemo,
                    onAction = { viewModel.onIntent(HistoryIntent.AddDemoEntries) },
                    modifier = Modifier.padding(padding),
                )
            }

            else -> {
                HistoryList(
                    entries = state.entries,
                    language = language,
                    contentPadding = padding,
                    onOpen = { viewModel.onIntent(HistoryIntent.OpenDetails(it)) },
                    onDelete = { viewModel.onIntent(HistoryIntent.Remove(it)) },
                )
            }
        }
    }

    state.selected?.let { entry ->
        HistoryDetailsDialog(
            entry = entry,
            language = language,
            onDismiss = { viewModel.onIntent(HistoryIntent.DismissDetails) },
            onDelete = {
                viewModel.onIntent(HistoryIntent.Remove(entry.id))
                viewModel.onIntent(HistoryIntent.DismissDetails)
            },
        )
    }
}

@Composable
private fun HistoryList(
    entries: List<HistoryEntry>,
    language: AppLanguage,
    contentPadding: PaddingValues,
    onOpen: (HistoryEntry) -> Unit,
    onDelete: (String) -> Unit,
) {
    val strings = LocalAppStrings.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding() + 16.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(entries, key = { it.id }) { entry ->
            HistoryRow(
                entry = entry,
                language = language,
                deleteLabel = strings.actionDelete,
                onClick = { onOpen(entry) },
                onDelete = { onDelete(entry.id) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun HistoryRow(
    entry: HistoryEntry,
    language: AppLanguage,
    deleteLabel: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(entry.word, style = MaterialTheme.typography.titleMedium)
            Text(entry.reading, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(entry.meaningFor(language), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text(
                formatTime(entry.lookedUpAtEpochMs),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TextButton(onClick = onDelete) {
            Text(deleteLabel)
        }
    }
}

@Composable
private fun HistoryDetailsDialog(
    entry: HistoryEntry,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    val strings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(entry.word) },
        text = {
            Column {
                Text("${strings.detailsReading}: ${entry.reading}")
                Spacer(Modifier.height(8.dp))
                Text("${strings.detailsMeaning}: ${entry.meaningFor(language)}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDelete) { Text(strings.actionDelete) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.actionClose) }
        },
    )
}

private fun HistoryEntry.meaningFor(language: AppLanguage): String = when (language) {
    AppLanguage.CHINESE -> meaningZh
    AppLanguage.JAPANESE -> meaningJa
    AppLanguage.ENGLISH, AppLanguage.SYSTEM -> meaningEn
}

private fun formatTime(epochMs: Long): String {
    val local = Instant.fromEpochMilliseconds(epochMs).toLocalDateTime(TimeZone.currentSystemDefault())
    val month = local.monthNumber.toString().padStart(2, '0')
    val day = local.dayOfMonth.toString().padStart(2, '0')
    val hour = local.hour.toString().padStart(2, '0')
    val minute = local.minute.toString().padStart(2, '0')
    return "${local.year}-$month-$day $hour:$minute"
}
