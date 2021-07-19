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
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import read.code.yourreader.R
import read.code.yourreader.databinding.FragmentHomeBinding
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.time.temporal.TemporalAdjusters.next
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), OnPageChangeListener, OnLoadCompleteListener, OnErrorListener,
    TextToSpeech.OnInitListener {


    lateinit var inputStream: InputStream
    lateinit var binding: FragmentHomeBinding
    var tts: TextToSpeech? = null
    var builderArray = ArrayList<String>()
    var playEnabled=false
    var i = 0
    var pdf:PDFView?=null
    var str:String=""
    var pages = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        InitialiseTTS()
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

            if (Intent.ACTION_VIEW == action && type != null) {
                if (type.equals("text/plain", ignoreCase = true)) {
                    handleTextData(intent)
                } else if (type.equals("application/pdf", ignoreCase = true)) {
                    handlePdfFile(intent)
                }
            }
        }


        binding.btnBack.setOnClickListener {
            if (i!=0){
                i--
                pagesReader()
            }
            else{
                Toast.makeText(requireContext(), "First page", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnFront.setOnClickListener {
            if (i<pages){
                i++
                pagesReader()
            }
            else{
                Toast.makeText(requireContext(), "Pages Ended", Toast.LENGTH_SHORT).show()
            }


        }
//
//        binding.openFileHome.setOnClickListener {
//            binding.pbar.visibility != binding.pbar.visibility
//        }

        binding.btnPaly.setOnClickListener {
            if (!playEnabled)
            {
                binding.btnPaly.setImageResource(R.drawable.ic_pause)
                pagesReader()
                playEnabled=true
            }
            else{
                binding.btnPaly.setImageResource(R.drawable.ic_play)
                tts!!.stop()
             playEnabled=false
            }
        }

        binding.seekBarSpeed.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var speechRate: Float = 0.1F
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                speechRate = ((progress + 1.0) / 10).toFloat()
                speechRate = binding.seekBarSpeed.progress.toFloat() / 50
                Log.d(TAG, "onProgressChanged: $speechRate")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                tts!!.setSpeechRate(speechRate)
                pagesReader()

            }
        })

        binding.seekBarPitch.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var pitch: Float = 0.1F
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                pitch = (progress.toFloat() + 1) / 100
                if (pitch < 2.0)
                    Log.d(TAG, "onProgressChanged: $pitch")


            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                tts!!.setPitch(pitch)
                pagesReader()

            }
        })

        return binding.root
    }

    private fun handlePdfFile(intent: Intent) {
        InitialiseTTS()

        val pdffile: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
        if (pdffile != null) {
            Log.d("Pdf File Path : ", "" + pdffile.path)
            extractTextFromPdfFile(pdffile)
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


    private fun extractTextFromPdfFile(uri: Uri) {
        try {
            inputStream = requireContext().contentResolver.openInputStream(uri)!!

        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "File Not Found", Toast.LENGTH_SHORT).show()
        }
        var fileContent = ""
        val reader: PdfReader?
        try {
            reader = PdfReader(inputStream)
            pages = reader.numberOfPages
            for (i in 1..pages) {
                fileContent =
                    PdfTextExtractor.getTextFromPage(reader, i, LocationTextExtractionStrategy())
                builderArray.add(fileContent)
            }
            reader.close()
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
        val meta: com.shockwave.pdfium.PdfDocument.Meta = binding.pdfViewHome.documentMeta
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
        binding.controler.visibility = View.VISIBLE

    }

    private fun UnLoadPdfLayout() {
        binding.layNoFile.visibility = View.VISIBLE
        binding.pdfViewHome.visibility = View.GONE
        binding.layPitch.visibility = View.GONE
        binding.laySpeed.visibility = View.GONE
        binding.seekBarPitch.visibility = View.GONE
        binding.seekBarSpeed.visibility = View.GONE
        binding.laySpeed.visibility = View.GONE
        binding.controler.visibility = View.GONE


    }

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun InitialiseTTS() {
        tts = TextToSpeech(context, this, "com.google.android.tts")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.ENGLISH)
            if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(requireContext(), "Language Not Supported", Toast.LENGTH_SHORT)
                    .show()

            }
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                startActivity(installIntent)
            }

        }
    }


    fun pagesReader() {

        if(i<=pages)
        {
            str = builderArray[i]
            speakOut(str)
            Log.d(TAG, "onCreateView: i=$i")
            Log.d(TAG, "onCreateView: $str")
            binding.pdfViewHome.jumpTo(i)

        }



    }
}