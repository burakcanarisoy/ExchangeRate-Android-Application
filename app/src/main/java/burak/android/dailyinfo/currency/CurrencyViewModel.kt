package burak.android.dailyinfo.currency

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import burak.android.dailyinfo.BuildConfig
import burak.android.dailyinfo.data.FavouriteCurrencyEntity
import burak.android.dailyinfo.data.FavouriteCurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val apiService: CurrencyApiService,
    private val repository: FavouriteCurrencyRepository
) : ViewModel() {

    var currencyState by mutableStateOf<CurrencyResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var fromCurrency by mutableStateOf("USD")
        private set

    var toCurrency by mutableStateOf("TRY")
        private set

    var amount by mutableStateOf("")
        private set

    var baseCurrency by mutableStateOf("TRY")
        private set

    var favourites by mutableStateOf<List<FavouriteCurrencyEntity>>(emptyList())
        private set

    init {
        getCurrencyRates()
        loadFavourites()
    }

    fun getCurrencyRates(base: String = baseCurrency) {
        viewModelScope.launch {
            isLoading = true
            try {
                val apiKey = BuildConfig.API_KEY
                currencyState = apiService.getRates(apiKey, base)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Bir hata oluştu: ${e.message}"
            }
            isLoading = false
        }
    }

    private fun loadFavourites() {
        viewModelScope.launch {
            favourites = repository.getAllFavourites()
        }
    }

    fun toggleFavourite(code: String, base: String, rate: Double) {
        viewModelScope.launch {
            val entity = FavouriteCurrencyEntity(code, base, rate)
            val exists = repository.isFavourite(code, base)
            if (exists) {
                repository.deleteFavourite(entity)
            } else {
                repository.insertFavourite(entity)
            }
            loadFavourites()
        }
    }

    fun updateFromCurrency(currency: String) {
        fromCurrency = currency
    }

    fun updateToCurrency(currency: String) {
        toCurrency = currency
    }

    fun updateAmount(value: String) {
        amount = value
    }

    fun updateBaseCurrency(newBase: String) {
        baseCurrency = newBase
    }
}
