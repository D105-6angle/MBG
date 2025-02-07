package com.ssafy.mbg.di

import android.content.Context
import com.ssafy.mbg.api.AuthApi
import com.ssafy.mbg.data.auth.repository.AuthRepository
import com.ssafy.mbg.data.auth.repository.AuthRepositoryImpl
import com.ssafy.mbg.data.auth.repository.PastKakaoLoginRepositoryImpl
import com.ssafy.mbg.data.auth.repository.PastSocialLoginRepository
import com.ssafy.mbg.api.MyPageApi
import com.ssafy.mbg.api.ScheduleApi
import com.ssafy.mbg.data.mypage.repository.MyPageRepository
import com.ssafy.mbg.data.preferences.UserPreferences
import com.ssafy.mbg.data.task.repository.ScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

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
    fun provideKakaoLoginRepository(context: Context): PastSocialLoginRepository =
        PastKakaoLoginRepositoryImpl(context)
    // 네이버 로그인 리포지토리 구현체를 싱글톤으로 제공 context가 필요없기 때문에 굳이 필요 없어용 나중에 지울게요.

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun providerMyPageRepository(
        myPageApi: MyPageApi
    ) : MyPageRepository {
        return MyPageRepository(myPageApi)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi
    ): AuthRepository {
        return AuthRepositoryImpl(authApi)
    }


    @Provides
    @Singleton
    fun provideMyPageApi(retrofit: Retrofit) : MyPageApi {
        return retrofit.create(MyPageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit) : AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideScheduleApi(retrofit: Retrofit) : ScheduleApi {
        return retrofit.create(ScheduleApi::class.java)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(scheduleApi : ScheduleApi) : ScheduleRepository {
        return ScheduleRepository(scheduleApi)
    }
//    @Provides
//    @Singleton
//    fun provideNaverLoginRepository(): SocialLoginRepository = NaverLoginRepositoryImpl()
}