package com.ssafy.tmbg.di

import android.content.Context
import android.content.SharedPreferences
import com.ssafy.tmbg.api.AuthApi
import com.ssafy.tmbg.api.NoticeApi
import com.ssafy.tmbg.api.ProfileApi
import com.ssafy.tmbg.api.ReportApi
import com.ssafy.tmbg.api.ScheduleApi
import com.ssafy.tmbg.data.auth.repository.AuthRepository
import com.ssafy.tmbg.data.auth.repository.AuthRepositoryImpl
import com.ssafy.tmbg.data.auth.repository.KakaoLoginRepositoryImpl
import com.ssafy.tmbg.data.auth.repository.SocialLoginRepository
import com.ssafy.tmbg.data.notice.repository.NoticeRepository
import com.ssafy.tmbg.data.notice.repository.NoticeRepositoryImpl
import com.ssafy.tmbg.data.profile.repository.ProfileRepository
import com.ssafy.tmbg.data.profile.repository.ProfileRepositoryImpl
import com.ssafy.tmbg.data.report.repositoy.ReportRepository
import com.ssafy.tmbg.data.report.repositoy.ReportRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
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

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit) : AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReportApi(retrofit: Retrofit) : ReportApi {
        return retrofit.create(ReportApi::class.java)
    }
    /**
     * ScheduleApi 인터페이스의 구현체를 제공합니다.
     * @param retrofit Retrofit 인스턴스
     * @return ScheduleApi 구현체
     */
    @Provides
    @Singleton
    fun provideScheduleApi(retrofit: Retrofit): ScheduleApi {
        return retrofit.create(ScheduleApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit) : ProfileApi {
        return retrofit.create(ProfileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileApi: ProfileApi
    ) : ProfileRepository {
        return ProfileRepositoryImpl(profileApi)
    }

    @Provides
    @Singleton
    fun providerUserPreferences(@ApplicationContext context: Context) : UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideNoticeRepository(
        noticeApi: NoticeApi
    ) : NoticeRepository {
        return NoticeRepositoryImpl(noticeApi)
    }

    @Provides
    @Singleton
    fun provideNoticeApi(retrofit: Retrofit) : NoticeApi {
        return retrofit.create(NoticeApi::class.java)
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
    fun providerReportRepository(
        reportApi: ReportApi
    ) : ReportRepository {
        return ReportRepositoryImpl(reportApi)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("TMBG_PREFS", Context.MODE_PRIVATE)
    }
}