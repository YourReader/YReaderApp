package read.code.yourreader.Fragments

import android.annotation.SuppressLint
import android.os.Bundle


import androidx.preference.PreferenceFragmentCompat
import read.code.yourreader.R

class SettingsFragment :  PreferenceFragmentCompat() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings2)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }
}

