package read.code.yourreader.MVVVM.repository

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import read.code.yourreader.Room.FileDao
import read.code.yourreader.data.Files
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

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




}