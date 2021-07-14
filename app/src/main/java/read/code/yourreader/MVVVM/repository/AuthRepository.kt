package read.code.yourreader.MVVVM.repository

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import read.code.yourreader.Auth.LoginActivity
import read.code.yourreader.activities.MainActivity
import read.code.yourreader.mvvm.repository.BaseRepository


class AuthRepository(var context: Context) : BaseRepository(context) {
    private var mAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (mAuth.currentUser!!.isEmailVerified) {
                            Toast.makeText(context, "Signed In as $email", Toast.LENGTH_SHORT)
                                .show()
                            Intent(context, MainActivity::class.java).also {
                                it.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                context.startActivity(it)
                            }
                        } else {
                            Toast.makeText(context, "First Verify Your Email", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Log.d(TAG, "login: Login Failed :- ${task.exception}")
                    }
                }
        } else {
            Toast.makeText(context, "Fill The Fields", Toast.LENGTH_SHORT).show()
        }

    }

    fun forgotPassword(email: String) {
        if (email.isNotEmpty()) {
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener {

            }
        }
    }

    fun register(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener {
                            Toast.makeText(
                                context,
                                "Check your Email For Verification",
                                Toast.LENGTH_SHORT
                            ).show()
                            Intent(context, LoginActivity::class.java).also {
                                context.startActivity(it)
                            }
                        }

                    } else {
                        Toast.makeText(
                            context,
                            "Something went Wrong Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, "login: Login Failed :- ${task.exception}")
                    }
                }
        } else {
            Toast.makeText(context, "Fill The Fields", Toast.LENGTH_SHORT).show()
        }

    }
}

