package com.example.myapplication.data.dto

import com.squareup.moshi.Json

data class NewsResponseDto(
    val status: String?,
    val totalResults: Int?,
    val articles: List<ArticleDto> = emptyList()
)

data class ArticleDto(
    val source: SourceDto?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    @Json(name = "urlToImage") val urlToImage: String?,
    @Json(name = "publishedAt") val publishedAt: String?,
    val content: String?
)

data class SourceDto(
    val id: String?,
    val name: String?
)

