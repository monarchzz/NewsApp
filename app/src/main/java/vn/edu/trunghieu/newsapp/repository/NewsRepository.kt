package vn.edu.trunghieu.newsapp.repository

import vn.edu.trunghieu.newsapp.api.RetrofitInstance
import vn.edu.trunghieu.newsapp.db.ArticleDatabase
import vn.edu.trunghieu.newsapp.model.Article

class NewsRepository(
    private val db: ArticleDatabase
) {
    suspend fun getTopNewsHeadlines(country: String, pageNumber: Int) =
        RetrofitInstance.api.getTopHeadlines(country,pageNumber)

    suspend fun searchForNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun insert(article: Article) = db.getArticleDao().insert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun findArticleFromDB(article: Article) = db.getArticleDao().findArticle(article.url)

    suspend fun articleCount(article: Article) = db.getArticleDao().articleCount(article.url)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}