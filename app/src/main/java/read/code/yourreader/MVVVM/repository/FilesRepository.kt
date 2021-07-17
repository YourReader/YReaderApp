package read.code.yourreader.MVVVM.repository

import read.code.yourreader.Room.FileDao
import read.code.yourreader.data.Files

class FilesRepository(private val filesDao: FileDao) {

    fun getAllFiles() = filesDao.getFiles()

    suspend fun addFile(file: Files) {
        filesDao.insert(file)
    }

}