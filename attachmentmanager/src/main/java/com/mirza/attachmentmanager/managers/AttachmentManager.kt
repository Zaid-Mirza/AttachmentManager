package com.mirza.attachmentmanager.managers

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mirza.attachmentmanager.R
import com.mirza.attachmentmanager.fragments.AttachmentFragment
import com.mirza.attachmentmanager.utils.AttachmentUtil
import com.mirza.attachmentmanager.fragments.DialogAction
import com.mirza.attachmentmanager.models.AttachmentDetail
import com.mirza.attachmentmanager.utils.FileUtil
import java.io.File

class AttachmentManager private constructor(builder: AttachmentBuilder) {


    private var title: String? = ""
    private var activity: AppCompatActivity? = null
    private var fragment: Fragment? = null
    private var selection: DialogAction? = null
    private var isMultiple = false
    private var context: Context? = null
    private var cameraFile: File? = null

    init {
        activity = builder.activity
        fragment = builder.fragment
        title = builder.title
        isMultiple = builder.isMultiple
        context = builder.context
    }

    fun openSelectionDialog() {
        activity?.let {

            val fragment = AttachmentFragment() { action ->
                selection = action
                when (action) {
                    DialogAction.GALLERY -> {
                        openGallery(activity, fragment, AttachmentUtil.PICK_PHOTO_CODE)
                    }
                    DialogAction.CAMERA -> {
                        openCamera(activity, fragment, AttachmentUtil.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
                    }

                    DialogAction.FILE -> {
                        openFileSystem(activity, fragment, AttachmentUtil.FILE_CODE)
                    }
                }
            }
            fragment.show(it.supportFragmentManager, "DIALOG_SELECTION")
        }
    }


    private fun openCamera(activity: AppCompatActivity?, fragment: Fragment?, permissionCode: Int) {
        if (PermissionManager.checkForPermissions(activity, fragment, PermissionManager.cameraPermissionList, permissionCode)) {
            val tuple = AttachmentUtil.onCamera(activity!!)
            cameraFile = tuple.photoFile
            AttachmentUtil.openCamera(tuple, activity, fragment)
        }
    }

    private fun openGallery(activity: AppCompatActivity?, fragment: Fragment?, permissionCode: Int) {
        if (PermissionManager.checkForPermissions(activity, fragment, PermissionManager.storagePermissionList, permissionCode)) {
            val intent = AttachmentUtil.onPhoto(activity!!, isMultiple)
            AttachmentUtil.openGallery(intent, activity, fragment)
        }
    }

    private fun openFileSystem(activity: AppCompatActivity?, fragment: Fragment?, permissionCode: Int) {
        if (PermissionManager.checkForPermissions(activity, fragment, PermissionManager.storagePermissionList, permissionCode)) {
            val intent = AttachmentUtil.onFile(activity, fragment)
        }
    }

    fun manipulateAttachments(requestCode: Int, resultCode: Int, data: Intent?): ArrayList<AttachmentDetail>? {
        val list = ArrayList<AttachmentDetail>()
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                AttachmentUtil.FILE_CODE, AttachmentUtil.PICK_PHOTO_CODE -> {
                    if (data != null) {
                        if (isMultiple) {

                            // Stub

                        } else {

                            val fileUri = data.data
                            fileUri?.let {
                                list.add(prepareAttachment(it, FileUtil.getFileDisplayName(it, context!!, File(it.toString())), FileUtil.getMimeType(it, context!!), FileUtil.getFileSize(it, context!!)))
                            }
                        }
                    }
                }
                AttachmentUtil.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val fileUri = Uri.fromFile(cameraFile)
                    val file = File(fileUri.toString())
                    val displayName = FileUtil.getFileDisplayName(fileUri, activity as AppCompatActivity, file)
                    list.add(prepareAttachment(fileUri, displayName, FileUtil.getMimeType(fileUri, context!!), FileUtil.getFileSize(fileUri, context!!)))


                }
            }
        }

        return list
    }

    private fun prepareAttachment(uri: Uri, name: String, mimeType: String?, size: Long): AttachmentDetail {

        val attachmentDetail = AttachmentDetail()
        attachmentDetail.uri = uri
        attachmentDetail.name = name
        attachmentDetail.path = uri.path
        attachmentDetail.mimeType = mimeType
        attachmentDetail.size = size
        return attachmentDetail
    }

    data class AttachmentBuilder(var context: Context,
                                 var activity: AppCompatActivity? = null,
                                 var fragment: Fragment? = null,
                                 var title: String? = context.getString(R.string.choose),
                                 var isMultiple: Boolean = false) {

        fun activity(activity: AppCompatActivity?) = apply { this.activity = activity }
        fun fragment(fragment: Fragment?) = apply { this.fragment = fragment }
        fun setTitle(title: String?) = apply { this.title = title }
        fun allowMultiple(isMultiple: Boolean) = apply { this.isMultiple = isMultiple }

        fun build() = AttachmentManager(this)

    }


}