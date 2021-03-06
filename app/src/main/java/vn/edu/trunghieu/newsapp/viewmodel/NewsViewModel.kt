package vn.edu.trunghieu.newsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import retrofit2.Response
import vn.edu.trunghieu.newsapp.model.Article
import vn.edu.trunghieu.newsapp.model.NewsResponse
import vn.edu.trunghieu.newsapp.repository.NewsRepository
import vn.edu.trunghieu.newsapp.util.Constants.GET_DATA_TIMEOUT
import vn.edu.trunghieu.newsapp.util.Resource
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel(){

    val topNewsHeadlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var topNewsHeadlinesPage = 1
    private var topNewsHeadlinesResponse : NewsResponse? = null
    private var isGetTNHLFTheFirstTime = true

    init {
        topNewsHeadlines.postValue(Resource.Loading())
    }

    fun getTopNewsHeadlinesOneTime(country: String){
        if (isGetTNHLFTheFirstTime){
            isGetTNHLFTheFirstTime = false
            clearTopNewsHeadLines()
            getTopNewsHeadlines(country)
        }
    }

    fun clearTopNewsHeadLines(){
        topNewsHeadlinesPage = 1
        topNewsHeadlinesResponse = null
    }

    suspend fun isArticleSaved(article: Article) : Boolean {
        val count = newsRepository.articleCount(article)
        if (count > 0) return true
        return false
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

    fun deleteNews(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun getSaveNews() = newsRepository.getSavedNews()

    fun getTopNewsHeadlines(country: String) = viewModelScope.launch {
        try {
            withTimeout(GET_DATA_TIMEOUT){
                if (newsRepository.hasInternetConnection){
                    val response = newsRepository.getTopNewsHeadlines(country, topNewsHeadlinesPage)
                    topNewsHeadlines.postValue(handleTopNewsHeadlinesResponse(response))
                }else {
                    topNewsHeadlines.postValue(Resource.Error(""))
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