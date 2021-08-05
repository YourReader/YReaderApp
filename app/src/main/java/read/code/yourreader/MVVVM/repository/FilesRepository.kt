package read.code.yourreader.MVVVM.repository

import read.code.yourreader.Room.FileDao
import read.code.yourreader.data.Files

class FilesRepository(private val filesDao: FileDao) {

    fun getAllFiles() = filesDao.getFiles()

    fun getAllFavoriteFiles() = filesDao.getFiles(true)

    fun getAllDoneFiles() = filesDao.getDoneFiles()

    fun getAllCurrentFiles() = filesDao.getCurrentReadingFiles()

    fun getAllTrashFiles() = filesDao.getTrashFiles()

    suspend fun deleteFile(file: Files) {
        filesDao.delete(file)
    }

    suspend fun deleteTheDatabase() {
        filesDao.deleteDatabase()
    }

    suspend fun addFile(file: Files) {
        filesDao.insert(file)
    }

    suspend fun updateFile(file: Files) {
        filesDao.update(file)
    }
}