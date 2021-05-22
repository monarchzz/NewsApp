package vn.edu.trunghieu.newsapp.ui.activity.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.edu.trunghieu.newsapp.model.Article
import vn.edu.trunghieu.newsapp.repository.NewsRepository

open class ArticleViewModel(
        private val newsRepository: NewsRepository
): ViewModel() {
        open fun deleteNews(article: Article) = viewModelScope.launch {
                newsRepository.deleteArticle(article)
        }
        open suspend fun findArticleFromDB(article: Article) : Article?{
                val list =  newsRepository.findArticleFromDB(article)
                if (list.isEmpty())
                        return null
                return list[0]
        }

        open suspend fun isArticleSaved(article: Article) : Boolean {
                val count = newsRepository.articleCount(article)
                if (count > 0) return true
                return false
        }

        open fun saveNews(article: Article) = viewModelScope.launch {
//                if (article.urlToImage == null){
//                        article.urlToImage = ""
//                }
                newsRepository.insert(article)
        }

}