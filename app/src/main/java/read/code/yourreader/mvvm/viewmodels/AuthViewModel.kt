package read.code.yourreader.mvvm.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

import read.code.yourreader.mvvm.repository.AuthRepository

class AuthViewModel(
    val repository: AuthRepository
) : ViewModel() {

    val email: MutableLiveData<String> = MutableLiveData()


    val password:MutableLiveData<String> = MutableLiveData()

    fun login() = CoroutineScope(IO).launch {
        repository.login(email.value.toString(),password.value.toString())
    }

    fun register() = CoroutineScope(IO).launch {
        repository.register(email.value.toString(),password.value.toString())
    }
    fun forgotPassword(email:String)
    {
        repository.forgotPassword(email)
    }


}