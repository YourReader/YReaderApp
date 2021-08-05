package read.code.yourreader.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_reading_now.*
import kotlinx.coroutines.launch
import read.code.yourreader.Adapter.FilesAdapter
import read.code.yourreader.MVVVM.viewmodels.FilesViewModel
import read.code.yourreader.R
import read.code.yourreader.data.Files
import read.code.yourreader.databinding.FragmentDoneReadingBinding

class DoneReadingFragment : Fragment(), FilesAdapter.OnCardViewClickListener {
    private lateinit var mFilesViewModel: FilesViewModel
    private var _binding: FragmentDoneReadingBinding? = null
    private val binding get() = _binding!!
    private val filesAdapter = FilesAdapter(this@DoneReadingFragment)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        init()
        _binding =
            FragmentDoneReadingBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.apply {
            doneReadingRecyclerView.apply {
                adapter = filesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }


        lifecycleScope.launch {
            mFilesViewModel.readDoneData.observe(requireActivity()) {
                filesAdapter.submitList(it)
            }
        }
    }

    private fun init() {
        mFilesViewModel =
            ViewModelProvider(this@DoneReadingFragment).get(FilesViewModel::class.java)
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
            Toast.makeText(requireContext(), "Removed from Favorites", Toast.LENGTH_SHORT)
                .show()
        mFilesViewModel.updateFavoriteStatus(files, isFavorite)
    }

    override fun onDoneClick(file: Files, isDone: Boolean) {
        Toast.makeText(requireContext(), "Marked as Unfinished", Toast.LENGTH_SHORT).show()
        mFilesViewModel.updateDoneStatus(file, isDone)
        mFilesViewModel.readDoneData.observe(this@DoneReadingFragment) {
            filesAdapter.submitList(it)
        }
    }
}
