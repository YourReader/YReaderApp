package read.code.yourreader.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import read.code.yourreader.R
import read.code.yourreader.di.components.DaggerFactoryComponent
import read.code.yourreader.di.modules.FactoryModule
import read.code.yourreader.di.modules.RepositoryModule
import read.code.yourreader.mvvm.repository.MainRepository
import read.code.yourreader.mvvm.viewmodels.MainViewModel
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: MainViewModel
    private lateinit var component: DaggerFactoryComponent
    private var tts:TextToSpeech?=null
    private val TAG = "MainActivity"
    private var currentuser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()




    }

    private fun init() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        mAuth = FirebaseAuth.getInstance()
        component = DaggerFactoryComponent.builder()
            .repositoryModule(RepositoryModule(this))
            .factoryModule(FactoryModule(MainRepository(this)))
            .build() as DaggerFactoryComponent
        viewModel = ViewModelProviders.of(this, component.getFactory())
            .get(MainViewModel::class.java)

        currentuser=mAuth.currentUser

        tts= TextToSpeech(this,this)

    }

    override fun onInit(status: Int) {
        if (status==TextToSpeech.SUCCESS)
        {
          var result=tts!!.setLanguage(Locale.US)
          if(result==TextToSpeech.LANG_MISSING_DATA){
              Toast.makeText(this, "Language is not Supported", Toast.LENGTH_SHORT).show()
          }
          else if(result==TextToSpeech.LANG_AVAILABLE){
              Log.d(TAG, "onInit: Initialised")
          }
        }
        else{
            Log.d(TAG, "onInit: Initialisation Failed")
        }
    }


    override fun onDestroy() {
        if (tts!=null){
            tts!!.stop()
            tts!!.shutdown()

        }
        super.onDestroy()
    }
    private fun speakOut(text:String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
}