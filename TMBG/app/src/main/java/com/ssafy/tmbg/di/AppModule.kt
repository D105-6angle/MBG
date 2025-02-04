package com.ssafy.tmbg.di

import android.content.Context
import com.ssafy.tmbg.data.auth.repository.KakaoLoginRepository
import com.ssafy.tmbg.data.auth.repository.KakaoLoginRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideKakaoLoginRepository(context: Context): KakaoLoginRepository =
        KakaoLoginRepositoryImpl(context)
}