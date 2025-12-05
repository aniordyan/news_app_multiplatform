package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Article
import com.example.myapplication.domain.repository.NewsRepository

class GetTopHeadlinesUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(country: String = "us"): List<Article> =
        repository.getTopHeadlines(country)
}

