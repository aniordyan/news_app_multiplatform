package com.example.myapplication.data.remote

import com.example.myapplication.data.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit interface for NewsAPI top-headlines
interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String? = null,
        @Query("q") query: String? = null,
        @Query("apiKey") apiKey: String
    ): NewsResponseDto
}
