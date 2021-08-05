package read.code.yourreader.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import read.code.yourreader.Adapter.FilesAdapter
import read.code.yourreader.MVVVM.viewmodels.FilesViewModel
import read.code.yourreader.R
import read.code.yourreader.activities.MainActivity
import read.code.yourreader.data.Files
import read.code.yourreader.databinding.FragmentTrashBinding
import java.io.File


class TrashFragment : Fragment(), FilesAdapter.OnCardViewClickListener {
    private lateinit var mFilesViewModel: FilesViewModel
    private var filesAdapter = FilesAdapter(this)
    private var binding: FragmentTrashBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTrashBinding.inflate(inflater, container, false)
        (activity as MainActivity).setActionBarTitle("Trash")
        init()
        return binding!!.root
    }

    private fun init() {
        mFilesViewModel = ViewModelProvider(this@TrashFragment).get(FilesViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.trashRecyclerView.apply {
            adapter = filesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        mFilesViewModel.readTrashData.observe(requireActivity()) {
            filesAdapter.submitList(it)
        }
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

    override fun onTrashClick(files: Files, isTrash: Boolean) {

        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Are you sure you want to delete this file permanently?")
            .setPositiveButton("Yes") { _, _ ->
                val file = File(files.path)
                if (file.exists()) {
                    if (file.delete())
                        Toast.makeText(requireContext(), "File Deleted", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(requireContext(), "File not Deleted", Toast.LENGTH_SHORT)
                            .show()
                    mFilesViewModel.deleteFile(files)
                } else
                    Toast.makeText(requireContext(), "Error Occurred", Toast.LENGTH_SHORT)
                        .show()
            }.setNegativeButton("No") { _, _ ->
                Log.d("TAG", "onTrashClick:Canceled ")
            }
            .show()

    }
}