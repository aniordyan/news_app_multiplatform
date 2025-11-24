import 'package:flutter/material.dart';
import '../../data/models/article.dart';

class NewsDetailsPage extends StatelessWidget {
  final Article article;

  const NewsDetailsPage({super.key, required this.article});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          article.sourceName ?? 'Details',
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // IMAGE
            if (article.imageUrl != null)
              ClipRRect(
                borderRadius: BorderRadius.circular(10),
                child: Image.network(
                  article.imageUrl!,
                  fit: BoxFit.cover,
                  errorBuilder: (_, __, ___) => const Icon(Icons.image),
                ),
              ),
            const SizedBox(height: 16),

            // TITLE
            Text(
              article.title,
              style: const TextStyle(
                fontSize: 22,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 12),

            // AUTHOR
            if (article.author != null)
              Text(
                'Author: ${article.author}',
                style: const TextStyle(fontSize: 16, color: Colors.grey),
              ),

            const SizedBox(height: 12),

            // DESCRIPTION
            if (article.description != null)
              Text(
                article.description!,
                style: const TextStyle(fontSize: 18),
              ),
          ],
        ),
      ),
    );
  }
}
