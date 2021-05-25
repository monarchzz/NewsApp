package vn.edu.trunghieu.newsapp.ui.activity.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import vn.edu.trunghieu.newsapp.model.Article
import vn.edu.trunghieu.newsapp.repository.NewsRepository
import javax.inject.Inject

@HiltViewModel
class ArticleViewModel @Inject constructor(
        private val newsRepository: NewsRepository
): ViewModel() {
        fun deleteNews(article: Article) = viewModelScope.launch {
                newsRepository.deleteArticle(article)
        }

        suspend fun findArticleFromDB(article: Article) : Article?{
                val list =  newsRepository.findArticleFromDB(article)
                if (list.isEmpty())
                        return null
                return list[0]
        }

        fun saveNews(article: Article) = viewModelScope.launch {
                newsRepository.insert(article)
        }

}