package read.code.yourreader.Fragments

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.util.Patterns
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
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import read.code.yourreader.R
import read.code.yourreader.activities.MainActivity
import read.code.yourreader.data.Files
import read.code.yourreader.databinding.FragmentHomeBinding
import java.io.*
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLEncoder
import java.util.*
import java.util.regex.Matcher
import kotlin.collections.ArrayList
import android.content.pm.PackageManager
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


class HomeFragment : Fragment(),
    OnPageChangeListener,
    OnLoadCompleteListener,
    OnErrorListener,
    TextToSpeech.OnInitListener {


    private lateinit var inputStream: InputStream
    lateinit var binding: FragmentHomeBinding
    var tts: TextToSpeech? = null
    private var builderArray = ArrayList<String>()
    private var playEnabled = false
    val googleTtsPackage = "com.google.android.tts"
    val picoPackage = "com.svox.pico"
    private var i = 0
    private var str: String = ""
    private val locales = Locale.getAvailableLocales()
    private val localeList: MutableList<Locale> = ArrayList()
    private var pages = 0
    private val links: MutableList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        InitialiseTTS()
        binding = FragmentHomeBinding.inflate(layoutInflater)

        (activity as MainActivity).setActionBarTitle("Home")

        val bundle = this.arguments
        Toast.makeText(
            requireContext(),
            "Found: ${bundle?.getParcelable<Files>("Object")}",
            Toast.LENGTH_SHORT
        )
            .show()

        val intent = requireActivity().intent
        if (intent != null) {
            val action = intent.action
            val type = intent.type
            Log.d(TAG, "onCreateView: Type is $type")
            if (Intent.ACTION_SEND == action && type != null) {
                when {
                    type.equals("text/plain", ignoreCase = true) -> {
                        //Text,Blogs or URl
                        readFromUrl(intent)
                    }
                    type.equals("application/pdf", ignoreCase = true) -> {
                        handlePdfFile(intent)
                    }
//                    type.equals(
//                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
//                        ignoreCase = true
//                    ) -> {
//                        //Word
//                        handleWordFile(intent)
//                    }
                }
            }

            //App Info Pref
            val sharedPreferences: SharedPreferences =
                requireActivity().getSharedPreferences("Info", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val shown = sharedPreferences.getBoolean("Info", false)
            if (shown) {
                binding.textInfo.visibility = View.GONE
                Log.d(TAG, "onCreateView: Shown")
            } else {
                binding.layNoFile.visibility = View.GONE
                binding.textInfo.visibility = View.VISIBLE
                speakOut(resources.getString(R.string.useInfo))
                editor.putBoolean("Info", true)
                editor.apply()
            }

            Log.d(TAG, "onCreateView: theme=$")


            if (Intent.ACTION_VIEW == action && type != null) {
                when {
                    type.equals("text/plain", ignoreCase = true) -> {
                        //Text,Blogs or URl
                        handleTextData(intent)
                    }
                    type.equals("application/pdf", ignoreCase = true) -> {
                        handlePdfFile(intent)
                    }
                    type.equals(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        ignoreCase = true
                    ) -> {
                        //Word
                        handleWordFile(intent)
                    }
                }
            }
        }


        binding.btnBack.setOnClickListener {
            if (i != 0) {
                i--
                pagesReader()
            } else {
                Toast.makeText(requireContext(), "First page", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnFront.setOnClickListener {
            if (i < pages) {
                i++
                pagesReader()
            } else {
                Toast.makeText(requireContext(), "Pages Ended", Toast.LENGTH_SHORT).show()
            }


        }


        binding.btnPaly.setOnClickListener {
            playEnabled = if (!playEnabled) {
                binding.btnPaly.setImageResource(R.drawable.ic_pause)
                pagesReader()
                true
            } else {
                binding.btnPaly.setImageResource(R.drawable.ic_play)
                tts!!.stop()
                false
            }
        }

        binding.openFileHome.setOnClickListener {
            val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
            fileIntent.type = "application/pdf"
            startActivityForResult(fileIntent, 21)
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

    private fun handleWordFile(intent: Intent) {
        //TODO Word Remaining
    }


    private fun handlePdfFile(intent: Intent) {
        InitialiseTTS()

        val pdfFile: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
        if (pdfFile != null) {
            Log.d("Pdf File Path : ", "" + pdfFile.path)
            extractTextFromPdfFile(pdfFile)
            displayFromUri(pdfFile)
            Log.d(TAG, "handlePdfFile: Pdf Loaded")
        }
    }

    private fun handleTextData(intent: Intent) {
        InitialiseTTS()
        val textData = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (textData != null) {
            Log.d("Text Data : ", "" + textData)
        }
    }


    private fun extractTextFromPdfFile(uri: Uri) {
        try {
            inputStream = requireContext().contentResolver.openInputStream(uri)!!

        } catch (e: FileNotFoundException) {
            Toast.makeText(requireContext(), "File Not Found", Toast.LENGTH_SHORT).show()
        }
        var fileContent: String
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


    private fun confirmDialog() {
        val d: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        d.setTitle("Install recommeded speech engine?")
        d.setMessage("Your device isn't using the recommended speech engine. Do you wish to install it?")
        d.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, arg1 ->
            val installVoice = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
            startActivity(installVoice)
        })
        d.setNegativeButton("No, later"
        ) { dialog, arg1 ->
            if (isPackageInstalled(
                    requireContext().packageManager,
                    picoPackage
                )
            ) tts!!.setEngineByPackageName(picoPackage)
        }
        d.show()
    }


    private fun isPackageInstalled(pm: PackageManager, packageName: String?): Boolean {
        try {
            pm.getPackageInfo(packageName!!, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.ERROR) {
            if (!isPackageInstalled(requireActivity().packageManager, googleTtsPackage))
                confirmDialog()
            else tts!!.setEngineByPackageName(googleTtsPackage);
        }
    }


    fun pagesReader() {
        if (i < pages) {
            str = builderArray[i]
            speakOut(str)
            Log.d(TAG, "onCreateView: i=$i")
            Log.d(TAG, "onCreateView: $str")
            binding.pdfViewHome.jumpTo(i)

        }
    }

    fun readFromUrl(intent: Intent) {
        val clipData = intent.clipData
        Log.d(TAG, "ReadFromUrl:Clipdata=$clipData\n\n")
        val url = extractLinks(clipData.toString())
        Log.d(TAG, "ReadFromUrl: \n\nFinal URL = ${url[0]}")

        var dataText = getDataFrommUrl(url[0])


    }

    private fun getDataFrommUrl(s: String): String {

        CoroutineScope(IO).launch {
            if (Patterns.WEB_URL.matcher(s).matches()) {
                try {
                    val doc: Document = Jsoup.connect(s).get()
                    var link = Jsoup.parse(URL(s), 4000)
                    val yourURLStr = URLEncoder.encode(s, "UTF-8")
                    Log.d(TAG, "getDataFrommUrl: \n\nNormal URL= $s\n\n")

                    Log.d(
                        TAG,
                        "getDataFrommUrl: content ${link.getElementsByClass("content").text()}"
                    )

                    Log.d(TAG, "getDataFrommUrl: \n\n\nBody = ${link.body().text()}")
                } catch (ex: SocketTimeoutException) {
                    Log.d(TAG, "getDataFrommUrl: ${ex.message}")
                } catch (ep: MalformedURLException) {
                } catch (e: IOException) {
                    Log.d(TAG, "getDataFrommUrl: ${e.message}")
                }
            } else {
                Toast.makeText(requireContext(), "URL not supported", Toast.LENGTH_SHORT).show()
            }


        }

        return ""
    }

    private fun extractLinks(text: String): Array<String> {
        val m: Matcher = Patterns.WEB_URL.matcher(text)
        while (m.find()) {
            val url: String = m.group()
            Log.d(TAG, "URL extracted: $url")
            links.add(url)
        }
        return links.toTypedArray()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            21 -> {
                if (resultCode == RESULT_OK) {
                    val path = data!!.data
                    handlePdfFilePath(path)
                }
            }
            else -> {
                Log.d(TAG, "onActivityResult: ELSE")

            }
        }
    }

    private fun handlePdfFilePath(path: Uri?) {
        extractTextFromPdfFile(path!!)
        displayFromUri(path)
    }


}










