package com.ssafy.mbg.di

import android.content.Context
import com.ssafy.mbg.api.AuthApi
import com.ssafy.mbg.api.BookApi
import com.ssafy.mbg.api.HomeApi
import com.ssafy.mbg.data.auth.repository.AuthRepository
import com.ssafy.mbg.data.auth.repository.AuthRepositoryImpl
import com.ssafy.mbg.api.MyPageApi
import com.ssafy.mbg.api.ReportApi
import com.ssafy.mbg.api.ScheduleApi
import com.ssafy.mbg.data.auth.repository.KakaoLoginRepositoryImpl
import com.ssafy.mbg.data.auth.repository.SocialLoginRepository
import com.ssafy.mbg.data.book.repository.BookRepository
import com.ssafy.mbg.data.book.repository.BookRepositoryImpl
import com.ssafy.mbg.data.mypage.repository.MyPageRepository
import com.ssafy.mbg.data.mypage.repository.MyPageRepositoryImpl
import com.ssafy.mbg.data.report.repository.ReportRepository
import com.ssafy.mbg.data.report.repository.ReportRepositoryImpl
import com.ssafy.mbg.data.task.repository.TaskRepositoryImpl
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
    fun provideKakaoLoginRepository(context: Context): SocialLoginRepository =
        KakaoLoginRepositoryImpl(context)
    // 네이버 로그인 리포지토리 구현체를 싱글톤으로 제공 context가 필요없기 때문에 굳이 필요 없어용 나중에 지울게요.

    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context
    ): UserPreferences = UserPreferences(context)

    @Provides
    @Singleton
    fun providerMyPageRepository(
        myPageApi: MyPageApi
    ) : MyPageRepository {
        return MyPageRepositoryImpl(myPageApi)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        myPageApi: MyPageApi
    ): AuthRepository {
        return AuthRepositoryImpl(authApi, myPageApi)
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
    fun provideScheduleRepository(scheduleApi : ScheduleApi) : TaskRepositoryImpl {
        return TaskRepositoryImpl(scheduleApi)
    }
//    @Provides
//    @Singleton
//    fun provideNaverLoginRepository(): SocialLoginRepository = NaverLoginRepositoryImpl()

    @Provides
    @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApi {
        return retrofit.create(HomeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReportApi(retrofit: Retrofit): ReportApi {
        return retrofit.create(ReportApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReportRepository(reportApi: ReportApi): ReportRepository {
        return ReportRepositoryImpl(reportApi)
    }

    @Provides
    @Singleton
    fun provideBookApi(retrofit: Retrofit): BookApi {
        return retrofit.create(BookApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBookRepository(bookApi: BookApi): BookRepository {
        return BookRepositoryImpl(bookApi)
    }
}