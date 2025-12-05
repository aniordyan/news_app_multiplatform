package com.example.myapplication.data.repository

import com.example.myapplication.data.dto.ArticleDto
import com.example.myapplication.data.dto.NewsResponseDto
import com.example.myapplication.data.remote.NewsRemoteDataSource
import com.example.myapplication.domain.model.Article
import com.example.myapplication.domain.repository.NewsRepository

class NewsRepositoryImpl(
    private val remote: NewsRemoteDataSource,
    private val apiKey: String
) : NewsRepository {
    override suspend fun getTopHeadlines(country: String): List<Article> {
        val response: NewsResponseDto = remote.getTopHeadlines(apiKey = apiKey, country = country)
        return response.articles.map { it.toDomain() }
    }
}

private fun ArticleDto.toDomain(): Article = Article(
    sourceName = this.source?.name,
    author = this.author,
    title = this.title,
    description = this.description,
    url = this.url,
    imageUrl = this.urlToImage,
    publishedAt = this.publishedAt,
    content = this.content
)

