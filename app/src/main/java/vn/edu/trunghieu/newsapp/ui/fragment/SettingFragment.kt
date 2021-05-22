package vn.edu.trunghieu.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.edu.trunghieu.newsapp.databinding.ActivityNewsBinding
import vn.edu.trunghieu.newsapp.databinding.FragmentSettingBinding
import vn.edu.trunghieu.newsapp.ui.activity.news.NewsActivity
import vn.edu.trunghieu.newsapp.util.AppSetting
import vn.edu.trunghieu.newsapp.util.Constants.Companion.THEME_DARK
import vn.edu.trunghieu.newsapp.util.Constants.Companion.THEME_LIGHT

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityNewsBinding : ActivityNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityNewsBinding = (activity as NewsActivity).binding
        activityNewsBinding.appBar.visibility = View.GONE

        settingTheme()

    }

    private fun settingTheme() {
        val appSetting = AppSetting(activity as NewsActivity)
        when(appSetting.theme){
            THEME_LIGHT -> binding.rbLight.isChecked = true
            THEME_DARK -> binding.rbDark.isChecked = true
            else -> binding.rbLight.isChecked = true
        }

        Log.i("TAG", "settingTheme: ${appSetting.theme}")
        binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                binding.rbLight.id -> appSetting.theme = THEME_LIGHT
                binding.rbDark.id -> appSetting.theme = THEME_DARK
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}