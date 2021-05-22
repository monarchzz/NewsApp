package vn.edu.trunghieu.newsapp.util

class Constants {
    companion object{
        const val API_KEY = "46ee2f3ab94d491c91f5a81cec526571"
        const val COUNTRY = "us"
        const val SORT_TYPE = "popularity"
        const val BASE_URL = "https://newsapi.org"
        const val SEARCH_NEWS_TIME_DELAY = 500L
        const val QUERY_PAGE_SIZE = 20
        const val LIMIT_PAGE = 5
        const val INTERNET_STATE_DELAY = 500L
        const val GET_DATA_TIMEOUT = 10000L
        const val PREFERENCE_FILE_KEY = "pref_key"
        const val THEME_KEY = "theme"
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
    }
}