package com.example.mybawanggacha.data.repository.manga

import com.example.mybawanggacha.data.remote.jikan.dto.MangaDetailData
import kotlinx.serialization.json.Json

internal object MangaDetailCacheCodec {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun encodeDetail(detail: MangaDetailData): String {
        return json.encodeToString(MangaDetailData.serializer(), detail)
    }

    fun decodeDetail(value: String): MangaDetailData {
        return json.decodeFromString(MangaDetailData.serializer(), value)
    }
}
