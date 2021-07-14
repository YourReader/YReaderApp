package read.code.yourreader.Fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_books.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import read.code.yourreader.databinding.FragmentBooksBinding
import java.io.File


class BooksFragment : Fragment() {
    var dir = File(Environment.getExternalStorageDirectory().absolutePath)
    private var pdfs = ArrayList<File>()
    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView( //the fragment is initialized and bound to the nav host activity.
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch {
            Search_Dir(dir)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val fd = ParcelFileDescriptor.open(
                    pdfs[pdfs.size - 1],
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
                val renderer: PdfRenderer = PdfRenderer(fd)
                val bitmap: Bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_4444)
                val page: PdfRenderer.Page = renderer.openPage(0)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bm.setImageBitmap(bitmap)
            } else {
                TODO("VERSION.SDK_INT < LOLLIPOP")
            }
            hellotext.text = pdfs[pdfs.size - 1].toString() + " Size: " + pdfs.size
        }
    }


    private fun Search_Dir(dir: File) {
        val pdfPattern = ".pdf"
        val FileList = dir.listFiles()
        if (FileList != null) {
            for (i in FileList.indices) {
                if (FileList[i].isDirectory) {
                    Search_Dir(FileList[i])
                } else {
                    if (FileList[i].name.endsWith(pdfPattern)) {
                        pdfs.add(FileList[i])
                    }
                }
            }
        }
    }
}
