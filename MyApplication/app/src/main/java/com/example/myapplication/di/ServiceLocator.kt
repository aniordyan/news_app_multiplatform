package com.example.myapplication.di

import com.example.myapplication.data.remote.NewsApiService
import com.example.myapplication.data.remote.NewsRemoteDataSource
import com.example.myapplication.data.repository.NewsRepositoryImpl
import com.example.myapplication.domain.usecase.GetTopHeadlinesUseCase
import com.example.myapplication.presentation.topheadlines.TopHeadlinesViewModel
import com.example.myapplication.util.BuildConfigProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.JsonAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ServiceLocator {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val moshi: Moshi = run {
        val builder = Moshi.Builder()
        // Try to add KotlinJsonAdapterFactory reflectively so the code still compiles even if the
        // IDE hasn't synced dependencies. At runtime, if the class is available it will be used.
        try {
            val clazz = Class.forName("com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory")
            val instance = clazz.getDeclaredConstructor().newInstance()
            if (instance is JsonAdapter.Factory) {
                builder.add(instance)
            }
        } catch (_: Throwable) {
            // If not available, proceed without it — Moshi will still work for many types.
        }
        builder.build()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://newsapi.org/")
        .client(okHttp)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }

    private val remoteDataSource: NewsRemoteDataSource by lazy {
        NewsRemoteDataSource(api)
    }

    private val repository: NewsRepositoryImpl by lazy {
        NewsRepositoryImpl(remote = remoteDataSource, apiKey = BuildConfigProvider.NEWS_API_KEY)
    }

    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase by lazy {
        GetTopHeadlinesUseCase(repository)
    }

    fun provideTopHeadlinesViewModel(): TopHeadlinesViewModel =
        TopHeadlinesViewModel(getTopHeadlinesUseCase)
}
