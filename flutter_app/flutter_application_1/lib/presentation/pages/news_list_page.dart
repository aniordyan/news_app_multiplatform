import 'package:flutter/material.dart';
import '../../data/models/article.dart';
import '../../data/remote/news_api_service.dart';
import 'news_details_page.dart';

class NewsListPage extends StatefulWidget {
  const NewsListPage({super.key});

  @override
  State<NewsListPage> createState() => _NewsListPageState();
}

class _NewsListPageState extends State<NewsListPage> {
  final NewsApiService _newsApiService = NewsApiService();
  late Future<List<Article>> _futureArticles;

  @override
  void initState() {
    super.initState();
    _futureArticles = _newsApiService.fetchTopHeadlines();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('News'),
      ),
      body: FutureBuilder<List<Article>>(
        future: _futureArticles,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          if (snapshot.hasError) {
            return Center(
              child: Text('Error: ${snapshot.error}'),
            );
          }

          final articles = snapshot.data ?? [];

          if (articles.isEmpty) {
            return const Center(
              child: Text('No news found'),
            );
          }

          return ListView.builder(
            itemCount: articles.length,
            itemBuilder: (context, index) {
              final article = articles[index];
              return ListTile(
                leading: article.imageUrl != null
                    ? Image.network(
                        article.imageUrl!,
                        width: 80,
                        fit: BoxFit.cover,
                        errorBuilder: (_, __, ___) => const Icon(Icons.image),
                      )
                    : const Icon(Icons.image),
                title: Text(
                  article.title,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
                subtitle: Text(
                  article.sourceName ?? 'Unknown source',
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
                onTap: () {
                  Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (_) => NewsDetailsPage(article: article),
    ),
  );
                },
              );
            },
          );
        },
      ),
    );
  }
}
