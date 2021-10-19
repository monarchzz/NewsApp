package vn.edu.trunghieu.newsapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import vn.edu.trunghieu.newsapp.R
import vn.edu.trunghieu.newsapp.adapters.NewsAdapter
import vn.edu.trunghieu.newsapp.databinding.ActivityNewsBinding
import vn.edu.trunghieu.newsapp.databinding.FragmentSavedNewsBinding
import vn.edu.trunghieu.newsapp.model.Article
import vn.edu.trunghieu.newsapp.model.ItemObjectBottomSheet
import vn.edu.trunghieu.newsapp.ui.NewsActivity
import vn.edu.trunghieu.newsapp.viewmodel.NewsViewModel
import vn.edu.trunghieu.newsapp.ui.NewsPageActivity
import vn.edu.trunghieu.newsapp.ui.ArticleActivity
import javax.inject.Inject

@AndroidEntryPoint
class SavedNewsFragment : Fragment() {
    private var _binding: FragmentSavedNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityNewsBinding : ActivityNewsBinding

    private lateinit var viewModel: NewsViewModel

    @Inject lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedNewsBinding.inflate(inflater, container, false)

        viewModel = (activity as NewsActivity).viewModel

        activityNewsBinding = (activity as NewsActivity).binding
        activityNewsBinding.toolbarTitle.text = getString(R.string.saved_news)

        setupRecyclerView()

        newsAdapter.apply {
            setOnItemClickListener { article ->
                MainScope().launch {
                    val isSavedNews = viewModel.isArticleSaved(article)

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
                clickOpenBottomSheet(article)
            }
        }

        viewModel.getSaveNews().observe(viewLifecycleOwner, { articles ->
            newsAdapter.submitList(articles)
        })
        ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(binding.rvSavedNews)

        return binding.root
    }

    private fun clickOpenBottomSheet(article: Article) {
        val dataList: List<ItemObjectBottomSheet> = mutableListOf(
            ItemObjectBottomSheet.Delete() ,
            ItemObjectBottomSheet.Share(),
            ItemObjectBottomSheet.GoToNewsPage(name = article.source.name),
        )
        val bottomSheetFragment = BottomSheetFragment(dataList)
        bottomSheetFragment.setOnClickListener { itemObjectBottomSheet ->
            when(itemObjectBottomSheet){
                is ItemObjectBottomSheet.Delete -> {
                    MainScope().launch {
                        val articleInDB = viewModel.findArticleFromDB(article)

                        articleInDB?.let {
                            viewModel.deleteNews(it)
                        }

                    }

                    bottomSheetFragment.dismiss()
                    Snackbar.make(binding.root, "Deleted article successfully!", Snackbar.LENGTH_LONG).apply {
                        anchorView = activityNewsBinding.bottomNavigationView
                        setAction("Undo"){
                            viewModel.saveNews(article)
                        }
                        show()

                    }
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
                else -> {}
            }
        }
        activity?.let { bottomSheetFragment.show(it.supportFragmentManager, bottomSheetFragment.tag) }
    }
    private val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            val article = newsAdapter.currentList[position]

            viewModel.deleteNews(article)

            Snackbar.make(binding.root, "Deleted article successfully!", Snackbar.LENGTH_LONG).apply {
                anchorView = activityNewsBinding.bottomNavigationView
                setAction("Undo"){
                    viewModel.saveNews(article)
                }
                show()

            }
        }

    }
    private fun setupRecyclerView(){
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}