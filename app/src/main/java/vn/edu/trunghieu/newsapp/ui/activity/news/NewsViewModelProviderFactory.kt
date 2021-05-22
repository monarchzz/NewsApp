package vn.edu.trunghieu.newsapp.ui.activity.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vn.edu.trunghieu.newsapp.repository.NewsRepository
import vn.edu.trunghieu.newsapp.util.ApplicationBroadcastReceiver

class NewsViewModelProviderFactory(
    private val applicationBroadcastReceiver: ApplicationBroadcastReceiver,
    private val repository: NewsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(applicationBroadcastReceiver,repository) as T
    }
}