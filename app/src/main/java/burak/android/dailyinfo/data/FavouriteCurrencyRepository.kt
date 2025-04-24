package burak.android.dailyinfo.data

import javax.inject.Inject

class FavouriteCurrencyRepository @Inject constructor(
    private val dao: FavouriteCurrencyDao
){
    suspend fun getAllFavourites() = dao.getAllFavourites()
    suspend fun insertFavourite(currency: FavouriteCurrencyEntity) = dao.insertFavourite(currency)
    suspend fun deleteFavourite(currency: FavouriteCurrencyEntity) = dao.deleteFavourite(currency)
    suspend fun isFavourite(code: String, base: String): Boolean = dao.getFavouriteByCode(code, base) != null
}