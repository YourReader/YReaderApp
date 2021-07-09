package read.code.yourreader.Fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import read.code.yourreader.R

class DownloadsFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_downloads, container, false)

        checkPermissions(READ_EXTERNAL_STORAGE,"Storage",100)


        return view
    }


    private fun checkPermissions(permissions:String, permission_name:String, code:Int){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            when{
                 ContextCompat.checkSelfPermission(requireContext(),permissions)  == PackageManager.PERMISSION_GRANTED ->{
                     Log.d(TAG, "checkPermissions: Permission Granted")
                }
                shouldShowRequestPermissionRationale(permissions) -> showDialog(permissions,permission_name,code)

                else -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(permissions),code)
            }
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder=AlertDialog.Builder(requireContext())

        builder.apply {
            setMessage("Permission to Access Your Pdf and Readable Documents")
            setTitle("Permission Required")
            setPositiveButton("Ok"){
                    _, _ ->

                ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission),requestCode)

            }
        }

        val dialog=builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        fun innerCheck(name:String)
        {
            if (grantResults.isEmpty()||grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireContext(), "$name Permission Not Granted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "$name Permission Granted", Toast.LENGTH_SHORT).show()}
        }
        when(requestCode){
            100  -> innerCheck("Storage")
        }

    }



}