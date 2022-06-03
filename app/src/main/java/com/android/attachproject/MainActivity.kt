package com.android.attachproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.attachproject.databinding.ActivityMainBinding
import com.mirza.attachmentmanager.managers.AttachmentManager
import com.mirza.attachmentmanager.managers.HideOption
import com.mirza.attachmentmanager.models.AttachmentDetail
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var attachmentManager: AttachmentManager? = null
    private var attachmentAdapter: AttachmentAdapter? = null
    private var allAttachments : ArrayList<AttachmentDetail>?= arrayListOf<AttachmentDetail>()
    private var mLauncher = registerForActivityResult(StartActivityForResult()) { result ->

        allAttachments = attachmentAdapter?.getItems()


        attachmentManager?.manipulateAttachments(this,result.resultCode,result.data)?.let {

            if(it.size > 0 && it[0].mimeType?.contains("pdf",ignoreCase = true) == true) {
                if (it.size > 0 && it[0].size!! <= 2000000) {
                    allAttachments?.addAll(it)
                } else {
                    Toast.makeText(
                        this,
                        "File size can't be more than 2MB",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }else if (it.size > 0){
                allAttachments?.addAll(it)
            }else{

            }

        }
        attachmentAdapter?.updateData(allAttachments!!)
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        attachmentManager = AttachmentManager.AttachmentBuilder(this) // must pass Context
            .fragment(null) // pass fragment reference if you are in fragment
            .setUiTitle("Choose File") // title of dialog or bottom sheet
            .allowMultiple(false) // set true if you want make multiple selection, default is false
            .asBottomSheet(true) // set true if you need to show selection as bottom sheet, default is as Dialog
            .setOptionsTextColor(android.R.color.holo_green_light) // change text color
            .setImagesColor(R.color.colorAccent) // change icon color
            .hide(HideOption.DOCUMENT) // You can hide any option do you want
            .setMaxPhotoSize(200000) // Set max  photo size in bytes
            .galleryMimeTypes(gallery) // mime types for gallery
            .filesMimeTypes(files) // mime types for files
            .build(); // Hide any of the three options
        fab.setOnClickListener {

            attachmentManager?.openSelection(mLauncher)
        }

        binding.contentLayout.attachmentRecyclerView.layoutManager =
            GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        attachmentAdapter = AttachmentAdapter(allAttachments!!)
        binding.contentLayout.attachmentRecyclerView.adapter = attachmentAdapter
        binding.contentLayout.addAttachmentImageView.setOnClickListener {
            attachmentManager?.openSelection(mLauncher)
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        attachmentManager?.handlePermissionResponse(
            requestCode,
            permissions,
            grantResults,
            mLauncher
        )
    }

}
