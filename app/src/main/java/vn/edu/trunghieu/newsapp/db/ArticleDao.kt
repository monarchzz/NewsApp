package vn.edu.trunghieu.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import vn.edu.trunghieu.newsapp.model.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Query("select * from articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("select * from articles where url = :url")
    suspend fun findArticle(url: String) : List<Article>

    @Query("select count(*) from articles where url = :url")
    suspend fun articleCount(url: String) : Int
}