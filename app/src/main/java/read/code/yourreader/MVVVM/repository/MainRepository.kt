package read.code.yourreader.mvvm.repository

import android.content.Context
import java.io.File

class MainRepository(context: Context) : BaseRepository(context) {
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
}