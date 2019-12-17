package com.android.attachproject

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import com.mirza.attachmentmanager.managers.AttachmentManager
import com.mirza.attachmentmanager.managers.HideOption

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var attachmentManager: AttachmentManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        attachmentManager = AttachmentManager.AttachmentBuilder(this) // must pass Context
                .activity(this) // container activity
                .fragment(null) // pass fragment reference if you are in fragment
                .setUiTitle(getString(R.string.m_choose)) // title of dialog or bottom sheet
                .allowMultiple(true) // set true if you want make multiple selection, default is false
                .asBottomSheet(false) // set true if you need to show selection as bottom sheet, default is as Dialog
                .setOptionsTextColor(android.R.color.holo_green_light)
                .setImagesColor(R.color.colorAccent)
                .hide(HideOption.DOCUMENT) // You can hide any option do you want
                .build()
        fab.setOnClickListener {

            attachmentManager?.openSelection()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val list = attachmentManager?.manipulateAttachments(requestCode, resultCode, data)
        Toast.makeText(this, list?.size.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        attachmentManager?.handlePermissionResponse(requestCode, permissions, grantResults)
    }

}
