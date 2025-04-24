package burak.android.dailyinfo.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context{
        return context
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase{
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "currency_db"
        ).build()
    }

    @Provides
    fun provideFavouriteCurrencyDao(db: AppDatabase): FavouriteCurrencyDao{
        return db.favouriteCurrencyDao()
    }
}