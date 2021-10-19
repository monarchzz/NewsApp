package vn.edu.trunghieu.newsapp.ui

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import vn.edu.trunghieu.newsapp.R
import vn.edu.trunghieu.newsapp.databinding.ActivityNewsPageBinding
import java.net.URL

class NewsPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            val drawable: Drawable? = ContextCompat
                .getDrawable(this@NewsPageActivity, R.drawable.ic_baseline_arrow_back_24)
            setHomeAsUpIndicator(drawable)
        }

        val sourceName = intent.extras?.get("source_name") as String
        val urlString = intent.extras?.get("url") as String

        val url = URL(urlString)
        val urlNewsPage = "${url.protocol}://${url.host}"

        binding.apply {
            toolbarTitle.text = sourceName
            newsPageWebView.apply {
                webViewClient = WebViewClient()
                loadUrl(urlNewsPage)
            }

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}