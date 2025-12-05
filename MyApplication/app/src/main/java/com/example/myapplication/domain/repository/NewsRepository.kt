package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Article

interface NewsRepository {
    // Single unified method with optional category and query
    suspend fun getTopHeadlines(country: String = "us", category: String? = null, query: String? = null): List<Article>
}
