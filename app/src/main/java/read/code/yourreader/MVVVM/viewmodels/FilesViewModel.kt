package read.code.yourreader.MVVVM.viewmodels

import android.app.Application
import android.net.Uri
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
import java.util.ArrayList

class FilesViewModel(application: Application) : AndroidViewModel(application) {

    val readCurrentReadData: LiveData<List<Files>>
    val readAllData: LiveData<List<Files>>
    val readFavData: LiveData<List<Files>>
    val readDoneData: LiveData<List<Files>>
    private val repository: FilesRepository

    init {
        val filesDao = FileDatabase.getDatabase(application).filesDao()
        repository = FilesRepository(filesDao)
        readAllData = repository.getAllFiles().asLiveData()
        readFavData = repository.getAllFavoriteFiles().asLiveData()
        readDoneData = repository.getAllDoneFiles().asLiveData()
        readCurrentReadData = repository.getAllCurrentFiles().asLiveData()
    }

    fun nukeDatabase() = viewModelScope.launch {
        repository.deleteTheDatabase()
    }

    fun addFile(files: Files) = viewModelScope.launch(Dispatchers.IO) {
        repository.addFile(files)
        Log.d("mActivity", "Adding data")

    }

    fun updateFavoriteStatus(file: Files, isFavorite: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFile(file.copy(favorites = isFavorite))
        }

    fun updateDoneStatus(file: Files, isDone: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFile(file.copy(doneReading = isDone, readingNow = false))
        }
    }

    fun updateReadingStatus(files: Files, isReading: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFile(files.copy(readingNow = isReading, doneReading = false))

        }
    }



}