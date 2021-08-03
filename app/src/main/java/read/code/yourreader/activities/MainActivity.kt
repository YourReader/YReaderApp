package read.code.yourreader.activities


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import read.code.yourreader.Fragments.*
import read.code.yourreader.MVVVM.viewmodels.FilesViewModel
import read.code.yourreader.R
import read.code.yourreader.databinding.ActivityMainBinding
import read.code.yourreader.di.components.DaggerFactoryComponent
import read.code.yourreader.di.modules.FactoryModule
import read.code.yourreader.di.modules.RepositoryModule
import read.code.yourreader.mvvm.repository.MainRepository
import read.code.yourreader.mvvm.viewmodels.MainViewModel
import read.code.yourreader.others.Values


class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: MainViewModel
    private lateinit var component: DaggerFactoryComponent
    private val TAG = "mActivity"
    private var currentuser: FirebaseUser? = null
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding
    private lateinit var mFilesViewModel: FilesViewModel
    var permissionMain=false


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //AccessFiles  Settings
        val sharedPreferencesAccess : SharedPreferences =
            getSharedPreferences("switchAccess", MODE_PRIVATE)
        val editor4 = sharedPreferencesAccess.edit()
        init()
        val sharedPreferences: SharedPreferences = getSharedPreferences("switchDark", MODE_PRIVATE)
        val theme = sharedPreferences.getBoolean("switchDark", false)

        if (theme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            resources.configuration.uiMode = Configuration.UI_MODE_NIGHT_YES
            setTheme(R.style.Theme_AppTheme_Dark)

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            resources.configuration.uiMode = Configuration.UI_MODE_NIGHT_NO
            setTheme(R.style.Theme_AppTheme)

        }
        binding.toolbarMain.showOverflowMenu()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (permissionMain)
        {
            editor4.putBoolean("switchAccess", true)
            editor4.apply()
        }
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
                    // TODO: 7/15/2021

                }

                R.id.menu_use -> {
                    binding.toolbarMain.title = "Use"
                    val sharedPreferencesInfo: SharedPreferences =
                        this.getSharedPreferences("Info", Context.MODE_PRIVATE)
                    val editor = sharedPreferencesInfo.edit()
                    editor.putBoolean("Info", false)
                    editor.apply()
                    fragmentTransition(HomeFragment())
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
                R.id.menu_Trash -> {
                    binding.toolbarMain.title = "Trash"
                    fragmentTransition(TrashFragment())
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

    //Checks if DB is empty or not
    private fun checkDb() {
        mFilesViewModel.readAllData.observe(this@MainActivity, {
            Values.isDbEmpty = it.isNullOrEmpty()
            Log.d(TAG, " MT: ${Values.isDbEmpty} Null: ${it.isNullOrEmpty()} Size ${it.size}")
            Log.d(TAG, "checkDb: Values: $it")
        })
    }

    private fun init() {

        val window: Window = this.window

        mFilesViewModel = ViewModelProvider(this).get(FilesViewModel::class.java)
        checkDb()

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this, R.color.my_statusbar_color)
        mAuth = FirebaseAuth.getInstance()
        component = DaggerFactoryComponent.builder()
            .repositoryModule(RepositoryModule(this))
            .factoryModule(FactoryModule(MainRepository(this)))
            .build() as DaggerFactoryComponent
        viewModel = ViewModelProviders.of(this, component.getFactory())
            .get(MainViewModel::class.java)

        currentuser = mAuth.currentUser

        checkUser()

        toggle =
            ActionBarDrawerToggle(
                this,
                binding.drawerlayout,
                binding.toolbarMain,
                R.string.open,
                R.string.close
            )
        binding.drawerlayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setSupportActionBar(
            binding.toolbarMain
        )
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        fragmentTransition(HomeFragment())
        binding.toolbarMain.title = "Home"



    }

    private fun checkManagePermission(
        permission: String,
        name: String,
        requestCode: Int,
        normal: Boolean
    ) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            when {
                Environment.isExternalStorageManager() ->{
                    Log.d(TAG, "checkPermissions: $name permission Granted")
                    permissionMain=true
                }

                shouldShowRequestPermissionRationale(permission) -> {
                    Log.d(TAG, "checkPermissions: IN THAT WEIRD SCOPE")
                    showManagePermissionDialog(normal, name)
                }
                else -> {
                    Log.d(TAG, "checkPermissions: IN ELSE $permission")
                    showManagePermissionDialog(normal, name)
                }
            }
        } else {
            showManagePermissionDialog(true, name)
        }
    }

    @SuppressLint("InlinedApi")
    private fun showManagePermissionDialog(normal: Boolean = true, name: String) {
        Log.d(TAG, "showManagePermissionDialog: Name: $name  Normal: $normal")
        if (!normal) {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage("To open Books , PDF's and documents the application needs permissions")
                setTitle("Permission Required")
                setPositiveButton("Grant") { _, _ ->

                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data =
                            Uri.parse(String.format("package:%s", applicationContext.packageName))
                        startActivityForResult(intent, 2296)
                    } catch (e: Exception) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        startActivityForResult(intent, 2296)
                    }

                }
            }
            val dialog = builder.create()
            dialog.show()
        } else {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(name), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty())
            for (i in grantResults.indices)
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: GRANTED $requestCode")

                }
                else{
                    showManagePermissionDialog(true, Manifest.permission.READ_EXTERNAL_STORAGE)}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                } else if (SDK_INT >= Build.VERSION_CODES.R) {
                    checkManagePermission(
                        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        "MANAGE",
                        101,
                        false
                    )

                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {


            else -> {
                if (binding.drawerlayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerlayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerlayout.openDrawer(GravityCompat.START)
                }
            }

        }
        return true

    }

    private fun checkUser() {
        mAuth = FirebaseAuth.getInstance()
        currentuser = mAuth.currentUser
        if (currentuser == null) {
            sendUserToHomeActivity()
        } else {
            handlePermissions()
        }

    }

    private fun handlePermissions() {
        Log.d(TAG, "handlePermissions: $SDK_INT")
        if (SDK_INT >= Build.VERSION_CODES.R) {
            checkManagePermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE, "MANAGE", 101, false)
        } else {
            checkStoragePermission(Manifest.permission.READ_EXTERNAL_STORAGE, "STORAGE", 100, true)
        }
    }

    private fun checkStoragePermission(
        permission: String,
        name: String,
        requestCode: Int,
        normal: Boolean
    ) {
        Log.d(TAG, "checkStoragePermission: NAME :$name ")
        when {
            ActivityCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_GRANTED ->
                Log.d(TAG, "checkPermissions: $name permission Granted")

            shouldShowRequestPermissionRationale(permission) -> {
                Log.d(TAG, "checkStoragePermission: In that weird scope")
                showManagePermissionDialog(normal, name)
            }
            else -> {
                Log.d(TAG, "checkPermissions: IN ELSE $permission")
                showManagePermissionDialog(normal, name)
            }
        }
    }

    private fun sendUserToHomeActivity() {
        Intent(this, HomeAuth::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }



    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }
}