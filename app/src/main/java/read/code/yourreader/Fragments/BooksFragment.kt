package read.code.yourreader.Fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
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
import androidx.fragment.app.Fragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import read.code.yourreader.databinding.FragmentBooksBinding
import java.io.File


class BooksFragment : Fragment() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    var dir = File(Environment.getExternalStorageDirectory().absolutePath)
    private var pdfs = ArrayList<File>()
    private var _binding: FragmentBooksBinding? = null
    private val binding get() = _binding!!
    lateinit var bitmap: Bitmap
    var permissionGranted=false

    override fun onCreateView( //the fragment is initialized and bound to the nav host activity.
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            checkPermissions(
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                "MANAGE",
                101
            )
        }
        binding.loadDocuBooks.setOnClickListener {
            loadFiles()

        }

        return binding.root
    }



    private fun Search_Dir(dir: File) {
        Log.d("TAG", "Search_Dir: INNN")
        val pdfPattern = ".pdf"
        val FileList = dir.listFiles()
        if (FileList != null) {
            for (i in FileList.indices) {
                if (FileList[i].isDirectory) {
                    Search_Dir(FileList[i])
                } else {
                    if (FileList[i].name.endsWith(pdfPattern)) {
                        pdfs.add(FileList[i])
                        Log.d("TAG", "Search_Dir: MP4: ${FileList[i]}")
                    }
                }
            }
        }
    }


    fun loadFiles()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            checkPermissions(
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                "MANAGE",
                101
            )
        }
        if (permissionGranted)
        {
            MainScope().launch {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Search_Dir(dir)
                    val fd = ParcelFileDescriptor.open(
                        pdfs[pdfs.size - 1],
                        ParcelFileDescriptor.MODE_READ_ONLY
                    )
                    val renderer = PdfRenderer(fd)
                    bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_4444)
                    val page: PdfRenderer.Page = renderer.openPage(0)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    hideLoadDocuLayout()
                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP")
                }
            }
        }
        else{
            Toast.makeText(requireContext(), "Cant Load Documents without Permission", Toast.LENGTH_SHORT).show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkPermissions(permission: String, name: String, requestCode: Int) {
        Log.d(TAG, "checkPermissions: PERMISSION ASKED $name")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "checkPermissions:  IS => M")
            when {
                Environment.isExternalStorageManager() -> {
                    Log.d(TAG, "checkPermissions: $name permission Granted")
                }

                shouldShowRequestPermissionRationale(permission) -> {
                    Log.d(TAG, "checkPermissions: IN THAT WEIRD SCOPE")
                    showManagePermissionDialog()
                }
                else -> {
                    Log.d(TAG, "checkPermissions: IN ELSE $permission")
                    showManagePermissionDialog()
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showManagePermissionDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setMessage("To open Books , PDF's and documents the application needs permissions")
            setTitle("Permission Required")
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
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                    permissionGranted=true
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    checkPermissions(
                        android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        "MANAGE",
                        101
                    )

                }
            }
        }
    }

    fun hideLoadDocuLayout()
    {
        binding.noticeNoLoaded.visibility=View.GONE
        binding.loadDocuBooks.visibility=View.GONE

    }
}
