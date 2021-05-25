package vn.edu.trunghieu.newsapp.db

import androidx.room.*
import vn.edu.trunghieu.newsapp.model.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase :RoomDatabase() {
    abstract fun getArticleDao() : ArticleDao

}