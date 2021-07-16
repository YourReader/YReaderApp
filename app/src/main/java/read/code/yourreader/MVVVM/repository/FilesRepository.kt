package read.code.yourreader.MVVVM.repository

import androidx.lifecycle.LiveData
import read.code.yourreader.Room.FileDao
import read.code.yourreader.data.Files

class FilesRepository(private val filesDao: FileDao) {

    val readAllData: LiveData<List<Files>> = filesDao.getFiles()

    suspend fun addFile(file: Files) {
        filesDao.insert(file)
    }

}