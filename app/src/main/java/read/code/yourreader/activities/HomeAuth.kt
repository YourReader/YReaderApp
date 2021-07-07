package read.code.yourreader.activities

import android.content.Intent
import android.content.res.Configuration
import android.media.VolumeShaper
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_home_auth.*
import read.code.yourreader.R
import read.code.yourreader.di.components.DaggerFactoryComponent
import read.code.yourreader.di.modules.FactoryModule
import read.code.yourreader.di.modules.RepositoryModule
import read.code.yourreader.mvvm.repository.AuthRepository
import read.code.yourreader.mvvm.viewmodels.AuthViewModel
import read.code.yourreader.others.Constants


class HomeAuth : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var mAuth: FirebaseAuth
    private var currentuser: FirebaseUser? = null
    private var verifiedboolean = false
    private val RC_SIGN_IN = 120
    private lateinit var viewModel: AuthViewModel
    private lateinit var component: DaggerFactoryComponent
    private lateinit var googleSignInClient: GoogleSignInClient

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_auth)

        init()

        home_no_account.setOnClickListener {
            sendUserToRegisterActivity()
        }
        home_mail_btn.setOnClickListener {
            sendUserToLoginActivity()
        }

        G_home_auth.setOnClickListener {
            signIn()
        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun init() {
        val window: Window = this.window

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.my_statusbar_color))
        }
        mAuth = FirebaseAuth.getInstance()
        checkUser()


        component = DaggerFactoryComponent.builder()
            .repositoryModule(RepositoryModule(this))
            .factoryModule(FactoryModule(AuthRepository(this)))
            .build() as DaggerFactoryComponent


        viewModel =
            ViewModelProviders.of(this, component.getFactory()).get(AuthViewModel::class.java)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


    }

    override fun onStart() {
        super.onStart()
        mAuth = FirebaseAuth.getInstance()
        currentuser = mAuth.currentUser
        if (currentuser != null) {
            verifiedboolean = currentuser!!.isEmailVerified
            if (verifiedboolean) {
                sendUserToMainActivity();
            }
        } else {
            Log.d(TAG, "onStart:Not Verified ")
        }
    }


    private fun sendUserToMainActivity() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    private fun sendUserToLoginActivity() {
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun sendUserToRegisterActivity() {
        Intent(this, RegisterActivity::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun checkUser() {
        mAuth = FirebaseAuth.getInstance()
        currentuser = mAuth.currentUser

        if (currentuser != null) {
            sendUserToMainActivity()
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(Constants.USERS)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
                    if (user != null) {
                        myRef.child(user.uid).child(Constants.USER_EMAIL).setValue(user.email)

                    }
                    Intent(this, MainActivity::class.java).also {
                        startActivity(it)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish()
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }


    override fun onResume() {
        super.onResume()
        var darkflag=resources.configuration.uiMode
        if (darkflag== Configuration.UI_MODE_NIGHT_YES)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                home_auth_ui.isForceDarkAllowed = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                home_auth_ui.isForceDarkAllowed = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            }
        }

    }
}