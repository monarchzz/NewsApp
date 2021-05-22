package vn.edu.trunghieu.newsapp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import vn.edu.trunghieu.newsapp.util.Constants.Companion.PREFERENCE_FILE_KEY
import vn.edu.trunghieu.newsapp.util.Constants.Companion.THEME_DARK
import vn.edu.trunghieu.newsapp.util.Constants.Companion.THEME_KEY
import vn.edu.trunghieu.newsapp.util.Constants.Companion.THEME_LIGHT

class AppSetting(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)

    var theme: Int = pref.getInt(THEME_KEY, THEME_LIGHT)
    set(value) {
        with(pref.edit()){
            putInt(THEME_KEY,value)
            apply()
        }
        field = value
        changeTheme()
    }

    init {
        changeTheme()

    }
    companion object{
        @Volatile
        private var instance : AppSetting? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: AppSetting(context).let {
                instance = it
            }
        }
    }


    private fun changeTheme(){
        AppCompatDelegate.setDefaultNightMode(when(theme){
            THEME_LIGHT -> MODE_NIGHT_NO
            THEME_DARK -> MODE_NIGHT_YES
            else -> MODE_NIGHT_NO
        })
    }

}