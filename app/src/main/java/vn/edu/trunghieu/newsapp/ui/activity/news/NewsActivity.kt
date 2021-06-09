package vn.edu.trunghieu.newsapp.ui.activity.news

import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import vn.edu.trunghieu.newsapp.R
import vn.edu.trunghieu.newsapp.databinding.ActivityNewsBinding
import vn.edu.trunghieu.newsapp.ui.activity.searchnews.SearchNewsActivity
import vn.edu.trunghieu.newsapp.ApplicationBroadcastReceiver
import vn.edu.trunghieu.newsapp.repository.NewsRepository
import vn.edu.trunghieu.newsapp.util.Constants.COUNTRY
import javax.inject.Inject
import android.net.ConnectivityManager as ConnectivityManager

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewsBinding

    @Inject lateinit var applicationBroadcastReceiver: ApplicationBroadcastReceiver
    @Inject lateinit var newsRepository: NewsRepository
    val viewModel: NewsViewModel by viewModels()

    private var firstTimeInternetDisconnect: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            val drawable: Drawable? = ContextCompat
                .getDrawable(this@NewsActivity,R.drawable.ic_baseline_home_24)
            setHomeAsUpIndicator(drawable)
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.apply {
            bottomNavigationView.setupWithNavController(navController)
        }

        firstTimeInternetDisconnect = false
        applicationBroadcastReceiver.hasInternetConnection.observe(this, { hasInternetConnection ->
            if (hasInternetConnection){
                if (firstTimeInternetDisconnect)
                    Snackbar.make(binding.root, "Internet connected", Snackbar.LENGTH_LONG).run {
                        anchorView = binding.bottomNavigationView
                        show()
                    }
                newsRepository.hasInternetConnection = true

            }else{
                Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_LONG).run {
                    anchorView = binding.bottomNavigationView
                    show()
                }
                if (!firstTimeInternetDisconnect)
                    firstTimeInternetDisconnect = true

                newsRepository.hasInternetConnection = false
            }
            viewModel.getTopNewsHeadlines(COUNTRY)
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.icon_search_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.searchNews -> {
                startActivity(Intent(this@NewsActivity, SearchNewsActivity::class.java))
            }
            android.R.id.home -> {
                binding.bottomNavigationView.selectedItemId = R.id.newsListFragment
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(applicationBroadcastReceiver, intentFilter)
        firstTimeInternetDisconnect = false // reset
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(applicationBroadcastReceiver)
    }

}
