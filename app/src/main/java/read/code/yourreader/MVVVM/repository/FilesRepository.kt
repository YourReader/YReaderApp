package read.code.yourreader.MVVVM.repository

import read.code.yourreader.Room.FileDao
import read.code.yourreader.data.Files

class FilesRepository(private val filesDao: FileDao) {

    fun getAllFiles() = filesDao.getFiles()

    fun getAllFavoriteFiles() = filesDao.getFiles(true)

    fun getAllDoneFiles() = filesDao.getDoneFiles()

    fun getAllCurrentFiles() = filesDao.getCurrentReadingFiles()

    suspend fun deleteTheDatabase() {
        filesDao.deleteDatabase()
    }

    suspend fun addFile(file: Files) {
        filesDao.insert(file)
    }

    suspend fun updateFile(file: Files) {
        filesDao.update(file)
    }

    fun getAllTrashFiles() = filesDao.getTrashFiles()


}