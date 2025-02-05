package com.ssafy.tmbg.di

import android.content.Context
import com.ssafy.tmbg.data.auth.repository.KakaoLoginRepositoryImpl
import com.ssafy.tmbg.data.auth.repository.NaverLoginRepositoryImpl
import com.ssafy.tmbg.data.auth.repository.SocialLoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// AppModule.kt - 앱의 의존성 주입을 위한 Hilt 모듈
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // 앱 컨텍스트를 싱글톤으로 제공
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    // 카카오 로그인 리포지토리 구현체를 싱글톤으로 제공
    @Provides
    @Singleton
    fun provideKakaoLoginRepository(context: Context): SocialLoginRepository =
        KakaoLoginRepositoryImpl(context)
    // 네이버 로그인 리포지토리 구현체를 싱글톤으로 제공 context가 필요없기 때문에 굳이 필요 없어용 나중에 지울게요.
//    @Provides
//    @Singleton
//    fun provideNaverLoginRepository(): SocialLoginRepository = NaverLoginRepositoryImpl()
}