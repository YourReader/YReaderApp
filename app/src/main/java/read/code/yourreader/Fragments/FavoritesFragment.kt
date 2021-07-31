package read.code.yourreader.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favorites.*
import read.code.yourreader.Adapter.FilesAdapter
import read.code.yourreader.MVVVM.viewmodels.FilesViewModel
import read.code.yourreader.data.Files
import read.code.yourreader.databinding.FragmentFavoritesBinding


class FavoritesFragment : Fragment(), FilesAdapter.OnCardViewClickListener {
    private lateinit var filesViewModel: FilesViewModel
    private var binding: FragmentFavoritesBinding? = null
    private val filesAdapter = FilesAdapter(this@FavoritesFragment)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        init()
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            favoritesRecyclerView.apply {
                adapter = filesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        filesViewModel.readFavData.observe(requireActivity()) {
            if (it.isNotEmpty())
                filesAdapter.submitList(it)

        }
    }

    private fun init() {
        filesViewModel = ViewModelProvider(this@FavoritesFragment).get(FilesViewModel::class.java)
    }

    override fun onCardClick(files: Files) {
        TODO("Not yet implemented")
    }

    override fun onFavoriteClick(files: Files, isFavorite: Boolean) {
        TODO("Not yet implemented")
    }
}