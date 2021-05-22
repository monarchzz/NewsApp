package vn.edu.trunghieu.newsapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.edu.trunghieu.newsapp.util.Constants

class RetrofitInstance {
    companion object{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }
}