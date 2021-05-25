package vn.edu.trunghieu.newsapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import vn.edu.trunghieu.newsapp.db.ArticleDao
import vn.edu.trunghieu.newsapp.db.ArticleDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideArticleDatabase(@ApplicationContext appContext: Context): ArticleDatabase = Room.databaseBuilder(
        appContext,
        ArticleDatabase::class.java,
        "article.db"
    ).build()

    @Provides
    @Singleton
    fun provideArticleDao(database: ArticleDatabase): ArticleDao = database.getArticleDao()

}