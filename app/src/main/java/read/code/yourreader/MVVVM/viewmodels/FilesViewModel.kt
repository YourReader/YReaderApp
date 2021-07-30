package read.code.yourreader.MVVVM.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import read.code.yourreader.MVVVM.repository.FilesRepository
import read.code.yourreader.Room.FileDatabase
import read.code.yourreader.data.Files

class FilesViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<Files>>
    val readFavData: LiveData<List<Files>>
    private val repository: FilesRepository

    init {
        val filesDao = FileDatabase.getDatabase(application).filesDao()
        repository = FilesRepository(filesDao)
        readAllData = repository.getAllFiles().asLiveData()
        readFavData = repository.getAllFavoriteFiles().asLiveData()
    }

    fun addFile(files: Files) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFile(files)
            Log.d("mActivity", "Adding data")
        }
    }

    fun onMarkedFavorite(file: Files, isFavorite: Boolean) = viewModelScope.launch {
        repository.updateFile(file.copy(favorites = isFavorite))
        Log.d("Favorite", "onMarkedFavorite: ${file.copy(favorites = isFavorite)}")
    }
}