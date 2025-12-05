package com.example.myapplication.presentation.topheadlines

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import com.example.myapplication.domain.model.Article

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeadlinesScreen(
    modifier: Modifier = Modifier,
    viewModel: TopHeadlinesViewModel,
    onArticleClick: (Article) -> Unit = {}
) {
    val state = viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        // Dark blue header titled "News" implemented as a simple Box to avoid TopAppBarDefaults version issues
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF0D47A1)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = "News", color = Color.White, modifier = Modifier.padding(start = 16.dp), style = MaterialTheme.typography.titleMedium)
        }

        when (val s = state.value) {
            is UiState.Loading -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Error: ${s.message}")
                    Text(text = "Tap to retry", modifier = Modifier.clickable { viewModel.refresh() })
                }
            }
            is UiState.Success -> {
                if (s.articles.isEmpty()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("No headlines")
                    }
                } else {
                    // Make the LazyColumn fill available space so it can scroll
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                        items(s.articles) { article ->
                            ArticleItem(article = article, onClick = { onArticleClick(it) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleItem(article: Article, onClick: (Article) -> Unit) {
    Card(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .clickable { onClick(article) }) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = article.title ?: "(No title)", style = MaterialTheme.typography.titleMedium)
                Text(text = article.sourceName ?: "", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
