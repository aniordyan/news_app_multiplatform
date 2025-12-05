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

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeadlinesScreen(
    modifier: Modifier = Modifier,
    viewModel: TopHeadlinesViewModel,
    onArticleClick: (Article) -> Unit = {}
) {
    val state = viewModel.uiState.collectAsState()

    // UI state for search and filter dialog
    val searchState = remember { mutableStateOf(TextFieldValue("")) }
    val showFilterDialog = remember { mutableStateOf(false) }
    val selectedCategory = remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        // Dark blue header titled "News" implemented as a simple Box to avoid TopAppBarDefaults version issues
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF0D47A1)),
            contentAlignment = Alignment.CenterStart
        ) {
            // Title + search + filter on one row
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                Text(text = "News", color = Color.White, modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.titleMedium)

                // Spacer-like padding
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))

                // Search field (compact) with a trailing search icon to apply
                TextField(
                    value = searchState.value,
                    onValueChange = { searchState.value = it },
                    placeholder = { Text(text = "Search", color = Color.White.copy(alpha = 0.7f)) },
                    singleLine = true,
                    // Use default colors to avoid unresolved symbol issues in this environment
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.setQuery(if (searchState.value.text.isBlank()) null else searchState.value.text)
                            viewModel.refresh()
                        }) {
                            Icon(painter = painterResource(android.R.drawable.ic_menu_search), contentDescription = "Search", tint = Color.White)
                        }
                    },
                    modifier = Modifier.height(40.dp).padding(end = 8.dp)
                )

                // Filter icon
                IconButton(onClick = { showFilterDialog.value = true }) {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_sort_by_size),
                        contentDescription = "Filters",
                        tint = Color.White
                    )
                }
            }
        }

        // Filter dialog
        if (showFilterDialog.value) {
            AlertDialog(
                onDismissRequest = { showFilterDialog.value = false },
                confirmButton = {
                    Button(onClick = {
                        // Apply selected category and refresh
                        viewModel.setCategory(selectedCategory.value)
                        viewModel.refresh()
                        showFilterDialog.value = false
                    }) {
                        Text("Apply")
                    }
                },
                dismissButton = {
                    Button(onClick = { showFilterDialog.value = false }) { Text("Cancel") }
                },
                title = { Text(text = "Filters") },
                text = {
                    Column {
                        FilterOption("Business", selectedCategory.value == "business") { selectedCategory.value = "business" }
                        FilterOption("Entertainment", selectedCategory.value == "entertainment") { selectedCategory.value = "entertainment" }
                        FilterOption("General", selectedCategory.value == "general") { selectedCategory.value = "general" }
                        FilterOption("Health", selectedCategory.value == "health") { selectedCategory.value = "health" }
                        // Add a clear option
                        Button(onClick = { selectedCategory.value = null }) { Text("Clear") }
                    }
                }
            )
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
private fun FilterOption(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onSelect() }
        .padding(vertical = 8.dp)) {
        Text(text = label, modifier = Modifier.weight(1f))
        if (selected) Text(text = "✓")
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
                Text(text = article.title ?: "(No title)", style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(text = article.sourceName ?: "", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
