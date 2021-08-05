package read.code.yourreader.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_reading_now.*
import read.code.yourreader.Adapter.FilesAdapter
import read.code.yourreader.MVVVM.viewmodels.FilesViewModel
import read.code.yourreader.R
import read.code.yourreader.data.Files
import read.code.yourreader.databinding.FragmentReadingNowBinding

class ReadingNowFragment : Fragment(), FilesAdapter.OnCardViewClickListener {
    private var binding: FragmentReadingNowBinding? = null
    private lateinit var mFilesViewModel: FilesViewModel
    private val filesAdapter = FilesAdapter(this@ReadingNowFragment)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        init()
        binding = FragmentReadingNowBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            readingNowRecyclerView.apply {
                adapter = filesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        mFilesViewModel.readCurrentReadData.observe(requireActivity()) {
            filesAdapter.submitList(it)
        }
    }

    private fun init() {
        mFilesViewModel =
            ViewModelProvider(this@ReadingNowFragment).get(FilesViewModel::class.java)
    }

    override fun onCardClick(files: Files) {
        mFilesViewModel.updateReadingStatus(files, true)
        val b = Bundle()
        b.putParcelable("Object", files)
        val homeFrag = HomeFragment()
        homeFrag.arguments = b
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_main, homeFrag)
            .commit()
    }

    override fun onFavoriteClick(files: Files, isFavorite: Boolean) {
        if (isFavorite)
            Toast.makeText(requireContext(), "Added to Favorites", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(requireContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show()
        mFilesViewModel.updateFavoriteStatus(files, isFavorite)
    }

    override fun onDoneClick(file: Files, isDone: Boolean) {
        if (isDone)
            Toast.makeText(requireContext(), "Marked as Finished", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(requireContext(), "Marked as Unfinished", Toast.LENGTH_SHORT).show()
        mFilesViewModel.updateDoneStatus(file, isDone)
    }

}
