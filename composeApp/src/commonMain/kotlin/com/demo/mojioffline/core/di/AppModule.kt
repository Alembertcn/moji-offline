package com.demo.mojioffline.core.di

import com.demo.mojioffline.data.local.HistoryLocalStore
import com.demo.mojioffline.data.local.PreferencesLocalStore
import com.demo.mojioffline.data.local.createSettings
import com.demo.mojioffline.data.repository.DefaultHistoryRepository
import com.demo.mojioffline.data.repository.DefaultPreferencesRepository
import com.demo.mojioffline.domain.repository.HistoryRepository
import com.demo.mojioffline.domain.repository.PreferencesRepository
import com.demo.mojioffline.feature.history.HistoryViewModel
import com.demo.mojioffline.feature.settings.SettingsViewModel
import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<Settings> { createSettings() }
    single { HistoryLocalStore(get()) }
    single { PreferencesLocalStore(get()) }
    single<PreferencesRepository> { DefaultPreferencesRepository(get()) }
    single<HistoryRepository> { DefaultHistoryRepository(get(), get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}
