//package com.ssafy.mbg.network
//
//import com.ssafy.mbg.network.interceptor.AuthInterceptor
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.util.concurrent.TimeUnit
//
//object ApiClient {
//    // Base Url 설정
//    private const val BASE_URL = "https://i12d106.p.ssafy.io/api/"
//    // 시간 제한 설정
//    private const val TIME_OUT = 30L
//
//    //HttpCleint 빌더
//    private val okHttpClient = OkHttpClient.Builder().apply {
//        addInterceptor(HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        })
//        addInterceptor(AuthInterceptor())
//        connectTimeout(TIME_OUT, TimeUnit.SECONDS)
//        writeTimeout(TIME_OUT, TimeUnit.SECONDS)
//        readTimeout(TIME_OUT, TimeUnit.SECONDS)
//    }.build()
//
//    val retrofit: Retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .client(okHttpClient)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//}