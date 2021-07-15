package read.code.yourreader.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import read.code.yourreader.databinding.FragmentBooksBinding
import java.io.File


class BooksFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    var dir = File(Environment.getExternalStorageDirectory().absolutePath)
    private var pdfs = ArrayList<String>()
    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!
    lateinit var bitmap: Bitmap
    var permissionGranted = false
    private val TAG = "bFragment"

    override fun onCreateView( //the fragment is initialized and bound to the nav host activity.
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBooksBinding.inflate(inflater, container, false)

        binding.loadDocuBooks.setOnClickListener {
            Log.d(TAG, "onCreateView:CLICKED ")
            loadFiles()
        }

        return binding.root
    }


    private fun handlePermissions() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            checkPermissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE, "MANAGE", 101)
        } else {
            checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, "STORAGE", 100)
        }
    }

    private fun Search_Dir_PDF(dir: File) {
        val pdfPattern = ".pdf"
        val FileList = dir.listFiles()
        if (FileList != null) {
            for (i in FileList.indices) {
                if (FileList[i].isDirectory) {
                    Search_Dir_PDF(FileList[i])
                } else {
                    if (FileList[i].name.endsWith(pdfPattern)) {
                        pdfs.add(FileList[i].toString())
                        Log.d("TAG", "Search_Dir: PDF: ${FileList[i]}")
                    }
                }
            }
        }
    }

    private fun Search_Dir_WORD(dir: File) {
        val pdfPattern = ".docx"
        val FileList = dir.listFiles()
        if (FileList != null) {
            for (i in FileList.indices) {
                if (FileList[i].isDirectory) {
                    Search_Dir_WORD(FileList[i])
                } else {
                    if (FileList[i].name.endsWith(pdfPattern)) {
                        pdfs.add(FileList[i].toString())
                        Log.d("TAG", "Search_Dir: MP4: ${FileList[i]}")
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun loadFiles() {
        handlePermissions()

        Log.d(TAG, "loadFiles: after HandlePermissions")
        if (permissionGranted) {
            Log.d(TAG, "loadFiles: Permissions granted: $permissionGranted ")
            binding.progressBarBooks.visibility = View.VISIBLE
            MainScope().launch {
                Log.d(TAG, "loadFiles: MAIN SCOPE ")
                Search_Dir_PDF(dir)
                Log.d(
                    TAG,
                    "loadFiles: PDF: ${pdfs[pdfs.size - 1]} FILE: ${File(pdfs[pdfs.size - 1])}"
                )
                val fd = ParcelFileDescriptor.open(
                    File(pdfs[pdfs.size - 1]),
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
                Log.d(TAG, "loadFiles: PDFS SIZE: ${pdfs.size}")
                val renderer = PdfRenderer(fd)
                bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_4444)
                val page: PdfRenderer.Page = renderer.openPage(0)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                Search_Dir_WORD(dir)
                hideLoadDocuLayout()
                binding.bm.setImageBitmap(bitmap)
                binding.hellotext.text = pdfs[pdfs.size - 1].toString() + " Size: " + pdfs.size
            }
        } else {
            Log.d(TAG, "loadFiles: ELSE")
            Toast.makeText(
                requireContext(),
                "Cant Load Documents without Permission",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkPermissions(permission: String, name: String, requestCode: Int) {
        Log.d(TAG, "checkPermissions: PERMISSION ASKED $name")
        if (SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG, "checkPermissions:  IS => M")
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
                        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
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


}
