package burak.android.dailyinfo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavouriteCurrencyDao {
    @Query("SELECT * FROM favourite_currencies")
    suspend fun getAllFavourites(): List<FavouriteCurrencyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(currency: FavouriteCurrencyEntity)

    @Delete
    suspend fun deleteFavourite(currency: FavouriteCurrencyEntity)

    @Query("SELECT * FROM favourite_currencies WHERE code = :code AND base= :base LIMIT 1")
    suspend fun getFavouriteByCode(code: String, base: String): FavouriteCurrencyEntity?
}