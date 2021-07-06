package read.code.yourreader.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import read.code.yourreader.R
import read.code.yourreader.di.components.DaggerFactoryComponent
import read.code.yourreader.di.modules.FactoryModule
import read.code.yourreader.di.modules.RepositoryModule
import read.code.yourreader.mvvm.repository.AuthRepository
import read.code.yourreader.mvvm.viewmodels.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    private val TAG = "LoginActivity"
    private lateinit var mAuth: FirebaseAuth
    private var currentuser: FirebaseUser? = null
    private var verifiedboolean = false
    private lateinit var component: DaggerFactoryComponent
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
//Branch
        home_no_account_log.setOnClickListener {
            sendToRegisterActivity()
        }

        viewModel.shit()
        btn_login_lg.setOnClickListener {
            val email=log_email_edit.text.toString()
            val pass=log_pass_edit.text.toString()
            viewModel.login(email,pass)
        }

        go_back_login.setOnClickListener {
            sendToHomeActivity()
        }





    }

    private fun sendToHomeActivity() {
        Intent(this, HomeAuth::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }
    private fun sendToRegisterActivity() {
        Intent(this, RegisterActivity::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun init(){
        val window: Window = this.window

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.my_statusbar_color)
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        mAuth = FirebaseAuth.getInstance()
        component = DaggerFactoryComponent.builder()
            .repositoryModule(RepositoryModule(this))
            .factoryModule(FactoryModule(AuthRepository(this)))
            .build() as DaggerFactoryComponent


        viewModel =
            ViewModelProviders.of(this, component.getFactory())
                .get(AuthViewModel::class.java)
    }







    override fun onStart() {
        super.onStart()
        mAuth = FirebaseAuth.getInstance()
        currentuser = mAuth.currentUser
        if (currentuser != null) {
            verifiedboolean = currentuser!!.isEmailVerified
            if (verifiedboolean) {
                viewModel.sendUserToMainActivity()
            }
        } else {
            Log.d(TAG, "onStart:Not Verified ")
        }
    }





    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}