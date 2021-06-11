package vn.edu.trunghieu.newsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import vn.edu.trunghieu.newsapp.util.AppSetting
import javax.inject.Inject

@HiltAndroidApp
class NewsApplication : Application(){
    @Inject lateinit var appSetting: AppSetting

    override fun onCreate() {
        super.onCreate()

        appSetting.applySettingTheme()
    }


}