package read.code.yourreader.Auth

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import read.code.yourreader.MVVVM.repository.AuthRepository
import read.code.yourreader.R
import read.code.yourreader.databinding.ActivityForgotPasswordBinding
import read.code.yourreader.databinding.ActivityMainBinding

import read.code.yourreader.di.components.DaggerFactoryComponent
import read.code.yourreader.di.modules.FactoryModule
import read.code.yourreader.di.modules.RepositoryModule
import read.code.yourreader.mvvm.viewmodels.AuthViewModel

class ForgotPassword : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel
    private val TAG = "ForgotPassword"
    private lateinit var mAuth: FirebaseAuth
    private var currentuser: FirebaseUser? = null
    private var verifiedboolean = false
    private lateinit var component: DaggerFactoryComponent

    lateinit var binding: ActivityForgotPasswordBinding

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        init()


        binding.goBackLoginFog.setOnClickListener {
            sendToLoginActivity()
        }


        binding.btnResetPass.setOnClickListener {
            val email=binding.emailForgot.text.toString()
            if (email.isNotEmpty())
            {
                viewModel.forgotPassword(email)
                Toast.makeText(this, "Reset Link Has been Sent to $email", Toast.LENGTH_SHORT).show()
                sendToLoginActivity()
            }
            else{
                Toast.makeText(this, "Fill the Fields", Toast.LENGTH_SHORT).show()
            }
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
        mAuth = FirebaseAuth.getInstance()
        component = DaggerFactoryComponent.builder()
            .repositoryModule(RepositoryModule(this))
            .factoryModule(FactoryModule(AuthRepository(this)))
            .build() as DaggerFactoryComponent


        viewModel =
            ViewModelProviders.of(this, component.getFactory())
                .get(AuthViewModel::class.java)
    }

    private fun sendToLoginActivity() {
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }
}