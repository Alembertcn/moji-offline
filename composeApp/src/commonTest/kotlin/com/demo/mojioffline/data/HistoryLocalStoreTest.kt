package com.demo.mojioffline.data

import com.demo.mojioffline.data.local.HistoryLocalStore
import com.demo.mojioffline.domain.model.HistoryEntry
import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HistoryLocalStoreTest {

    @Test
    fun writeAndReadRoundTrip() {
        val store = HistoryLocalStore(MapSettings())
        val entries = listOf(
            HistoryEntry(
                id = "a",
                word = "旅行",
                reading = "りょこう",
                meaningZh = "旅行",
                meaningEn = "travel",
                meaningJa = "旅をすること",
                lookedUpAtEpochMs = 42,
            ),
        )

        store.writeAll(entries)

        assertEquals(entries, store.readAll())
    }

    @Test
    fun clearRemovesEntries() {
        val store = HistoryLocalStore(MapSettings())
        store.writeAll(
            listOf(
                HistoryEntry(
                    id = "a",
                    word = "旅行",
                    reading = "りょこう",
                    meaningZh = "旅行",
                    meaningEn = "travel",
                    meaningJa = "旅をすること",
                    lookedUpAtEpochMs = 42,
                ),
            ),
        )

        store.clear()

        assertTrue(store.readAll().isEmpty())
    }
}
