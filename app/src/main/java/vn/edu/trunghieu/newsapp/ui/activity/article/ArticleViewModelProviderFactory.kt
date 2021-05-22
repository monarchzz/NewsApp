package vn.edu.trunghieu.newsapp.ui.activity.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vn.edu.trunghieu.newsapp.repository.NewsRepository

class ArticleViewModelProviderFactory(
    private val newsRepository: NewsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ArticleViewModel(newsRepository) as T
    }
}