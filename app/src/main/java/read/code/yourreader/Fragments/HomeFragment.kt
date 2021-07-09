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


class HomeFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_home, container, false)

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

        return view
    }

    private fun handlePdfFile(intent: Intent) {
        val pdffile: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
        if (pdffile != null) {
            Log.d("Pdf File Path : ", "" + pdffile.path)
        }
    }

    private fun handleTextData(intent: Intent) {
        val textdata = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (textdata != null) {
            Log.d("Text Data : ", "" + textdata)
            //downloadFile(textdata)
        }
    }

}