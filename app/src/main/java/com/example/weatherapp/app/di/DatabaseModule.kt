package com.example.weatherapp.app.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.KotaDao
import com.example.data.local.TeamDatabase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TeamDatabase {

        val passphrase: ByteArray = SQLiteDatabase.getBytes("dicoding".toCharArray())
        val factory = SupportFactory(passphrase)

        return  Room.databaseBuilder(
                context,
                TeamDatabase::
                class.java, "Team.db"
        ).fallbackToDestructiveMigration()
                .openHelperFactory(factory)
            .allowMainThreadQueries().build()
    }


    @Provides
    fun provideCityDao( database: TeamDatabase): KotaDao = database.kotaDao()
}