package vn.edu.trunghieu.newsapp.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.edu.trunghieu.newsapp.util.Constants

abstract class PaginationScrollListener:
    RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount

        val isNotLoadingAndIsNotLastPage = !isLoading() && !isLastPage()
        val isAtLastItem = firstVisibleItem + visibleItemCount >= totalItemCount
        val isNotAtBeginning = firstVisibleItem > 0
        val isTotalMoreThanPageSize = totalItemCount >= Constants.QUERY_PAGE_SIZE


        val shouldLoading = isNotLoadingAndIsNotLastPage && isAtLastItem
                && isNotAtBeginning && isTotalMoreThanPageSize

        if (shouldLoading){
            loadItem()
        }
    }

    abstract fun isLoading(): Boolean
    abstract fun isLastPage(): Boolean
    abstract fun loadItem()
}