package read.code.yourreader.Fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.shockwave.pdfium.PdfDocument
import kotlinx.android.synthetic.main.fragment_home.*
import read.code.yourreader.R
import read.code.yourreader.databinding.FragmentHomeBinding
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class HomeFragment : Fragment() , OnPageChangeListener, OnLoadCompleteListener, OnErrorListener
    {



     lateinit var inputStream : InputStream
    lateinit var binding: FragmentHomeBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        val intent = requireActivity().intent
        if (intent != null) {
            val action = intent.action
            val type = intent.type
            if (Intent.ACTION_SEND == action && type != null) {
                if (type.equals("text/plain", ignoreCase = true)) {
                    handleTextData(intent)
                }
                else if (type.equals("application/pdf", ignoreCase = true)) {
                    handlePdfFile(intent)
                }
            }
        }

        return binding.root
    }

    private fun handlePdfFile(intent: Intent) {
        val pdffile: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
        if (pdffile != null) {
            Log.d("Pdf File Path : ", "" + pdffile.path)
            extractTextFromPdfFile(pdffile)
            displayFromUri(pdffile)
            Log.d(TAG, "handlePdfFile: Pdf Loaded")


        }
    }

    private fun handleTextData(intent: Intent) {
        val textdata = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (textdata != null) {
            Log.d("Text Data : ", "" + textdata)
        }
    }


    private fun extractTextFromPdfFile(uri:Uri){
        try{

             inputStream= requireContext().contentResolver.openInputStream(uri)!!

        }
        catch (e:FileNotFoundException){
            Toast.makeText(requireContext(), "File Not Found", Toast.LENGTH_SHORT).show()
        }
        var fileContent=""
        var builder=StringBuilder()
        var reader: PdfReader? =null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                reader= PdfReader(inputStream)
                var pages=reader.numberOfPages
                for (i in 1..pages)
                {
                  fileContent=PdfTextExtractor.getTextFromPage(reader,i)

                }
                builder.append(fileContent)
            }
            reader?.close()
//            CoroutineScope(IO).launch {
//                //Add TTs Here
//            }
        }
        catch (e:IOException){
            Log.d(TAG, "extractTextFromPdfFile: ${e.message}")
        }

    }

    private fun displayFromUri(uri: Uri) {
            binding.pdfViewHome.fromUri(uri)
                .password(null)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAnnotationRendering(false)
                .load()
    }

        override fun onPageChanged(page: Int, pageCount: Int) {
            Log.d(TAG, "onPageChanged: Page Changed")
        }

        override fun loadComplete(nbPages: Int) {
            val meta: PdfDocument.Meta = binding.pdfViewHome.documentMeta
            Log.e(TAG, "title = " + meta.title)
            Log.e(TAG, "author = " + meta.author)
            Log.e(TAG, "subject = " + meta.subject)
            Log.e(TAG, "keywords = " + meta.keywords)
            Log.e(TAG, "creator = " + meta.creator)
            Log.e(TAG, "producer = " + meta.producer)
            Log.e(TAG, "creationDate = " + meta.creationDate)
            Log.e(TAG, "modDate = " + meta.modDate)
        }



        override fun onError(t: Throwable?) {
            Log.e(TAG, "Cannot load page ")
        }


    }