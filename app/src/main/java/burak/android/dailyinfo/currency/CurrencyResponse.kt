package burak.android.dailyinfo.currency

data class CurrencyResponse(
    val result: String,
    val time_last_update_utc: String,
    val time_next_update_utc: String,
    val base_code: String,
    val conversion_rates: Map<String, Double>
)
