package read.code.yourreader.Fragments

import android.content.ContentValues.TAG
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialDialogs
import com.google.firebase.auth.FirebaseAuth
import read.code.yourreader.R
import read.code.yourreader.databinding.FragmentSettingsBinding
import read.code.yourreader.di.components.DaggerFactoryComponent
import read.code.yourreader.di.modules.FactoryModule
import read.code.yourreader.di.modules.RepositoryModule
import read.code.yourreader.mvvm.repository.MainRepository
import read.code.yourreader.mvvm.viewmodels.MainViewModel


class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var component: DaggerFactoryComponent
    private lateinit var mAuth: FirebaseAuth
    var permissionRevoke=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        mAuth = FirebaseAuth.getInstance()
        component = DaggerFactoryComponent.builder()
            .repositoryModule(RepositoryModule(requireContext()))
            .factoryModule(FactoryModule(MainRepository(requireContext())))
            .build() as DaggerFactoryComponent
        viewModel = ViewModelProviders.of(this, component.getFactory())
            .get(MainViewModel::class.java)


        binding.SignOutSettings.setOnClickListener {
            viewModel.signOut()
            requireActivity().finish()
        }

        binding.languageChoose.setOnClickListener {
            val installIntent = Intent()
            installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
            startActivity(installIntent)
        }
        binding.settingsTts.setOnClickListener {
            TODO("Remaining")
        }

        //Dark Mode Settings
        val sharedPreferencesDark: SharedPreferences =
            requireActivity().getSharedPreferences("switchDark", MODE_PRIVATE)
        val editor = sharedPreferencesDark.edit()
        binding.darkModeSwitch.isChecked = sharedPreferencesDark.getBoolean("switchDark", false)


        Log.d(TAG, "onCreateView: theme=$")

        binding.darkModeSwitch.setOnClickListener {
            if (binding.darkModeSwitch.isChecked) {
                editor.putBoolean("switchDark", true)
                editor.apply()
                Log.d(TAG, "onCreateView: Settings Changed to dark")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            if (!binding.darkModeSwitch.isChecked) {
                editor.putBoolean("switchDark", false)
                editor.apply()
                Log.d(TAG, "onCreateView: Settings Changed to Light")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


            }
            editor.apply()
            Toast.makeText(requireContext(), "Setting Saved", Toast.LENGTH_SHORT).show()
        }
        Log.d(TAG, "onCreateView: is checked = ${binding.darkModeSwitch.isChecked}")


       //Screen On Settings
        val sharedPreferencesScreenOn: SharedPreferences =
            requireActivity().getSharedPreferences("switchScreen", MODE_PRIVATE)
        val editor2 = sharedPreferencesScreenOn.edit()
        binding.screenOnSwitch.isChecked = sharedPreferencesScreenOn.getBoolean("switchScreen", true)


        binding.screenOnSwitch.setOnClickListener {
            if (binding.screenOnSwitch.isChecked) {
                editor2.putBoolean("switchScreen", true)
                editor2.apply()
                Log.d(TAG, "onCreateView: switchScreen Changed to On")
            }
            if (!binding.screenOnSwitch.isChecked) {
                editor2.putBoolean("switchScreen", false)
                editor2.apply()
                Log.d(TAG, "onCreateView: switchScreen Changed to Off")


            }
            editor2.apply()
            Toast.makeText(requireContext(), "Setting Saved", Toast.LENGTH_SHORT).show()
        }




        //Brightness  Settings
        val sharedPreferencesBrightness: SharedPreferences =
            requireActivity().getSharedPreferences("brightNessSwitch", MODE_PRIVATE)
        val editor3 = sharedPreferencesBrightness.edit()
        binding.brightNessSwitch.isChecked = sharedPreferencesBrightness.getBoolean("brightNessSwitch", true)


        binding.brightNessSwitch.setOnClickListener {
            if (binding.brightNessSwitch.isChecked) {
                editor3.putBoolean("brightNessSwitch", true)
                editor3.apply()
                Log.d(TAG, "onCreateView: brightNessSwitch Changed to On")
            }
            if (!binding.brightNessSwitch.isChecked) {
                editor3.putBoolean("brightNessSwitch", false)
                editor3.apply()
                Log.d(TAG, "onCreateView: brightNessSwitch Changed to Off")
            }
            editor3.apply()
        }



        //AccessFiles  Settings
        val sharedPreferencesAccess : SharedPreferences =
            requireActivity().getSharedPreferences("switchAccess", MODE_PRIVATE)
        val editor4 = sharedPreferencesAccess.edit()
        binding.switchAccess.isChecked = sharedPreferencesAccess.getBoolean("switchAccess", false)


        binding.switchAccess.setOnClickListener {
            if (binding.switchAccess.isChecked) {
                editor4.putBoolean("switchAccess", true)
                editor4.apply()
                Log.d(TAG, "onCreateView: switchAccess Changed to On")
            }
            if (!binding.switchAccess.isChecked) {
                val dialog=AlertDialog.Builder(requireContext())
                    .setTitle("Permission Revoke")
                    .setMessage(R.string.dialog_messgae)
                    .setPositiveButton("Yes"
                    ) { dialog, which ->
                        permissionRevoke=true
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancle"
                    ) { dialog, which ->
                        binding.switchAccess.isChecked =true
                        dialog.dismiss()
                    }

                dialog.show()
                editor4.putBoolean("switchAccess", false)
                editor4.apply()
                Log.d(TAG, "onCreateView: switchAccess Changed to Off")
            }
            editor4.apply()
        }











        return binding.root
    }


}