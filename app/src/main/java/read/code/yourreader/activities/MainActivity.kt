package read.code.yourreader.activities


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
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


class MainActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: MainViewModel
    private lateinit var component: DaggerFactoryComponent
    private val TAG = "mActivity"
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

        if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

        if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        fragmentTransition(HomeFragment())
        binding.toolbarMain.title = "Home"


    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkPermissions(permission: String, name: String, requestCode: Int) {
        Log.d(TAG, "checkPermissions: PERMISSION ASKED $name")
        if (SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "checkPermissions:  IS => M")
            when {
                Environment.isExternalStorageManager() ->
                    Log.d(TAG, "checkPermissions: $name permission Granted")

                shouldShowRequestPermissionRationale(permission) -> {
                    Log.d(TAG, "checkPermissions: IN THAT WEIRD SCOPE")
                    showManagePermissionDialog()
                }
                else -> {
                    Log.d(TAG, "checkPermissions: IN ELSE $permission")
                    showManagePermissionDialog()
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showManagePermissionDialog() {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                } else if (SDK_INT >= Build.VERSION_CODES.R) {
                    checkPermissions(
                        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        "MANAGE",
                        101
                    )

                }
            }
        }
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
        } else if (SDK_INT >= Build.VERSION_CODES.R) {
            checkPermissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE, "MANAGE", 101)
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