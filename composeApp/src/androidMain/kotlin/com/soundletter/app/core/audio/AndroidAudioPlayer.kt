package com.soundletter.app.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

/**
 * Android implementation of AudioPlayer interface.
 * Kita menggunakan nama class yang berbeda untuk menghindari konflik dengan interface.
 */
class AndroidAudioPlayer(private val context: Context) : AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    override fun play(url: String, onFinished: () -> Unit) {
        if (url.isBlank()) {
            onFinished()
            return
        }
        
        try {
            stop()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { it.start() }
                setOnCompletionListener { onFinished() }
                setOnErrorListener { _, _, _ ->
                    onFinished()
                    false
                }
            }
        } catch (e: Exception) {
            onFinished()
        }
    }

    override fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (e: Exception) {}
    }

    override fun stop() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {}
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    override fun release() {
        stop()
    }
}
