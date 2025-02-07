package com.ssafy.tmbg.di

import com.ssafy.tmbg.api.ScheduleApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * OkHttpClient를 제공합니다.
     * 네트워크 요청에 대한 기본 설정을 담당합니다.
     * @return 기본 설정이 적용된 OkHttpClient 인스턴스
     * build(): 설정된 옵션들을 적용하여 OkHttpClient 인스턴스를 생성합니다.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()  // 설정된 옵션들을 적용하여 OkHttpClient 인스턴스 생성
    }

    /**
     * Retrofit 인스턴스를 제공합니다.
     * @param okHttpClient HTTP 클라이언트
     * @return 설정된 Retrofit 인스턴스
     * baseUrl(): API 서버의 기본 URL을 설정합니다.
     * client(): 사용할 HTTP 클라이언트를 설정합니다.
     * addConverterFactory(): JSON 변환기를 설정합니다.
     * build(): 설정된 옵션들을 적용하여 Retrofit 인스턴스를 생성합니다.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://i12d106.p.ssafy.io/")  // API 서버의 기본 URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())  // Moshi -> Gson
            .build()
    }

}