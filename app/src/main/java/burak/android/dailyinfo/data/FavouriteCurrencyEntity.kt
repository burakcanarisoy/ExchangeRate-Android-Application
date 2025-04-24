package burak.android.dailyinfo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_currencies", primaryKeys = ["code", "base"])
data class FavouriteCurrencyEntity(
    val code: String,
    val base: String,
    val rate: Double
)
