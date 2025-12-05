package com.example.myapplication.data.remote

class NewsRemoteDataSource(private val api: NewsApiService) {
    suspend fun getTopHeadlines(apiKey: String, country: String = "us", category: String? = null, query: String? = null) =
        api.getTopHeadlines(country = country, category = category, query = query, apiKey = apiKey)
}
