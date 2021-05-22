package vn.edu.trunghieu.newsapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import vn.edu.trunghieu.newsapp.model.NewsResponse
import vn.edu.trunghieu.newsapp.util.Constants

interface NewsApi {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") countryCode: String = Constants.COUNTRY,
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = Constants.API_KEY
    ) : Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q") searchQuery: String,
        @Query("page") pageNumber: Int = 1,
        @Query("sortBy") sortBy: String = Constants.SORT_TYPE,
        @Query("apiKey") apiKey: String = Constants.API_KEY
    ) : Response<NewsResponse>

}