package vn.edu.trunghieu.newsapp.ui.activity.searchnews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vn.edu.trunghieu.newsapp.repository.NewsRepository
import vn.edu.trunghieu.newsapp.util.ApplicationBroadcastReceiver

class SearchNewsViewModelProviderFactory(
    private val applicationBroadcastReceiver: ApplicationBroadcastReceiver,
    private val repository: NewsRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchNewsViewModel(applicationBroadcastReceiver,repository) as T
    }
}