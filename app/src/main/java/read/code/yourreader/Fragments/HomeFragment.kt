package read.code.yourreader.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import read.code.yourreader.R
import java.io.InputStream


class HomeFragment : Fragment() {


    lateinit var inputStream: InputStream


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        val intent = requireActivity().intent
        if (intent != null) {
            val action = intent.action
            val type = intent.type
            if (Intent.ACTION_SEND == action && type != null) {
                if (type.equals("text/plain", ignoreCase = true)) {
                    handleTextData(intent)
                } else if (type.equals("application/pdf", ignoreCase = true)) {
                    handlePdfFile(intent)
                }
            }
        }

        return view
    }

    private fun handlePdfFile(intent: Intent) {
        val pdffile: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
        if (pdffile != null) {
            Log.d("Pdf File Path : ", "" + pdffile.path)
            extractTextFromPdfFile(pdffile)


        }
    }

    private fun handleTextData(intent: Intent) {
        val textdata = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (textdata != null) {
            Log.d("Text Data : ", "" + textdata)


        }
    }


    private fun extractTextFromPdfFile(uri: Uri) {
        try {

            inputStream = requireContext().contentResolver.openInputStream(uri)!!

        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "File Not Found", Toast.LENGTH_SHORT).show()
        }
        var fileContent = ""
        var builder = StringBuilder()
        var reader: PdfReader? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                reader = PdfReader(inputStream)
                var pages = reader.numberOfPages
                for (i in 1..pages) {
                    fileContent = PdfTextExtractor.getTextFromPage(reader, i)

                }
                builder.append(fileContent)
            }
            reader?.close()
            CoroutineScope(IO).launch {
                text_home.text = builder.toString()
            }
        } catch (e: IOException) {

        }

    }

}