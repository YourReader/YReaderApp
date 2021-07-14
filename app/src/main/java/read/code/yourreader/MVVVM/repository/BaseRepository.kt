package read.code.yourreader.mvvm.repository

import android.content.Context
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import read.code.yourreader.activities.HomeAuth
import read.code.yourreader.activities.MainActivity


abstract class BaseRepository(private var contextBase: Context) {
    private var mAuthBase = FirebaseAuth.getInstance()
    var curUser=mAuthBase.currentUser


    fun signOut() {
        mAuthBase = FirebaseAuth.getInstance()

        mAuthBase.signOut()
        Intent(contextBase, HomeAuth::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            contextBase.startActivity(it)

        }

    }

    fun sendUserToMainActivity() {
        Intent(contextBase, MainActivity::class.java).also {
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            contextBase.startActivity(it)
        }
    }

}