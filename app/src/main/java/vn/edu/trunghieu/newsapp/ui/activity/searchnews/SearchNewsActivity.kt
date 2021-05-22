package vn.edu.trunghieu.newsapp.ui.activity.searchnews

import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import vn.edu.trunghieu.newsapp.R
import vn.edu.trunghieu.newsapp.adapters.NewsAdapter
import vn.edu.trunghieu.newsapp.databinding.ActivitySearchNewsBinding
import vn.edu.trunghieu.newsapp.db.ArticleDatabase
import vn.edu.trunghieu.newsapp.model.Article
import vn.edu.trunghieu.newsapp.model.ItemObjectBottomSheet
import vn.edu.trunghieu.newsapp.repository.NewsRepository
import vn.edu.trunghieu.newsapp.ui.fragment.BottomSheetFragment
import vn.edu.trunghieu.newsapp.ui.activity.NewsPageActivity
import vn.edu.trunghieu.newsapp.ui.activity.article.ArticleActivity
import vn.edu.trunghieu.newsapp.util.ApplicationBroadcastReceiver
import vn.edu.trunghieu.newsapp.util.Constants.Companion.COUNTRY
import vn.edu.trunghieu.newsapp.util.Constants.Companion.LIMIT_PAGE
import vn.edu.trunghieu.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import vn.edu.trunghieu.newsapp.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import vn.edu.trunghieu.newsapp.util.Resource

class SearchNewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchNewsBinding

    private lateinit var applicationBroadcastReceiver: ApplicationBroadcastReceiver
    private lateinit var viewModel: SearchNewsViewModel

    private lateinit var newsAdapter: NewsAdapter

    private var isLoading = false
    private var isScrolling = false
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupRecyclerView()
        setupViewModel()

        searchNews()

    }

    private fun searchNews() {
        var job: Job? = null

        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    job?.cancel()
                    job = query?.let { searchNewsJob(it) }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    job?.cancel()
                    job = newText?.let { searchNewsJob(it) }
                    return false
                }
            })
        }
    }

    private fun searchNewsJob(text: String) = MainScope().launch {
        delay(SEARCH_NEWS_TIME_DELAY)
        if (text.isNotEmpty()){
            viewModel.clearSearchNews()
            viewModel.searchNews(text)
        }
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@SearchNewsActivity)
            addOnScrollListener(scrollListener)
        }

        newsAdapter.apply {
            setOnItemClickListener { article ->
                MainScope().launch {
                    val isSavedNews =
                        withContext(Dispatchers.Default) {
                            viewModel.isArticleSaved(article)
                        }
                    val intent: Intent = Intent(this@SearchNewsActivity,
                        ArticleActivity::class.java).apply {
                        val bundle = Bundle().apply {
                            putSerializable("article", article)
                            putInt("isSavedNews", if (isSavedNews) 0 else 1)
                        }
                        putExtras(bundle)
                    }

                    startActivity(intent)
                }
            }
            setOnClickMoreButtonListener { article ->
                MainScope().launch {
                    val isSavedNews =
                        withContext(Dispatchers.Default) {
                            viewModel.isArticleSaved(article)
                        }
                    clickOpenBottomSheet(article, isSavedNews)
                }
            }
        }
    }

    private fun setupViewModel() {
        applicationBroadcastReceiver = ApplicationBroadcastReceiver()
        val newsRepository = NewsRepository(ArticleDatabase(this))
        val factory = SearchNewsViewModelProviderFactory(applicationBroadcastReceiver,newsRepository)
        viewModel = ViewModelProvider(this,factory).get(SearchNewsViewModel::class.java)

        viewModel.clearSearchNews()

        viewModel.searchNews.observe(this, { response ->
            when(response){
                is Resource.Success -> {
                    isLoading = false

                    response.data?.let { newsResponse ->
                        if (viewModel.searchNewsPage == 1){
                            newsResponse.articles.clear()
                        }
                        newsAdapter.setData(newsResponse.articles.toList())

                        var totalPage = newsResponse.totalResults / QUERY_PAGE_SIZE + 1
                        if (totalPage > LIMIT_PAGE){
                            totalPage = LIMIT_PAGE
                        }

                        totalPage += 1
                        isLastPage = viewModel.searchNewsPage == totalPage

                    }
                }
                is Resource.Error -> {
                    isLoading = false

                    response.message?.let { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    isLoading = true
                }
            }
        })
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.searchToolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            val drawable: Drawable? = ContextCompat
                .getDrawable(this@SearchNewsActivity, R.drawable.ic_baseline_arrow_back_24)
            setHomeAsUpIndicator(drawable)
        }
    }
    private fun clickOpenBottomSheet(article: Article, isSavedNews: Boolean) {
        val dataList: List<ItemObjectBottomSheet> = mutableListOf(
            if (isSavedNews) ItemObjectBottomSheet.Delete() else ItemObjectBottomSheet.Save(),
            ItemObjectBottomSheet.Share(),
            ItemObjectBottomSheet.GoToNewsPage(name = article.source.name),
        )
        val bottomSheetFragment = BottomSheetFragment(dataList)
        bottomSheetFragment.setOnClickListener { itemObjectBottomSheet ->
            when(itemObjectBottomSheet){
                is ItemObjectBottomSheet.Save -> {
                    viewModel.saveNews(article)
                    bottomSheetFragment.dismiss()
                    Toast.makeText(this@SearchNewsActivity, "Article saved successfully",
                        Toast.LENGTH_SHORT).show()
                }
                is ItemObjectBottomSheet.Delete -> {
                    MainScope().launch {
                        val articleInDB =
                            withContext(Dispatchers.Default) {
                                viewModel.findArticleFromDB(article)
                            }
                        articleInDB?.let {
                            viewModel.deleteNews(it)
                        }

                    }

                    bottomSheetFragment.dismiss()
                    Toast.makeText(this@SearchNewsActivity, "Deleted article successfully!",
                        Toast.LENGTH_SHORT).show()
                }
                is ItemObjectBottomSheet.Share -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, article.url)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                    bottomSheetFragment.dismiss()
                }
                is ItemObjectBottomSheet.GoToNewsPage -> {
                    val intent: Intent = Intent(this@SearchNewsActivity,
                        NewsPageActivity::class.java).apply {

                        val bundle = Bundle().apply {
                            putString("url", article.url)
                            putString("source_name", article.source.name)
                        }
                        putExtras(bundle)
                    }
                    startActivity(intent)
                    bottomSheetFragment.dismiss()
                }
            }
        }
        bottomSheetFragment.show(supportFragmentManager,bottomSheetFragment.tag)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            val isTouchScroll = newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
            val isFling = newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING
            isScrolling = isFling || isTouchScroll
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndIsNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItem + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItem > 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE


            val shouldLoading = isNotLoadingAndIsNotLastPage && isAtLastItem
                    && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldLoading){
                viewModel.searchNews(COUNTRY)
                isScrolling = false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(applicationBroadcastReceiver, intentFilter)

    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(applicationBroadcastReceiver)
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