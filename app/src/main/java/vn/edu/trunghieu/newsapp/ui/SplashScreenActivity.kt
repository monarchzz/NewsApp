package vn.edu.trunghieu.newsapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import vn.edu.trunghieu.newsapp.ui.activity.news.NewsActivity
import vn.edu.trunghieu.newsapp.util.AppSetting
import javax.inject.Inject

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, NewsActivity::class.java))
        finish()
    }
}