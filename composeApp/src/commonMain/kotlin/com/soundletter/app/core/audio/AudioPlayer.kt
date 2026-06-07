package com.soundletter.app.core.audio

interface AudioPlayer {
    fun play(url: String, onFinished: () -> Unit = {})
    fun pause()
    fun stop()
    fun isPlaying(): Boolean
    fun release()
}
