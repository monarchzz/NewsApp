package vn.edu.trunghieu.newsapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import vn.edu.trunghieu.newsapp.R
import vn.edu.trunghieu.newsapp.databinding.ActivityNewsBinding
import vn.edu.trunghieu.newsapp.databinding.FragmentSettingBinding
import vn.edu.trunghieu.newsapp.ui.NewsActivity
import vn.edu.trunghieu.newsapp.util.AppSetting
import vn.edu.trunghieu.newsapp.util.Constants.THEME_DARK
import vn.edu.trunghieu.newsapp.util.Constants.THEME_LIGHT
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityNewsBinding : ActivityNewsBinding

    @Inject lateinit var appSetting: AppSetting

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        activityNewsBinding = (activity as NewsActivity).binding
        activityNewsBinding.toolbarTitle.text = getString(R.string.setting)

        settingTheme()

        return binding.root
    }

    private fun settingTheme() {

        when(appSetting.theme){
            THEME_LIGHT -> binding.rbLight.isChecked = true
            THEME_DARK -> binding.rbDark.isChecked = true
            else -> binding.rbLight.isChecked = true
        }

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