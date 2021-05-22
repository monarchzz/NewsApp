package vn.edu.trunghieu.newsapp.ui.activity.searchnews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import vn.edu.trunghieu.newsapp.model.NewsResponse
import vn.edu.trunghieu.newsapp.repository.NewsRepository
import vn.edu.trunghieu.newsapp.ui.activity.article.ArticleViewModel
import vn.edu.trunghieu.newsapp.util.ApplicationBroadcastReceiver
import vn.edu.trunghieu.newsapp.util.Constants
import vn.edu.trunghieu.newsapp.util.Resource
import java.io.IOException

class SearchNewsViewModel(
    private val applicationBroadcastReceiver: ApplicationBroadcastReceiver,
    private val newsRepository: NewsRepository
) : ArticleViewModel(newsRepository) {

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    private var searchNewsResponse : NewsResponse? = null

    fun clearSearchNews(){
        searchNewsPage = 1
        searchNewsResponse = null
    }


    fun searchNews(searchQuery: String ) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        try {
            withTimeout(Constants.GET_DATA_TIMEOUT){
                if (applicationBroadcastReceiver.hasInternetConnection.value!!){
                    val response = newsRepository.searchForNews(searchQuery, searchNewsPage)
                    searchNews.postValue(handleSearchForNewsResponse(response))
                }else {
                    searchNews.postValue(Resource.Error("No internet connection"))
                }
            }
        }catch (t: TimeoutCancellationException){
            searchNews.postValue(Resource.Error("Timeout"))
        }catch (t: IOException){
            searchNews.postValue(Resource.Error("Network Failure"))
        }catch (t: Exception){

            searchNews.postValue(t.message?.let { Resource.Error("$it this is unknown exception") })
        }
    }

    private fun handleSearchForNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null){
                    searchNewsResponse = resultResponse
                }else {
                    val newArticle = resultResponse.articles
                    searchNewsResponse?.articles?.addAll(newArticle)
                }

                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}