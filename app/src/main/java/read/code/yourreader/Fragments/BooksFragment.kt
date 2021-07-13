package read.code.yourreader.Fragments

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import read.code.yourreader.R
import java.io.File


class BooksFragment : Fragment() {
    val fileList: ArrayList<File> = ArrayList()
    val dir = File(Environment.getExternalStorageDirectory().absolutePath)
    var pdfs: ArrayList<String> = ArrayList()
    override fun onCreateView( //the fragment is initialized and bound to the nav host activity.
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        GlobalScope.launch {
            Log.d("This", "onCreateView: HERE: ${Search_Dir(dir)}")
            Log.d("TAG", "onCreateView:FINAL $pdfs ")
        }

        return inflater.inflate(R.layout.fragment_books, container, false)
    }

    private fun Search_Dir(dir: File) {
        Log.d("TAG", "Search_Dir:Starting of func $dir")
        val pdfPattern = ".pdf"
        val FileList = dir.listFiles()

        if (FileList != null) {
            Log.d("TAG", "Search_Dir: THis is not null $FileList")
            for (i in FileList.indices) {
                if (FileList[i].isDirectory) {
                    Log.d("TAG", "Search_Dir:This is a directory: ${FileList[i]}")
                    Search_Dir(FileList[i])
                } else {
                    if (FileList[i].name.endsWith(pdfPattern) || FileList[i].name.endsWith(".doc")) {
                        //here you have that file.
                        Log.d("TAG", "Search_Dir:Got it ${FileList[i]}")
                        pdfs.add(FileList[i].toString())
                    }
                }
            }
        }
        Log.d("TAG", "Search_Dir: End of func $FileList")
    }

}
