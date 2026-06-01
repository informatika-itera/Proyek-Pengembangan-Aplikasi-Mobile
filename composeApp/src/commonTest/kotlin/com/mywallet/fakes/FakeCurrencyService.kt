package com.mywallet.fakes

import com.mywallet.data.remote.CurrencyResponse
import com.mywallet.data.remote.CurrencyService

class FakeCurrencyService : CurrencyService {
    var mockResponse = CurrencyResponse("IDR", mapOf("USD" to 0.000062))

    override suspend fun getExchangeRates(): CurrencyResponse {
        return mockResponse
    }
}
