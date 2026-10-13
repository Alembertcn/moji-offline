package com.demo.mojioffline.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.demo.mojioffline.domain.model.AppLanguage

data class AppStrings(
    val appName: String,
    val tabHistory: String,
    val tabSettings: String,
    val historyTitle: String,
    val historyEmptyTitle: String,
    val historyEmptyBody: String,
    val historyErrorTitle: String,
    val historyErrorBody: String,
    val actionRetry: String,
    val actionAddDemo: String,
    val actionClearAll: String,
    val actionDelete: String,
    val actionClose: String,
    val settingsTitle: String,
    val settingsLanguage: String,
    val settingsTheme: String,
    val settingsDebug: String,
    val settingsForceError: String,
    val settingsForceErrorBody: String,
    val settingsAbout: String,
    val settingsAboutBody: String,
    val languageSystem: String,
    val languageZh: String,
    val languageEn: String,
    val languageJa: String,
    val themeSystem: String,
    val themeLight: String,
    val themeDark: String,
    val detailsReading: String,
    val detailsMeaning: String,
)

val LocalAppStrings = staticCompositionLocalOf { EnglishStrings }

val EnglishStrings = AppStrings(
    appName = "Moji Offline",
    tabHistory = "History",
    tabSettings = "Settings",
    historyTitle = "Lookup History",
    historyEmptyTitle = "No lookups yet",
    historyEmptyBody = "Your offline lookup history will appear here. Add demo entries to explore the flow.",
    historyErrorTitle = "Could not load history",
    historyErrorBody = "Local storage failed or error simulation is on. Retry or turn off the debug toggle in Settings.",
    actionRetry = "Retry",
    actionAddDemo = "Add demo entries",
    actionClearAll = "Clear all",
    actionDelete = "Delete",
    actionClose = "Close",
    settingsTitle = "Settings",
    settingsLanguage = "Language",
    settingsTheme = "Theme",
    settingsDebug = "Debug",
    settingsForceError = "Simulate history error",
    settingsForceErrorBody = "Forces the History screen into an error state so empty/error UX can be reviewed in interviews.",
    settingsAbout = "About",
    settingsAboutBody = "Phase 1 demo: offline lookup history inspired by MOJi Dictionary UX. No accounts. No membership gates.",
    languageSystem = "System",
    languageZh = "中文",
    languageEn = "English",
    languageJa = "日本語",
    themeSystem = "System",
    themeLight = "Light",
    themeDark = "Dark",
    detailsReading = "Reading",
    detailsMeaning = "Meaning",
)

val ChineseStrings = AppStrings(
    appName = "Moji 离线",
    tabHistory = "历史",
    tabSettings = "设置",
    historyTitle = "查询历史",
    historyEmptyTitle = "还没有查询记录",
    historyEmptyBody = "离线查询历史会显示在这里。可以先添加演示词条体验流程。",
    historyErrorTitle = "无法加载历史",
    historyErrorBody = "本地存储失败，或已开启错误模拟。请重试，或在设置中关闭调试开关。",
    actionRetry = "重试",
    actionAddDemo = "添加演示词条",
    actionClearAll = "清空全部",
    actionDelete = "删除",
    actionClose = "关闭",
    settingsTitle = "设置",
    settingsLanguage = "语言",
    settingsTheme = "主题",
    settingsDebug = "调试",
    settingsForceError = "模拟历史错误",
    settingsForceErrorBody = "强制历史页进入错误状态，方便面试时演示空态/错态。",
    settingsAbout = "关于",
    settingsAboutBody = "第一期演示：参考 MOJi 词典历史体验的离线查询记录。无账号，无会员限制。",
    languageSystem = "跟随系统",
    languageZh = "中文",
    languageEn = "English",
    languageJa = "日本語",
    themeSystem = "跟随系统",
    themeLight = "浅色",
    themeDark = "深色",
    detailsReading = "读音",
    detailsMeaning = "释义",
)

val JapaneseStrings = AppStrings(
    appName = "Moji Offline",
    tabHistory = "履歴",
    tabSettings = "設定",
    historyTitle = "検索履歴",
    historyEmptyTitle = "履歴はまだありません",
    historyEmptyBody = "オフラインの検索履歴がここに表示されます。デモ項目を追加して流れを確認できます。",
    historyErrorTitle = "履歴を読み込めません",
    historyErrorBody = "ローカル保存に失敗したか、エラー模擬がオンです。再試行するか、設定でデバッグ切替をオフにしてください。",
    actionRetry = "再試行",
    actionAddDemo = "デモ項目を追加",
    actionClearAll = "すべて削除",
    actionDelete = "削除",
    actionClose = "閉じる",
    settingsTitle = "設定",
    settingsLanguage = "言語",
    settingsTheme = "テーマ",
    settingsDebug = "デバッグ",
    settingsForceError = "履歴エラーを模擬",
    settingsForceErrorBody = "履歴画面をエラー状態にし、面接で空状態/エラー状態を見せやすくします。",
    settingsAbout = "このアプリについて",
    settingsAboutBody = "フェーズ1デモ：MOJi辞書の履歴UXを参考にしたオフライン検索履歴。アカウントなし。会員制限なし。",
    languageSystem = "システム",
    languageZh = "中文",
    languageEn = "English",
    languageJa = "日本語",
    themeSystem = "システム",
    themeLight = "ライト",
    themeDark = "ダーク",
    detailsReading = "読み",
    detailsMeaning = "意味",
)

fun stringsFor(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.CHINESE -> ChineseStrings
    AppLanguage.JAPANESE -> JapaneseStrings
    AppLanguage.ENGLISH, AppLanguage.SYSTEM -> EnglishStrings
}

@Composable
fun ProvideAppLocale(
    language: AppLanguage,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalAppStrings provides stringsFor(language), content = content)
}
