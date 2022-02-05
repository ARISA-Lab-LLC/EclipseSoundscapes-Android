package org.eclipsesoundscapes.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.eclipsesoundscapes.data.AppDatabase
import org.eclipsesoundscapes.data.EclipseConfigurationDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideEclipseConfigurationDao(appDatabase: AppDatabase): EclipseConfigurationDao {
        return appDatabase.eclipseConfigurationDao()
    }
}