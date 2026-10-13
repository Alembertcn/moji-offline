package com.demo.mojioffline.data.local

import com.demo.mojioffline.domain.model.HistoryEntry

/**
 * Bundled offline sample lookups used for first-run seeding and "Add demo entries".
 * Kept tiny on purpose: Phase 1 is about history UX, not a full dictionary corpus.
 */
object HistorySeed {
    fun sampleEntries(nowEpochMs: Long): List<HistoryEntry> = listOf(
        HistoryEntry(
            id = "seed-1",
            word = "勉強",
            reading = "べんきょう",
            meaningZh = "学习；用功",
            meaningEn = "study; diligence",
            meaningJa = "学問や技芸を学ぶこと",
            lookedUpAtEpochMs = nowEpochMs - 5 * 60_000,
        ),
        HistoryEntry(
            id = "seed-2",
            word = "旅行",
            reading = "りょこう",
            meaningZh = "旅行",
            meaningEn = "travel; trip",
            meaningJa = "よその土地へ行くこと",
            lookedUpAtEpochMs = nowEpochMs - 30 * 60_000,
        ),
        HistoryEntry(
            id = "seed-3",
            word = "美味しい",
            reading = "おいしい",
            meaningZh = "好吃的；美味的",
            meaningEn = "delicious; tasty",
            meaningJa = "味がよい",
            lookedUpAtEpochMs = nowEpochMs - 2 * 60 * 60_000,
        ),
        HistoryEntry(
            id = "seed-4",
            word = "友達",
            reading = "ともだち",
            meaningZh = "朋友",
            meaningEn = "friend",
            meaningJa = "親しく付き合う人",
            lookedUpAtEpochMs = nowEpochMs - 24 * 60 * 60_000,
        ),
        HistoryEntry(
            id = "seed-5",
            word = "明日",
            reading = "あした",
            meaningZh = "明天",
            meaningEn = "tomorrow",
            meaningJa = "きょうの次の日",
            lookedUpAtEpochMs = nowEpochMs - 48 * 60 * 60_000,
        ),
    )
}
