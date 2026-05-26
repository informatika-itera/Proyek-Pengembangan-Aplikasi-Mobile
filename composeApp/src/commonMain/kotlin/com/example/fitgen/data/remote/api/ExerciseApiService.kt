package com.example.fitgen.data.remote.api

import com.example.fitgen.BuildKonfig
import com.example.fitgen.core.network.NetworkResult
import com.example.fitgen.core.network.safeApiCall
import com.example.fitgen.data.remote.dto.ExerciseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

class ExerciseApiService(
    private val httpClient: HttpClient
) {
    /**
     * Search for exercises by name using ExerciseDB API from RapidAPI.
     */
    suspend fun searchExerciseByName(name: String): NetworkResult<List<ExerciseDto>> {
        return safeApiCall {
            val response = httpClient.get("https://exercisedb.p.rapidapi.com/exercises/name/$name") {
                header("X-RapidAPI-Key", BuildKonfig.RAPID_API_KEY)
                header("X-RapidAPI-Host", "exercisedb.p.rapidapi.com")
            }
            response.body<List<ExerciseDto>>()
        }
    }
}
