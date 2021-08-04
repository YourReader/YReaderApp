package read.code.yourreader.mvvm.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class MainRepository(var context: Context) : BaseRepository(context) {
    private var pdfs = ArrayList<File>()

    fun searchPdf(dir: File): ArrayList<File> {
        val pdfPattern = ".pdf"
        val FileList = dir.listFiles()
        if (FileList != null) {
            for (i in FileList.indices) {
                if (FileList[i].isDirectory) {
                    searchPdf(FileList[i])
                } else {
                    if (FileList[i].name.endsWith(pdfPattern)) {
                        pdfs.add(FileList[i])
                    }
                }
            }
        }
        return pdfs
    }

    fun extractTextFromPdfFile(toUri: Uri): java.util.ArrayList<String> {
        var pages = 0
        lateinit var inputStream: InputStream

        var builderArray = ArrayList<String>()
        var fileContent: String
        val reader: PdfReader?
        try {
            inputStream = context.contentResolver.openInputStream(toUri)!!
            reader = PdfReader(inputStream)
            pages = reader.numberOfPages
            for (i in 1..pages) {
                fileContent =
                    PdfTextExtractor.getTextFromPage(
                        reader,
                        i,
                        LocationTextExtractionStrategy()
                    )
                builderArray.add(fileContent)
            }
            reader.close()


        } catch (e: IOException) {
            Log.d(ContentValues.TAG, "extractTextFromPdfFile: ${e.message}")
        } catch (e: FileNotFoundException) {
            Toast.makeText(context, "File Not Found", Toast.LENGTH_SHORT).show()
        }

        return builderArray
    }
}