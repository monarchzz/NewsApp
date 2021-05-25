package vn.edu.trunghieu.newsapp.ui.activity.article

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.edu.trunghieu.newsapp.R
import vn.edu.trunghieu.newsapp.databinding.ActivityArticleBinding
import vn.edu.trunghieu.newsapp.model.Article
import vn.edu.trunghieu.newsapp.ui.activity.NewsPageActivity

@AndroidEntryPoint
class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding
    private val viewModel: ArticleViewModel by viewModels()

    private var isSavedNews = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            val drawable: Drawable? = ContextCompat
                .getDrawable(this@ArticleActivity, R.drawable.ic_baseline_arrow_back_24)
            setHomeAsUpIndicator(drawable)
        }

        val article: Article = intent.extras?.getSerializable("article") as Article
        val num = intent.extras?.getInt("isSavedNews")
        isSavedNews = num == 0

        setupFunction(article)

        binding.articleWebView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }
    }

    private fun setupFunction(article: Article) {

        binding.apply {
            val text = "Go to ${article.source.name}"
            btnGoToNewsPage.apply {
                this.text = text
                setOnClickListener {
                    val intent: Intent = Intent(this@ArticleActivity,
                        NewsPageActivity::class.java).apply {

                        val bundle = Bundle().apply {
                            putString("url", article.url)
                            putString("source_name", article.source.name)
                        }
                        putExtras(bundle)
                    }
                    startActivity(intent)
                }
            }

            updateSaveIcon()
            imgBtnSave.setOnClickListener {
                if (isSavedNews){
                    MainScope().launch {
                        val articleInDB = withContext(Dispatchers.Default) {
                                viewModel.findArticleFromDB(article)
                         }
                        articleInDB?.let {
                            viewModel.deleteNews(it)
                        }
                        isSavedNews = false
                        updateSaveIcon()
                        Toast.makeText(this@ArticleActivity, "Deleted article successfully!",
                            Toast.LENGTH_SHORT).show()
                    }

                }else {
                    viewModel.saveNews(article)
                    isSavedNews = true
                    updateSaveIcon()
                    Toast.makeText(this@ArticleActivity, "Article saved successfully",
                        Toast.LENGTH_SHORT).show()
                }
            }

            imgBtnShare.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, article.url)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }
    }

    private fun updateSaveIcon(){
        if (isSavedNews){
            binding.imgBtnSave.setImageResource(R.drawable.ic_baseline_bookmark_24)
        }else {
            binding.imgBtnSave.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
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