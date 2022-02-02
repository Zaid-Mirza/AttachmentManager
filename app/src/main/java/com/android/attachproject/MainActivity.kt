package com.android.attachproject

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity;
import com.mirza.attachmentmanager.managers.AttachmentManager
import com.mirza.attachmentmanager.utils.AttachmentUtil
import com.mirza.attachmentmanager.utils.FileUtil

import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtils
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var attachmentManager: AttachmentManager? = null
    var mGetContent = registerForActivityResult(StartActivityForResult()) { result ->
        val ss = ""
        val code = result.data?.extras?.get(AttachmentUtil.REQUEST_CODE) as Int?
       val list =  attachmentManager?.manipulateAttachments(this,result.resultCode,result.data)
        Toast.makeText(this, list?.size.toString(), Toast.LENGTH_LONG).show()
    }
    var gallery = arrayOf(
        "image/png",
        "image/jpg",
        "image/jpeg"
    )
    var files = arrayOf(
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  // .ppt & .pptx
        "application/pdf"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        attachmentManager = AttachmentManager.AttachmentBuilder(this) // must pass Context
            .fragment(null) // pass fragment reference if you are in fragment
            .setUiTitle(getString(R.string.m_choose)) // title of dialog or bottom sheet
            .allowMultiple(true) // set true if you want make multiple selection, default is false
            .asBottomSheet(false) // set true if you need to show selection as bottom sheet, default is as Dialog
            .setOptionsTextColor(android.R.color.holo_green_light)
            .setImagesColor(R.color.colorAccent)
            // You can hide any option do you want
            .setMaxPhotoSize(200000) // Set max camera photo size in bytes
            .galleryMimeTypes(gallery) // mime types for gallery
            .filesMimeTypes(files) // mime types for files
            .build()
        fab.setOnClickListener {


          attachmentManager?.openSelection(mGetContent)
          //  test()
        }
    }

    fun test() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            action = Intent.ACTION_OPEN_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        val fileMimeTypes = FileUtil.mimeTypes
        intent.type = if (fileMimeTypes.size == 1) fileMimeTypes[0] else "*/*"
        if (!fileMimeTypes.isNullOrEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, fileMimeTypes)
        }

        val list = packageManager?.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        if (list?.size!! > 0) {

            mGetContent.launch(intent)
//                startActivityForResult(
//                    Intent.createChooser(
//                        intent,
//                        getString(com.mirza.attachmentmanager.R.string.m_selectFile_txt)
//                    ), AttachmentUtil.FILE_CODE
//                )

        }

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val list = attachmentManager?.manipulateAttachments(
//            applicationContext,
//            requestCode,
//            resultCode,
//            data
//        )
//
//        Toast.makeText(this, list?.size.toString(), Toast.LENGTH_LONG).show()
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        attachmentManager?.handlePermissionResponse(requestCode, permissions, grantResults,mGetContent)
    }

    fun convertFileTo64base(context: Context, uri: Uri?): String? {
        var byteArray = ByteArray(0)
        try {
            val inputStream = context.contentResolver.openInputStream(uri!!)
            byteArray = IOUtils.toByteArray(inputStream)
            inputStream!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
