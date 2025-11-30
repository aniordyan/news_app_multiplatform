import 'dart:io';

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
  final TextEditingController _searchController = TextEditingController();

  bool _isLoading = false;
  String? _errorMessage;

  // offline search
  List<Article> _cachedArticles = [];


  List<Article> _visibleArticles = [];

  String? _selectedCategory;

  bool _isOfflineSearch = false;

  @override
  void initState() {
    super.initState();

    
    _searchController.addListener(() {
      setState(() {});
    });

    
    _loadNews();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadNews({String? category, String? query}) async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
      _isOfflineSearch = false;
    });

    try {
      final articles = await _newsApiService.fetchTopHeadlines(
        category: category,
        query: query,
      );

      setState(() {
        _cachedArticles = articles;
        _visibleArticles = articles;
      });
    } on SocketException catch (_) {
      // no internet.
      if (_cachedArticles.isNotEmpty && query != null && query.isNotEmpty) {
    
        final lower = query.toLowerCase();

        final filtered = _cachedArticles.where((article) {
          final text = (article.title + ' ' + (article.description ?? ''))
              .toLowerCase();
          return text.contains(lower);
        }).toList();

        setState(() {
          _visibleArticles = filtered;
          _isOfflineSearch = true;
        });
      } else {
        setState(() {
          _errorMessage = 'No internet connection.';
        });
      }
    } catch (e) {
      setState(() {
        _errorMessage = 'Failed to load news: $e';
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  void _onSearchPressed() {
    final query = _searchController.text.trim();

    if (query.isEmpty) {
      _loadNews(category: _selectedCategory);
      return;
    }

    _loadNews(
      category: _selectedCategory,
      query: query,
    );
  }

  void _clearSearch() {
    _searchController.clear();
    _loadNews(category: _selectedCategory);
  }

  void _openFilterDialog() {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (_) {
        return StatefulBuilder(
          builder: (context, setModalState) {
            return Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'Select Category',
                    style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 10),

                  //categories
                  _buildRadioOption(
                    setModalState,
                    'business',
                    'Business',
                  ),
                  _buildRadioOption(
                    setModalState,
                    'entertainment',
                    'Entertainment',
                  ),
                  _buildRadioOption(
                    setModalState,
                    'general',
                    'General',
                  ),
                  _buildRadioOption(
                    setModalState,
                    'health',
                    'Health',
                  ),

                  const SizedBox(height: 20),
                  ElevatedButton(
                    onPressed: () {
                      Navigator.pop(context);
                      _applyFilter();
                    },
                    child: const Text('Apply'),
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }

  Widget _buildRadioOption(
    void Function(void Function()) setModalState,
    String value,
    String label,
  ) {
    return RadioListTile<String>(
      title: Text(label),
      value: value,
      groupValue: _selectedCategory,
      onChanged: (newValue) {
        setModalState(() {
          _selectedCategory = newValue;
        });
      },
    );
  }

  void _applyFilter() {
    // meep current search text, apply filter with it.
    final query = _searchController.text.trim().isEmpty
        ? null
        : _searchController.text.trim();

    _loadNews(
      category: _selectedCategory,
      query: query,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('News'),
        actions: [
          IconButton(
            icon: const Icon(Icons.filter_alt_outlined),
            onPressed: _openFilterDialog,
          ),
        ],
      ),
      body: Column(
        children: [
          //search bar
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _searchController,
                    decoration: InputDecoration(
                      hintText: 'Search news...',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8),
                      ),
                      prefixIcon: const Icon(Icons.search),
                      suffixIcon: _searchController.text.isNotEmpty
                          ? IconButton(
                              icon: const Icon(Icons.close),
                              onPressed: _clearSearch,
                            )
                          : null,
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                ElevatedButton(
                  onPressed: _onSearchPressed,
                  child: const Text('Search'),
                ),
              ],
            ),
          ),

      
          Expanded(
            child: _buildBody(),
          ),
        ],
      ),
    );
  }

  Widget _buildBody() {
  if (_isLoading) {
    return const Center(child: CircularProgressIndicator());
  }

  if (_errorMessage != null) {
    return Center(child: Text(_errorMessage!));
  }

  if (_visibleArticles.isEmpty) {
    return const Center(child: Text('No Results Found'));
  }

  // 👇 This is allowed because parent (Expanded) gives it a height
  return RefreshIndicator(
    onRefresh: _onRefresh,
    child: ListView.builder(
      physics: const AlwaysScrollableScrollPhysics(),
      itemCount: _visibleArticles.length,
      itemBuilder: (context, index) {
        final article = _visibleArticles[index];
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
    ),
  );
}



  Future<void> _onRefresh() async {
  final query = _searchController.text.trim();
  await _loadNews(
    category: _selectedCategory,
    query: query.isEmpty ? null : query,
  );
}

}
