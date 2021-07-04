package read.code.yourreader.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import read.code.yourreader.R
import read.code.yourreader.di.components.DaggerFactoryComponent
import read.code.yourreader.mvvm.viewmodels.AuthViewModel

class HomeAuth : AppCompatActivity() {
    private val TAG = "LoginActivity"
    private lateinit var mAuth: FirebaseAuth
    private var currentuser: FirebaseUser? = null
    private var verifiedboolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_auth)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        mAuth= FirebaseAuth.getInstance()
        checkUser()
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
            finish()
        }
    }

    private fun checkUser() {
        mAuth = FirebaseAuth.getInstance()
        currentuser = mAuth.currentUser

        if (currentuser != null) {
            sendUserToMainActivity()
        }

    }


}