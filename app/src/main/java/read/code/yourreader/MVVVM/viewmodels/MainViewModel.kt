package read.code.yourreader.mvvm.viewmodels


import androidx.lifecycle.ViewModel
import read.code.yourreader.mvvm.repository.MainRepository
import java.io.File

class MainViewModel constructor(var repository: MainRepository) : ViewModel() {

    fun signOut() {
        repository.signOut()
    }

    fun fetchPdf(dir: File) {
        repository.searchPdf(dir)
    }
}