package burak.android.dailyinfo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavouriteCurrencyEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){
    abstract fun favouriteCurrencyDao(): FavouriteCurrencyDao

    companion object{
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "currency_db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}