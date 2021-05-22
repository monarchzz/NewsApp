package vn.edu.trunghieu.newsapp.ui.activity.news

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import retrofit2.Response
import vn.edu.trunghieu.newsapp.model.NewsResponse
import vn.edu.trunghieu.newsapp.repository.NewsRepository
import vn.edu.trunghieu.newsapp.ui.activity.article.ArticleViewModel
import vn.edu.trunghieu.newsapp.util.ApplicationBroadcastReceiver
import vn.edu.trunghieu.newsapp.util.Constants.Companion.COUNTRY
import vn.edu.trunghieu.newsapp.util.Constants.Companion.GET_DATA_TIMEOUT
import vn.edu.trunghieu.newsapp.util.Constants.Companion.INTERNET_STATE_DELAY
import vn.edu.trunghieu.newsapp.util.Resource
import java.io.IOException

class NewsViewModel(
    private val applicationBroadcastReceiver: ApplicationBroadcastReceiver,
    private val newsRepository: NewsRepository
) : ArticleViewModel( newsRepository) {

    val topNewsHeadlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var topNewsHeadlinesPage = 1
    private var topNewsHeadlinesResponse : NewsResponse? = null



    fun clearTopNewsHeadLines(){
        topNewsHeadlinesPage = 1
        topNewsHeadlinesResponse = null
    }

    init {
        topNewsHeadlines.postValue(Resource.Loading())
        getTopNewsHeadlines(COUNTRY)
    }

    fun getSaveNews() = newsRepository.getSavedNews()

    fun getTopNewsHeadlines(country: String) = viewModelScope.launch {
        try {
            withTimeout(GET_DATA_TIMEOUT){
                if (applicationBroadcastReceiver.hasInternetConnection.value == null){
                    delay(INTERNET_STATE_DELAY)
                }
                val hasNetworkConnection = applicationBroadcastReceiver.hasInternetConnection.value
                if (hasNetworkConnection!= null && hasNetworkConnection){
                    val response = newsRepository.getTopNewsHeadlines(country, topNewsHeadlinesPage)
                    topNewsHeadlines.postValue(handleTopNewsHeadlinesResponse(response))
                }else {
                    topNewsHeadlines.postValue(Resource.Error("No internet connection in newsList"))
                }
            }

        }catch (t: TimeoutCancellationException){
            topNewsHeadlines.postValue(Resource.Error("Timeout"))
        } catch (t: IOException){
            topNewsHeadlines.postValue(Resource.Error("Network Failure"))
        }catch (t: Exception){
            topNewsHeadlines.postValue(t.message?.let { Resource.Error(it) })
        }
    }

    private fun handleTopNewsHeadlinesResponse(response: Response<NewsResponse>) : Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                topNewsHeadlinesPage++
                if (topNewsHeadlinesResponse == null){
                    topNewsHeadlinesResponse = resultResponse
                }else {
                    val newArticle = resultResponse.articles
                    topNewsHeadlinesResponse?.articles?.addAll(newArticle)
                }

                return Resource.Success(topNewsHeadlinesResponse ?: resultResponse)
            }
        }
        return Resource.Error("Can't connect to server")
    }

}