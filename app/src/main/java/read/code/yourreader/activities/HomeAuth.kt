package read.code.yourreader.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_home_auth.*
import read.code.yourreader.R


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

        home_no_account.setOnClickListener {
            sendUserToRegisterActivity()
        }
        home_mail_btn.setOnClickListener {
            sendUserToLoginActivity()
        }
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
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            finish()
        }
    }

    private fun sendUserToLoginActivity() {
        Intent(this, LoginActivity::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
    }
    private fun sendUserToRegisterActivity() {
        Intent(this, RegisterActivity::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
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
}