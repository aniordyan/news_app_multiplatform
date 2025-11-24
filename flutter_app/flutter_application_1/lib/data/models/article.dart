class Article {
  final String title;
  final String? author;
  final String? description;
  final String? imageUrl;
  final String? sourceName;

  Article({
    required this.title,
    this.author,
    this.description,
    this.imageUrl,
    this.sourceName,
  });

  factory Article.fromJson(Map<String, dynamic> json) {
    final source = json['source'] as Map<String, dynamic>?;
    return Article(
      title: json['title'] ?? '',
      author: json['author'],
      description: json['description'],
      imageUrl: json['urlToImage'],
      sourceName: source != null ? source['name'] as String? : null,
    );
  }
}
