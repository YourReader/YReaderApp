package read.code.yourreader.Room

import kotlinx.coroutines.flow.Flow
import read.code.yourreader.data.Files

class FilesRepository(private val filesDao: FileDao) {
    val readAllData: Flow<List<Files>> = filesDao.getFiles()
}