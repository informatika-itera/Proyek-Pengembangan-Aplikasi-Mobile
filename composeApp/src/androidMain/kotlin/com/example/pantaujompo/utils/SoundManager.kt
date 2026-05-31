package com.example.pantaujompo.utils

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.concurrent.thread

actual class SoundManager actual constructor() {
    actual fun playSplashSound() {
        thread {
            try {
                val sampleRate = 44100
                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate, 
                    AudioFormat.CHANNEL_OUT_MONO, 
                    AudioFormat.ENCODING_PCM_16BIT
                )
                
                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize * 4,
                    AudioTrack.MODE_STREAM
                )
                
                audioTrack.play()

                // Phase 1: Gathering Energy (Harmonic Chord Swell)
                val phase1Duration = 800
                val samples1 = sampleRate * phase1Duration / 1000
                val buffer1 = ShortArray(samples1)
                var a1=0.0; var a2=0.0; var a3=0.0
                for (i in 0 until samples1) {
                    val p = i.toDouble() / samples1
                    a1 += 2.0 * Math.PI * 261.63 / sampleRate // C4
                    a2 += 2.0 * Math.PI * 329.63 / sampleRate // E4
                    a3 += 2.0 * Math.PI * 392.00 / sampleRate // G4
                    val vol = p // Ramp up volume smoothly
                    val s = (Math.sin(a1) + Math.sin(a2) + Math.sin(a3)) / 3.0
                    buffer1[i] = (s * 32767 * vol * 0.5).toInt().toShort()
                }
                audioTrack.write(buffer1, 0, samples1)

                Thread.sleep(100) // Brief silence

                // Phase 2: Burst (Higher octave resolving chord - "Tada!")
                val phase2Duration = 800
                val samples2 = sampleRate * phase2Duration / 1000
                val buffer2 = ShortArray(samples2)
                a1=0.0; a2=0.0; a3=0.0
                for (i in 0 until samples2) {
                    val p = i.toDouble() / samples2
                    a1 += 2.0 * Math.PI * 523.25 / sampleRate // C5
                    a2 += 2.0 * Math.PI * 659.25 / sampleRate // E5
                    a3 += 2.0 * Math.PI * 783.99 / sampleRate // G5
                    // Add a tiny bit of noise for a subtle energy texture
                    val noise = (Math.random() * 2.0 - 1.0) * 0.05
                    val vol = if (p < 0.1) p * 10 else (1.0 - p) // Quick attack, slow fade out
                    val s = (Math.sin(a1) + Math.sin(a2) + Math.sin(a3) + noise) / 3.05
                    buffer2[i] = (s * 32767 * vol * 0.6).toInt().toShort()
                }
                audioTrack.write(buffer2, 0, samples2)

                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
