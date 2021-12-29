package com.jamal.myread

import com.jamal.myread.model.ScreenReaderService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideService() : ScreenReaderService = ScreenReaderService()
}