package read.code.yourreader.MVVVM.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import read.code.yourreader.MVVVM.repository.FilesRepository
import read.code.yourreader.Room.FileDatabase
import read.code.yourreader.data.Files

class FilesViewModel(application: Application) : AndroidViewModel(application) {

    private val readAllData: LiveData<List<Files>>
    private val repository: FilesRepository

    init {
        val filesDao = FileDatabase.getDatabase(application).filesDao()
        repository = FilesRepository(filesDao)
        readAllData = repository.readAllData
    }

    fun addFile(files: Files) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFile(files)
        }
    }
}