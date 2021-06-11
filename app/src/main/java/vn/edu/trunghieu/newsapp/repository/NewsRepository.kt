package vn.edu.trunghieu.newsapp.repository

import vn.edu.trunghieu.newsapp.api.NewsApi
import vn.edu.trunghieu.newsapp.db.ArticleDao
import vn.edu.trunghieu.newsapp.model.Article
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val articleDao: ArticleDao,
    private val api: NewsApi
) {
    var hasInternetConnection: Boolean = true

    suspend fun getTopNewsHeadlines(country: String, pageNumber: Int) = api.getTopHeadlines(country,pageNumber)

    suspend fun searchForNews(searchQuery: String, pageNumber: Int) = api.searchForNews(searchQuery, pageNumber)

    suspend fun insert(article: Article) = articleDao.insert(article)

    fun getSavedNews() = articleDao.getAllArticles()

    suspend fun findArticleFromDB(article: Article) = articleDao.findArticle(article.url)

    suspend fun articleCount(article: Article) = articleDao.articleCount(article.url)

    suspend fun deleteArticle(article: Article) = articleDao.deleteArticle(article)

}