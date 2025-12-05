package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Article

interface NewsRepository {
    suspend fun getTopHeadlines(country: String = "us"): List<Article>
}

