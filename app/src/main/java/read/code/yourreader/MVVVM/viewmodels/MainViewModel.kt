package read.code.yourreader.mvvm.viewmodels


import androidx.lifecycle.ViewModel
import read.code.yourreader.mvvm.repository.MainRepository

class MainViewModel constructor(var repository: MainRepository) : ViewModel() {

    fun signOut() {
        repository.signOut()
    }


}