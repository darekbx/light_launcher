package com.darekbx.lightlauncher.repository.remote.stocks

class StocksProvider(
    private val currencyService: CurrencyService,
    private val responseParser: ResponseParser
) {

    enum class StockType(val code: String) {
        GOLD("xaupln"),
        BTC("btc.v"),
        ALLEGRO("ale")
    }

    suspend fun fetch(type: StockType): Double? {
        val response = currencyService.getCurrencyInfo(type.code)
        return responseParser.parseResponse(response)
    }
}
