package vn.edu.trunghieu.newsapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import javax.inject.Singleton

@ActivityScoped
class ApplicationBroadcastReceiver @Inject constructor() : BroadcastReceiver() {

    companion object{
        const val NO_INTERNET_CONNECTION = 0
        const val HAS_INTERNET_CONNECTION = 1
    }

    private var onInternetStateChange: ((Int) -> Unit)? = null

    fun setOnInternetStateChangeListener(listener: (Int) -> Unit){
        onInternetStateChange = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action){
            if (context?.let { isNetworkAvailable(it) } == true){
                onInternetStateChange?.let { it(HAS_INTERNET_CONNECTION) }
            }else {
                onInternetStateChange?.let { it(NO_INTERNET_CONNECTION) }
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when{
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}