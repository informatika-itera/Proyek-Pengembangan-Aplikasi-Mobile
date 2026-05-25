package com.example.mybawanggacha.data.local.source

import com.example.mybawanggacha.data.remote.jikan.dto.AnimeAiredDto
import com.example.mybawanggacha.data.remote.jikan.dto.AnimeBroadcastDto
import com.example.mybawanggacha.data.remote.jikan.dto.AnimeDetailData
import com.example.mybawanggacha.data.remote.jikan.dto.AnimeEpisodeDto
import com.example.mybawanggacha.data.remote.jikan.dto.AnimeImages
import com.example.mybawanggacha.data.remote.jikan.dto.ImageUrls
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimeDetailCacheCodecTest {

    @Test
    fun detailRoundTrip_shouldKeepAnimeDetailFields() {
        val detail = AnimeDetailData(
            mal_id = 1,
            url = "https://myanimelist.net/anime/1",
            images = AnimeImages(jpg = ImageUrls(image_url = "image.jpg")),
            title = "Cowboy Bebop",
            title_english = "Cowboy Bebop",
            title_japanese = "カウボーイビバップ",
            episodes = 26,
            status = "Finished Airing",
            airing = false,
            aired = AnimeAiredDto(string = "Apr 3, 1998 to Apr 24, 1999"),
            broadcast = AnimeBroadcastDto(string = "Saturdays at 01:00 (JST)")
        )

        val decoded = AnimeDetailCacheCodec.decodeDetail(
            AnimeDetailCacheCodec.encodeDetail(detail)
        )

        assertEquals(detail.mal_id, decoded.mal_id)
        assertEquals(detail.title, decoded.title)
        assertEquals(detail.episodes, decoded.episodes)
        assertEquals(detail.aired?.string, decoded.aired?.string)
        assertEquals(detail.broadcast?.string, decoded.broadcast?.string)
    }

    @Test
    fun episodesRoundTrip_shouldKeepEpisodeFields() {
        val episodes = listOf(
            AnimeEpisodeDto(
                mal_id = 1,
                title = "Asteroid Blues",
                title_japanese = "アステロイド・ブルース",
                title_romanji = "Asteroid Blues",
                aired = "1998-04-03",
                filler = false,
                recap = false
            )
        )

        val decoded = AnimeDetailCacheCodec.decodeEpisodes(
            AnimeDetailCacheCodec.encodeEpisodes(episodes)
        )

        assertEquals(episodes, decoded)
    }
}
