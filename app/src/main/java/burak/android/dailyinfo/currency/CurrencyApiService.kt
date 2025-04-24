package burak.android.dailyinfo.currency

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("v6/{apiKey}/latest/{base}")
    suspend fun getRates(
        @Path("apiKey") apiKey: String,
        @Path("base") baseCurrency: String
    ): CurrencyResponse
}