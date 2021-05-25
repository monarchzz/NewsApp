package vn.edu.trunghieu.newsapp.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.edu.trunghieu.newsapp.databinding.ItemArticleBinding
import vn.edu.trunghieu.newsapp.model.Article
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class NewsAdapter @Inject constructor() : ListAdapter<Article, NewsAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    class ArticleViewHolder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)

    class ArticleDiffCallback : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView)
                .load(article.urlToImage)
                .into(articleImage)

            tvTitle.text = article.title
            tvSource.text = article.source.name

            //format date

            try {
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                val date: Date? = format.parse(article.publishedAt)
                val time = DateUtils.getRelativeTimeSpanString(date!!.time)
                tvPublishedAt.text = time
            }catch (e: ParseException){
                tvPublishedAt.text = article.publishedAt
            }

            imgBtnMore.setOnClickListener {
                onClickMoreButtonListener?.let {
                    it(article)
                }
            }

        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { itemClick ->
                itemClick(article)
            }
        }

    }

    private var onItemClickListener: ((Article) -> Unit)? = null
    private var onClickMoreButtonListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
    fun setOnClickMoreButtonListener(listener: (Article) -> Unit){
        onClickMoreButtonListener = listener
    }
}