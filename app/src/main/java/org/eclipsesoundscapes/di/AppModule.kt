package org.eclipsesoundscapes.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.eclipsesoundscapes.data.DataManager
import org.eclipsesoundscapes.data.SharedPrefsHelper
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideDataManager(@ApplicationContext context: Context): DataManager {
        return DataManager(SharedPrefsHelper(context))
    }
}