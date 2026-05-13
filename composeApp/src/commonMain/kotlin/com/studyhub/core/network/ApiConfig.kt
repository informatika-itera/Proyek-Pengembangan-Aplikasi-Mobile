package com.studyhub.core.network

expect object ApiConfig {
    val groqApiKey: String
    val groqBaseUrl: String
    val groqModel: String
}
