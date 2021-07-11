package read.code.yourreader.activities

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import read.code.yourreader.Fragments.*
import read.code.yourreader.R
import read.code.yourreader.databinding.ActivityMainBinding
import read.code.yourreader.di.components.DaggerFactoryComponent
import read.code.yourreader.di.modules.FactoryModule
import read.code.yourreader.di.modules.RepositoryModule
import read.code.yourreader.mvvm.repository.MainRepository
import read.code.yourreader.mvvm.viewmodels.MainViewModel
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: MainViewModel
    private lateinit var component: DaggerFactoryComponent
    private var tts: TextToSpeech? = null
    private val TAG = "MainActivity"
    private var currentuser: FirebaseUser? = null
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setSupportActionBar(binding.toolbarMain)

        binding.toolbarMain.showOverflowMenu()

        checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, "Storage", 100)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.reading_now -> {
                    binding.toolbarMain.title = "Reading now"
                    fragmentTransition(ReadingNowFragment())
                }
                R.id.books -> {
                    binding.toolbarMain.title = "Books and Documents"
                    fragmentTransition(BooksFragment())
                }

                R.id.menu_haveread -> {
                    binding.toolbarMain.title = "Done Reading"
                    fragmentTransition(DoneReadingFragment())
                }
                R.id.menu_settings -> {
                    binding.toolbarMain.title = "Settings"
                    fragmentTransition(SettingsFragment())
                }
                R.id.menu_feedback -> {
                    binding.toolbarMain.title = "Feedback"
                    fragmentTransition(FeedbackFragment())
                }
                R.id.menu_use -> {
                    binding.toolbarMain.title = "Use"
                    fragmentTransition(FeedbackFragment())
                }
                R.id.home_menu -> {
                    binding.toolbarMain.title = "Home"
                    fragmentTransition(HomeFragment())
                }

                R.id.menu_Downloads -> {
                    binding.toolbarMain.title = "Downlaods"
                    fragmentTransition(DownloadsFragment())
                }

                R.id.menu_fav -> {
                    binding.toolbarMain.title = "Favorites"
                    fragmentTransition(FavoritesFragment())
                }
            }
            true
        }
    }

    private fun fragmentTransition(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_main, fragment)
            commit()
        }
        if (binding.drawerlayout.isDrawerOpen(GravityCompat.START))
            binding.drawerlayout.closeDrawer(GravityCompat.START)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun init() {

        val window: Window = this.window

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.my_statusbar_color)
        }
        mAuth = FirebaseAuth.getInstance()
        component = DaggerFactoryComponent.builder()
            .repositoryModule(RepositoryModule(this))
            .factoryModule(FactoryModule(MainRepository(this)))
            .build() as DaggerFactoryComponent
        viewModel = ViewModelProviders.of(this, component.getFactory())
            .get(MainViewModel::class.java)

        currentuser = mAuth.currentUser

        checkUser()
        tts = TextToSpeech(this, this)

        toggle =
            ActionBarDrawerToggle(this, binding.drawerlayout, binding.toolbarMain, R.string.open, R.string.close)
        binding.drawerlayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(binding.toolbarMain
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        fragmentTransition(HomeFragment())
    }

    fun checkPermissions(permission: String, name: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(
                        ContentValues.TAG,
                        "checkPermissions: $name permission Granted"
                    )
                }
                shouldShowRequestPermissionRationale(permission) -> showDialog(
                    permission,
                    name,
                    requestCode
                )

                else -> ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    requestCode
                )
            }
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage("Permission to Access Your Pdf and Readable Documents")
            setTitle("Permission Required")
            setPositiveButton("Ok") { dialog, which ->
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission),
                    requestCode
                )
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fun innerCheck(name: String) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "$name Permission Not Granted", Toast.LENGTH_SHORT)
//                    .show()
            } else {
//                Toast.makeText(this, "$name Permission Granted", Toast.LENGTH_SHORT)
//                    .show()
            }
        }
        when (requestCode) {
            100 -> innerCheck("Storage")
        }

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                Toast.makeText(this, "Language is not Supported", Toast.LENGTH_SHORT).show()
            } else if (result == TextToSpeech.LANG_AVAILABLE) {
                Log.d(TAG, "onInit: Initialised")
            }
        } else {
            Log.d(TAG, "onInit: Initialisation Failed")
        }
    }

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)

    }

    private fun checkUser() {
        mAuth = FirebaseAuth.getInstance()
        currentuser = mAuth.currentUser

        if (currentuser == null) {
            sendUserToHomeActivity()
        }

    }


    private fun sendUserToHomeActivity() {
        Intent(this, HomeAuth::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)

        return true
    }

}