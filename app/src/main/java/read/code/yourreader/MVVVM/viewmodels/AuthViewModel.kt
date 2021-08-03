package read.code.yourreader.mvvm.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import read.code.yourreader.MVVVM.repository.AuthRepository

class AuthViewModel(
    val repository: AuthRepository
) : ViewModel() {

    fun login(email: String, password: String) = CoroutineScope(IO).launch {
        repository.login(email, password)
    }

    fun register(email: String, password: String) = CoroutineScope(IO).launch {
        repository.register(email, password)
    }

    fun forgotPassword(email: String) {
        repository.forgotPassword(email)
    }

    fun sendUserToMainActivity() {
        repository.sendUserToMainActivity()
    }


}