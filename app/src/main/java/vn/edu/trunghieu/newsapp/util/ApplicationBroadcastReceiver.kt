package vn.edu.trunghieu.newsapp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.MutableLiveData

class ApplicationBroadcastReceiver : BroadcastReceiver() {
    val hasInternetConnection : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    companion object{
        @Volatile
        private var instance : ApplicationBroadcastReceiver? = null
        private val LOCK = Any()

        operator fun invoke() = instance ?: synchronized(LOCK){
            instance ?: ApplicationBroadcastReceiver().let {
                instance = it
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
//        hasInternetConnection.postValue(true)
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action){
            if (context?.let { isNetworkAvailable(it) } == true){
                hasInternetConnection.postValue(true)
            }else {
                hasInternetConnection.postValue(false)
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
            connectivityManager.activeNetworkInfo?.run {
                when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}