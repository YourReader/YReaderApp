package read.code.yourreader.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import read.code.yourreader.Adapter.FilesAdapter
import read.code.yourreader.MVVVM.viewmodels.FilesViewModel
import read.code.yourreader.R
import read.code.yourreader.data.Files
import read.code.yourreader.databinding.FragmentBooksBinding
import read.code.yourreader.others.Values
import java.io.File

@DelicateCoroutinesApi
@SuppressLint("SetTextI18n")
class BooksFragment : Fragment(), FilesAdapter.OnCardViewClickListener {
    private var dir = File(Environment.getExternalStorageDirectory().absolutePath)
    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!
    private var permissionGranted = false
    private val TAG = "bFragment"
    private lateinit var mFilesViewModel: FilesViewModel
    private val filesAdapter = FilesAdapter(this@BooksFragment)
    private val pdfPattern = ".pdf"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            filesRecyclerView.apply {
                adapter = filesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        init()
        binding.loadDocuBooks.setOnClickListener {
            binding.progressBarBooks.visibility = View.VISIBLE
            loadFiles()
        }
    }

    private fun init() {
        mFilesViewModel =
            ViewModelProvider(this@BooksFragment).get(FilesViewModel::class.java)
        Log.d(TAG, "init:isDBEmpty ${Values.isDbEmpty}")
        if (!Values.isDbEmpty) {
            hideLoadDocuLayout()
            loadFiles()
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

    override fun onTrashClick(file: Files, isTrash: Boolean) {
        if (isTrash)
            Toast.makeText(requireContext(), "Added to trash", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(requireContext(), "Removed from trash", Toast.LENGTH_SHORT).show()
        mFilesViewModel.updateTrashStatus(file, isTrash)
    }

    private fun handlePermissions() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            checkPermissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE, "MANAGE", 101)
        } else {
            checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, "STORAGE", 100)
        }
    }

    private fun searchFiles(dir: File) {
        val FileList = dir.listFiles()
        if (FileList != null) {
            for (i in FileList.indices) {
                if (FileList[i].isDirectory) {
                    searchFiles(FileList[i])
                } else {
                    if (FileList[i].name.endsWith(pdfPattern)) {
                        mFilesViewModel.addFile(
                            Files(
                                path = FileList[i].toString(),
                                type = pdfPattern
                            )
                        )
                        Log.d(
                            TAG,
                            "searchFiles: S: ${
                                "%.2f".format(
                                    (FileList[i].length()).toFloat()
                                            / 1048576.0
                                )
                            } MB name:${FileList[i].name}"
                        )
                    }

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadFiles() {
        handlePermissions()
        GlobalScope.launch(Dispatchers.IO) {
            if (!permissionGranted) {
                Log.d(TAG, "loadFiles: No permissions")
            } else if (permissionGranted) {
                if (Values.isDbEmpty) {
                    Log.d(TAG, "loadFiles: First Time User")
                    searchFiles(dir)
                    lifecycleScope.launch {
                        mFilesViewModel.readAllData.observe(viewLifecycleOwner) {
                            filesAdapter.submitList(it)
                            hideLoadDocuLayout()
//                            showData()
                        }
                    }
                } else if (!Values.isDbEmpty) {
                    Log.d(TAG, "loadFiles: Not First Time User")
                    lifecycleScope.launch {
                        mFilesViewModel.readAllData.observe(requireActivity()) {
                            filesAdapter.submitList(it)
//                            showData()
                        }
                    }
                }
            }
        }
    }


    private fun checkPermissions(permission: String, name: String, requestCode: Int) {
        Log.d(TAG, "checkPermissions: PERMISSION ASKED $name")
        if (SDK_INT >= Build.VERSION_CODES.R) {
            when {
                Environment.isExternalStorageManager() -> {
                    permissionGranted = true
                    Log.d(TAG, "checkPermissions: $name permission Granted")
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    Log.d(TAG, "checkPermissions: IN THAT WEIRD SCOPE")
                    showManagePermissionDialog(false, permission)
                }
                else -> {
                    Log.d(TAG, "checkPermissions: IN ELSE $permission")
                    showManagePermissionDialog(false, permission)
                }
            }
        } else {
            when {
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    permissionGranted = true
                    Log.d(TAG, "checkPermissions: $name permission Granted")
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    Log.d(TAG, "checkPermissions: IN THAT WEIRD SCOPE")
                    showManagePermissionDialog(true, permission)
                }
                else -> {
                    Log.d(TAG, "checkPermissions: IN ELSE $permission")
                    showManagePermissionDialog(true, permission)
                }
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun showManagePermissionDialog(normal: Boolean = true, name: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setMessage("To open Books , PDF's and documents the application needs permissions")
            setTitle("Permission Required")
            if (!normal) {
                setPositiveButton("Grant") { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data =
                            Uri.parse(String.format("package:%s", requireContext().packageName))
                        startActivityForResult(intent, 2296)
                    } catch (e: Exception) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        startActivityForResult(intent, 2296)
                    }
                }
            } else {
                setPositiveButton("Grant") { _, _ ->
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(name), 100)
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                    permissionGranted = true
                } else if (SDK_INT >= Build.VERSION_CODES.R) {
                    checkPermissions(
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        "MANAGE",
                        101
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty())
            for (i in grantResults.indices)
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    permissionGranted = true
                else
                    showManagePermissionDialog(true, permissions.toString())

    }

    private fun hideLoadDocuLayout() {
        binding.noticeNoLoaded.visibility = View.GONE
        binding.loadDocuBooks.visibility = View.GONE
        binding.progressBarBooks.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}