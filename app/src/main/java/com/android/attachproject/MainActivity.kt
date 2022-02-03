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
import com.mirza.attachmentmanager.managers.HideOption
import com.mirza.attachmentmanager.utils.AttachmentUtil
import com.mirza.attachmentmanager.utils.FileUtil

import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtils
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var attachmentManager: AttachmentManager? = null
    private var mLauncher = registerForActivityResult(StartActivityForResult()) { result ->

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
            .hide(HideOption.DOCUMENT)// You can hide any option do you want
            .setMaxPhotoSize(200000) // Set max camera photo size in bytes
            .galleryMimeTypes(gallery) // mime types for gallery
            .filesMimeTypes(files) // mime types for files
            .build()
        fab.setOnClickListener {


          attachmentManager?.openSelection(mLauncher)
          //  test()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        attachmentManager?.handlePermissionResponse(requestCode, permissions, grantResults,mLauncher)
    }

}
