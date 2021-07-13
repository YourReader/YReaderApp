package read.code.yourreader.Fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.shockwave.pdfium.PdfDocument
import read.code.yourreader.databinding.FragmentHomeBinding
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*


class HomeFragment : Fragment(), OnPageChangeListener, OnLoadCompleteListener, OnErrorListener,
    TextToSpeech.OnInitListener {


    lateinit var inputStream: InputStream
    lateinit var binding: FragmentHomeBinding
    lateinit var tts: TextToSpeech
    var builder = StringBuilder()


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
                } else if (type.equals("application/pdf", ignoreCase = true)) {
                    handlePdfFile(intent)
                }
            }
        }


        InitialiseTTS()
        binding.openFileHome.setOnClickListener {

        }

        binding.btnPaly.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                speakOut(builder.toString())
            }

        }


        binding.seekBarSpeed.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                var speed = binding.seekBarSpeed.progress.toFloat() / 50
                if (speed < 0.1) speed = 0.1f
                tts.setSpeechRate(speed)
                Log.d(TAG, "onProgressChanged: $speed")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.seekBarPitch.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                var pitch = binding.seekBarPitch.progress.toFloat() / 50
                if (pitch < 0.1) pitch = 0.1f

                tts.setPitch(pitch)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        return binding.root
    }

    private fun handlePdfFile(intent: Intent) {
        InitialiseTTS()

        val pdffile: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
        if (pdffile != null) {
            Log.d("Pdf File Path : ", "" + pdffile.path)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                extractTextFromPdfFile(pdffile)
            }
            displayFromUri(pdffile)
            Log.d(TAG, "handlePdfFile: Pdf Loaded")


        }
    }

    private fun handleTextData(intent: Intent) {
        InitialiseTTS()
        val textdata = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (textdata != null) {
            Log.d("Text Data : ", "" + textdata)
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun extractTextFromPdfFile(uri: Uri) {
        try {

            inputStream = requireContext().contentResolver.openInputStream(uri)!!

        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "File Not Found", Toast.LENGTH_SHORT).show()
        }
        var fileContent = ""
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
        } catch (e: IOException) {
            Log.d(TAG, "extractTextFromPdfFile: ${e.message}")
        }

    }

    private fun displayFromUri(uri: Uri) {
        loadPdfLayout()
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
        Toast.makeText(requireContext(), "Cant Load Pdf", Toast.LENGTH_SHORT).show()
        UnLoadPdfLayout()
    }


    private fun loadPdfLayout() {
        binding.layNoFile.visibility = View.GONE
        binding.pdfViewHome.visibility = View.VISIBLE
        binding.layPitch.visibility = View.VISIBLE
        binding.laySpeed.visibility = View.VISIBLE
        binding.seekBarPitch.visibility = View.VISIBLE
        binding.seekBarSpeed.visibility = View.VISIBLE
        binding.laySpeed.visibility = View.VISIBLE
        binding.btnPaly.visibility = View.VISIBLE

    }

    private fun UnLoadPdfLayout() {
        binding.layNoFile.visibility = View.VISIBLE
        binding.pdfViewHome.visibility = View.GONE
        binding.layPitch.visibility = View.GONE
        binding.laySpeed.visibility = View.GONE
        binding.seekBarPitch.visibility = View.GONE
        binding.seekBarSpeed.visibility = View.GONE
        binding.laySpeed.visibility = View.GONE
        binding.btnPaly.visibility = View.GONE

    }

    override fun onDestroy() {
        InitialiseTTS()
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speakOut(text: String) {

        var pitch = binding.seekBarPitch.progress.toFloat() / 50
        if (pitch < 0.1) pitch = 0.1f
        var speed = binding.seekBarSpeed.progress.toFloat() / 50
        if (speed < 0.1) speed = 0.1f

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")

    }

    private fun InitialiseTTS() {
        tts = TextToSpeech(context, this,"com.google.android.tts" )

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts.setLanguage(Locale.ENGLISH)
            if ( result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(requireContext(), "Language Not Supported", Toast.LENGTH_SHORT)
                    .show()

            }
            if (result == TextToSpeech.LANG_MISSING_DATA ){
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                startActivity(installIntent)
            }

        }    }
}