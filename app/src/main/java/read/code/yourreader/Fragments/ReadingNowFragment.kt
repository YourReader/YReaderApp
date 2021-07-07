package read.code.yourreader.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import read.code.yourreader.R

class ReadingNowFragment : Fragment() {
    override fun onCreateView( //the fragment is initialized and bound to the nav host activity.
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reading_now, container, false)
    }
}
