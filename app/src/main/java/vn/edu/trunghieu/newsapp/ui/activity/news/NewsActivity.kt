package vn.edu.trunghieu.newsapp.ui.activity.news

import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import vn.edu.trunghieu.newsapp.R
import vn.edu.trunghieu.newsapp.databinding.ActivityNewsBinding
import vn.edu.trunghieu.newsapp.db.ArticleDatabase
import vn.edu.trunghieu.newsapp.repository.NewsRepository
import vn.edu.trunghieu.newsapp.ui.activity.searchnews.SearchNewsActivity
import vn.edu.trunghieu.newsapp.util.AppSetting
import vn.edu.trunghieu.newsapp.util.ApplicationBroadcastReceiver
import vn.edu.trunghieu.newsapp.util.Constants.Companion.THEME_DARK
import vn.edu.trunghieu.newsapp.util.Constants.Companion.THEME_LIGHT
import android.net.ConnectivityManager as ConnectivityManager

class NewsActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewsBinding
    lateinit var viewModel: NewsViewModel

    lateinit var applicationBroadcastReceiver: ApplicationBroadcastReceiver

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

        applicationBroadcastReceiver = ApplicationBroadcastReceiver()

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val factory = NewsViewModelProviderFactory(applicationBroadcastReceiver,newsRepository)
        viewModel = ViewModelProvider(this,factory).get(NewsViewModel::class.java)

        firstTimeInternetDisconnect = false
        applicationBroadcastReceiver.hasInternetConnection.observe(this, { hasInternetConnection ->
            if (hasInternetConnection){
                if (firstTimeInternetDisconnect)
                    Toast.makeText(this@NewsActivity,"Internet connected",
                        Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@NewsActivity,"No internet connection",
                    Toast.LENGTH_SHORT).show()
                if (!firstTimeInternetDisconnect)
                    firstTimeInternetDisconnect = true
            }
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