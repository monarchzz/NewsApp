package vn.edu.trunghieu.newsapp.model

data class  NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)