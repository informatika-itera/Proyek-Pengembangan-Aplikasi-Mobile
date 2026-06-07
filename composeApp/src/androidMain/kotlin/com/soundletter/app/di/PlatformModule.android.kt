package com.soundletter.app.di

import com.soundletter.app.core.util.DatabaseDriverFactory
import com.soundletter.app.core.audio.AudioPlayer
import com.soundletter.app.core.audio.AndroidAudioPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val platformModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    // Fix: Bind implementasi Android ke interface AudioPlayer
    single<AudioPlayer> { AndroidAudioPlayer(androidContext()) }
}
