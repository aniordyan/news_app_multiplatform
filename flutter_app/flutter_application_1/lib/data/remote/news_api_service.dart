import 'dart:convert';

import 'package:http/http.dart' as http;
import '../models/article.dart';

class NewsApiService {
  static const String _baseHost = 'newsapi.org';
  static const String _country = 'us';
  static const String _path = '/v2/top-headlines';

  
  static const String _apiKey = 'a3545c9c34274d2d86a795e853073eaf';

  Future<List<Article>> fetchTopHeadlines({
    String? category,
    String? query,
  }) async {
    final params = <String, String>{
      'country': _country,
      'apiKey': _apiKey,
    };

    if (category != null && category.isNotEmpty) {
      params['category'] = category;
    }

    if (query != null && query.isNotEmpty) {
      params['q'] = query;
    }

    final uri = Uri.https(_baseHost, _path, params);

    final response = await http.get(uri);

    if (response.statusCode != 200) {
      throw Exception('Failed to load news: ${response.statusCode}');
    }

    final Map<String, dynamic> jsonBody = json.decode(response.body);
    final List<dynamic> articlesJson = jsonBody['articles'] ?? [];

    return articlesJson
        .map((item) => Article.fromJson(item as Map<String, dynamic>))
        .toList();
  }

}
