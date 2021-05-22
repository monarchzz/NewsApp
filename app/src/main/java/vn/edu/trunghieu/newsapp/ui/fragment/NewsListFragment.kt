package vn.edu.trunghieu.newsapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import vn.edu.trunghieu.newsapp.R
import vn.edu.trunghieu.newsapp.adapters.NewsAdapter
import vn.edu.trunghieu.newsapp.databinding.ActivityNewsBinding
import vn.edu.trunghieu.newsapp.databinding.FragmentNewsListBinding
import vn.edu.trunghieu.newsapp.model.Article
import vn.edu.trunghieu.newsapp.model.ItemObjectBottomSheet
import vn.edu.trunghieu.newsapp.ui.activity.news.NewsActivity
import vn.edu.trunghieu.newsapp.ui.activity.news.NewsViewModel
import vn.edu.trunghieu.newsapp.ui.activity.NewsPageActivity
import vn.edu.trunghieu.newsapp.ui.activity.article.ArticleActivity
import vn.edu.trunghieu.newsapp.util.ApplicationBroadcastReceiver
import vn.edu.trunghieu.newsapp.util.Constants
import vn.edu.trunghieu.newsapp.util.Constants.Companion.COUNTRY
import vn.edu.trunghieu.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import vn.edu.trunghieu.newsapp.util.Resource

class NewsListFragment : Fragment() {
    private var _binding: FragmentNewsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NewsViewModel
    private lateinit var activityNewsBinding : ActivityNewsBinding

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var applicationBroadcastReceiver: ApplicationBroadcastReceiver

    private var isLoading = false
    private var isScrolling = false
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsActivity).viewModel
        activityNewsBinding = (activity as NewsActivity).binding
        applicationBroadcastReceiver = (activity as NewsActivity).applicationBroadcastReceiver

        activityNewsBinding.appBar.visibility = View.VISIBLE
        activityNewsBinding.toolbarTitle.text = getString(R.string.news_list_title)

        setupRecyclerView()
        binding.swipeRefreshLayoutListNews.setOnRefreshListener {
            refreshData()
        }

        newsAdapter.apply {
            setOnItemClickListener { article ->
                MainScope().launch {
                    val isSavedNews =
                        withContext(Dispatchers.Default) {
                            viewModel.isArticleSaved(article)
                        }
                    val intent: Intent = Intent(activity,
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


        viewModel.topNewsHeadlines.observe(viewLifecycleOwner, { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.setData(newsResponse.articles.toList())
                        var totalPage = newsResponse.totalResults / QUERY_PAGE_SIZE + 1
                        if (totalPage > Constants.LIMIT_PAGE){
                            totalPage = Constants.LIMIT_PAGE
                        }
                        totalPage += 1
                        isLastPage = viewModel.topNewsHeadlinesPage == totalPage

                    }

                }
                is Resource.Error -> {
                    hideProgressBar()
                        response.message?.let { message ->
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                            Log.i("TAG", "onViewCreated error: $message")
                    }

                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun refreshData(){
        val hasInternetConnection = applicationBroadcastReceiver.hasInternetConnection.value
        if (hasInternetConnection != null && hasInternetConnection){
            viewModel.clearTopNewsHeadLines()
            viewModel.getTopNewsHeadlines(COUNTRY)
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
                    Toast.makeText(activity, "Article saved successfully", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(activity, "Deleted article successfully!", Toast.LENGTH_SHORT).show()
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
                    val intent: Intent = Intent(activity, NewsPageActivity::class.java).apply {
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
        activity?.let { bottomSheetFragment.show(it.supportFragmentManager, bottomSheetFragment.tag) }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = ProgressBar.INVISIBLE
        isLoading = false
        binding.swipeRefreshLayoutListNews.isRefreshing = false
    }
    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = ProgressBar.VISIBLE
        isLoading = true
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
                viewModel.getTopNewsHeadlines(COUNTRY)
                isLoading = true
            }
        }
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvNewsList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@NewsListFragment.scrollListener)
        }
        
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}