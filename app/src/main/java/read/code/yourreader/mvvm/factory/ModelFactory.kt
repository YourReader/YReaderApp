package read.code.yourreader.mvvm.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import read.code.yourreader.mvvm.repository.AuthRepository
import read.code.yourreader.mvvm.repository.BaseRepository
import read.code.yourreader.mvvm.repository.MainRepository
import read.code.yourreader.mvvm.viewmodels.AuthViewModel
import read.code.yourreader.mvvm.viewmodels.MainViewModel

@Suppress("UNCHECKED_CAST")
class ModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository as AuthRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository = repository as MainRepository) as T
            }


            else -> {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

}