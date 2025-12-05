package com.example.myapplication.data.remote

class NewsRemoteDataSource(private val api: NewsApiService) {
    suspend fun getTopHeadlines(apiKey: String, country: String = "us") =
        api.getTopHeadlines(country = country, apiKey = apiKey)
}

