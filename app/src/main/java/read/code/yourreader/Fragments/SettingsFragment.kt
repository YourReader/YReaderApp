package read.code.yourreader.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.preference.Preference


import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import read.code.yourreader.R

class SettingsFragment :  PreferenceFragmentCompat()  {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings2)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val sp=PreferenceManager.getDefaultSharedPreferences(requireContext())

        val swipeBrightNess=sp.getBoolean("BrightnessBySwipe",true)

        val darkMode=sp.getBoolean("DarkMode",false)
        val screenOn=sp.getBoolean("ScreenOn",false)
        val AccessDocs=sp.getBoolean("AccessDocs",false)

        val upgradeToPremium=sp.getString("Upgrade","")


        Log.d("TAG", "onCreatePreferences: $swipeBrightNess \n $darkMode " +
                "\n $screenOn \n $AccessDocs \n $upgradeToPremium \n ")



    }


}

