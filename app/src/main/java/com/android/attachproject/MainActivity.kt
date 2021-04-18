package com.android.attachproject

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import com.mirza.attachmentmanager.managers.AttachmentManager
import com.mirza.attachmentmanager.managers.HideOption

import kotlinx.android.synthetic.main.activity_main.*
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var attachmentManager: AttachmentManager? = null
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
                .hide(HideOption.DOCUMENT) // You can hide any option do you want
                .setMaxCameraPhotoSize(200000)
                .build()
        fab.setOnClickListener {

            attachmentManager?.openSelection()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val list = attachmentManager?.manipulateAttachments(applicationContext,requestCode, resultCode, data)

        Toast.makeText(this, list?.size.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        attachmentManager?.handlePermissionResponse(requestCode, permissions, grantResults)
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
