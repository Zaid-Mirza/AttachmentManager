package com.mirza.attachmentmanager.managers

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mirza.attachmentmanager.R
import com.mirza.attachmentmanager.fragments.AttachmentBottomSheet
import com.mirza.attachmentmanager.fragments.AttachmentFragment
import com.mirza.attachmentmanager.fragments.DialogAction
import com.mirza.attachmentmanager.models.AttachmentDetail
import com.mirza.attachmentmanager.utils.AttachmentUtil
import com.mirza.attachmentmanager.utils.FileUtil
import java.io.File
import java.lang.ref.WeakReference


enum class HideOption {
    GALLERY, CAMERA, DOCUMENT
}

var selectedRequestCode = AttachmentUtil.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE

class AttachmentManager private constructor(builder: AttachmentBuilder) {


    companion object {
        val instance = this
    }

    private var title: String? = ""
    private var activity: WeakReference<AppCompatActivity>? = null
    private var fragment: WeakReference<Fragment>? = null
    private var selection: DialogAction? = null
    private var isMultiple = false
    private var cameraFile: File? = null
    private var isBottomSheet = false
    private var imagesColor: Int? = null
    private var optionsTextColor: Int? = null
    private var hideOptions: HideOption? = null
    private var maxPhotoSize: Long? = null
    private var galleryMimeTypes: Array<String>? = null
    private var filesMimeTypes: Array<String>? = null

    init {
        activity = builder.activity
        fragment = builder.fragment
        title = builder.title
        isMultiple = builder.isMultiple
        isBottomSheet = builder.isBottomSheet
        imagesColor = builder.imagesColor
        optionsTextColor = builder.optionsTextColor
        hideOptions = builder.hideOption
        maxPhotoSize = builder.maxPhotoSize
        galleryMimeTypes = builder.galleryMimeTypes
        filesMimeTypes = builder.filesMimeTypes
    }

    /**
     * Call this method to open attachment selection
     */
    fun openSelection(launcher: ActivityResultLauncher<Intent>) {
        activity?.get()?.let {

            if (isBottomSheet) {
                val attachmentFragmentSheet = AttachmentBottomSheet(
                    title,
                    optionsTextColor,
                    imagesColor,
                    hideOptions
                ) { action ->
                    handleSelectionResponse(action, launcher)
                }
                attachmentFragmentSheet.show(it.supportFragmentManager, "DIALOG_SELECTION")
            } else {
                val attachmentFragmentDialog = AttachmentFragment(
                    title,
                    optionsTextColor,
                    imagesColor,
                    hideOptions
                ) { action ->
                    handleSelectionResponse(action, launcher)
                }
                attachmentFragmentDialog.show(it.supportFragmentManager, "DIALOG_SELECTION")
            }

        }
    }

    /**
     * @param action contains selection value selected by user using dialog or bottom sheet
     */

    private fun handleSelectionResponse(
        action: DialogAction,
        launcher: ActivityResultLauncher<Intent>
    ) {
        selection = action
        when (action) {
            DialogAction.GALLERY -> {
                openGallery(launcher)
            }

            DialogAction.CAMERA -> {
                startCamera(launcher)
            }

            DialogAction.FILE -> {
                openFileSystem(launcher)
            }
        }
    }

    /**
     * @param activity container Activity and could be null if if user is interacting with AttachmentManager from fragment
     * @param fragment will hold the reference to fragment if user is interacting with AttachmentManager from fragment
     * @param permissionCode used in case of permission grant
     */

    private fun openCamera(
        activity: AppCompatActivity?,
        fragment: Fragment?,
        permissionCode: Int,
        launcher: ActivityResultLauncher<Intent>
    ) {
        if (PermissionManager.checkForPermissions(
                activity,
                fragment,
                PermissionManager.cameraPermissionList,
                permissionCode
            )
        ) {
            val tuple = AttachmentUtil.onCamera(activity!!)
            cameraFile = tuple.photoFile
            AttachmentUtil.openCamera(tuple, activity, fragment, launcher)
            selectedRequestCode = AttachmentUtil.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
        }
    }

    /**
     * @param activity container Activity and could be null if if user is interacting with AttachmentManager from fragment
     * @param fragment will hold the reference to fragment if user is interacting with AttachmentManager from fragment
     * @param permissionCode used in case of permission grant
     */

    private fun openGallery(
        activity: AppCompatActivity?,
        fragment: Fragment?,
        permissionCode: Int, launcher: ActivityResultLauncher<Intent>
    ) {
        if (PermissionManager.checkForPermissions(
                activity,
                fragment,
                PermissionManager.storagePermissionList,
                permissionCode
            )
        ) {

            val intent =
                AttachmentUtil.onPhoto(activity!!, isMultiple, galleryMimeTypes = galleryMimeTypes)
            AttachmentUtil.openGallery(intent, activity, fragment, launcher)
            selectedRequestCode = AttachmentUtil.PICK_PHOTO_CODE
        }
    }

    /**
     * @param activity container Activity and could be null if if user is interacting with AttachmentManager from fragment
     * @param fragment will hold the reference to fragment if user is interacting with AttachmentManager from fragment.
     * @param permissionCode used in case of permission grant
     */
    private fun openFileSystem(
        activity: AppCompatActivity?,
        fragment: Fragment?,
        permissionCode: Int, launcher: ActivityResultLauncher<Intent>
    ) {


        selectedRequestCode = AttachmentUtil.FILE_CODE
        val intent = AttachmentUtil.onFile(activity, fragment, isMultiple, filesMimeTypes, launcher)

    }

    /**
     * Use below three methods to interact with AttachmentManager directly without any dialog or bottom sheet
     */
    fun startCamera(launcher: ActivityResultLauncher<Intent>) {
        openCamera(
            activity?.get(),
            fragment?.get(),
            AttachmentUtil.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE, launcher
        )
    }

    fun openGallery(launcher: ActivityResultLauncher<Intent>) {
        openGallery(activity?.get(), fragment?.get(), AttachmentUtil.PICK_PHOTO_CODE, launcher)
    }

    fun openFileSystem(launcher: ActivityResultLauncher<Intent>) {
        openFileSystem(activity?.get(), fragment?.get(), AttachmentUtil.FILE_CODE, launcher)
    }


    /**
     * Use this method from onActivityResult within your activity or fragment
     * @return List of AttachmentDetail objects
     */
    fun manipulateAttachments(
        context: Context,
        resultCode: Int,
        data: Intent?
    ): ArrayList<AttachmentDetail> {
        val list = ArrayList<AttachmentDetail>()
        if (resultCode == RESULT_OK) {
            when (selectedRequestCode) {
                AttachmentUtil.FILE_CODE, AttachmentUtil.PICK_PHOTO_CODE -> {
                    if (data != null) {
                        if (isMultiple && data.clipData != null) {

                            data.clipData?.let {
                                // Toast.makeText(context!!, it.itemCount.toString(), Toast.LENGTH_SHORT).show()
                                for (x in 0 until it.itemCount) {
                                    it.getItemAt(x).uri?.let { uri ->
                                        var uriFromFile = uri
                                        if (selectedRequestCode == AttachmentUtil.PICK_PHOTO_CODE && FileUtil.getMimeType(
                                                uriFromFile,
                                                activity?.get()!!
                                            )?.contains("video", true) == false
                                        ) {
                                            checkAndAdjustImageSize(uri, context)?.let {
                                                uriFromFile = it
                                            }
                                        }
                                        list.add(
                                            prepareAttachment(
                                                uriFromFile,
                                                FileUtil.getFileDisplayName(
                                                    uriFromFile,
                                                    activity?.get()!!,
                                                    File(uriFromFile.toString())
                                                ),
                                                FileUtil.getMimeType(
                                                    uriFromFile,
                                                    activity?.get()!!
                                                ),
                                                FileUtil.getFileSize(uriFromFile, activity?.get()!!)
                                            )
                                        )
                                    }

                                }
                            }

                        } else {

                            val fileUri = data.data
                            fileUri?.let { uri ->
                                var uriFromFile = uri
                                if (selectedRequestCode == AttachmentUtil.PICK_PHOTO_CODE && FileUtil.getMimeType(
                                        uriFromFile,
                                        activity?.get()!!
                                    )?.contains("video", true) == false
                                ) {
                                    checkAndAdjustImageSize(uri, context)?.let {
                                        uriFromFile = it
                                    }
                                }

                                list.add(
                                    prepareAttachment(
                                        uriFromFile,
                                        FileUtil.getFileDisplayName(
                                            uriFromFile,
                                            activity?.get()!!,
                                            File(uriFromFile.toString())
                                        ),
                                        FileUtil.getMimeType(uriFromFile, activity?.get()!!),
                                        FileUtil.getFileSize(uriFromFile, activity?.get()!!)
                                    )
                                )
                            }
                        }
                    }
                }

                AttachmentUtil.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE -> {

                    cameraFile?.let {

                        var fileUri = Uri.fromFile(cameraFile)
                        val displayName = FileUtil.getFileDisplayName(
                            fileUri,
                            activity?.get() as AppCompatActivity,
                            it
                        )
                        // Resize Image
                        checkAndAdjustImageSize(fileUri, context)?.let {
                            fileUri = it
                        }


                        list.add(
                            prepareAttachment(
                                fileUri,
                                displayName,
                                FileUtil.getMimeType(fileUri, activity?.get()!!),
                                FileUtil.getFileSize(fileUri, activity?.get()!!)
                            )
                        )
                    }


                }
            }
        }

        return list
    }


    private fun checkAndAdjustImageSize(uri: Uri, context: Context): Uri? {
        maxPhotoSize?.let {
            if (FileUtil.getFileSize(uri, activity?.get()!!) > it) {
                val file = AttachmentUtil.resizeImage(uri, 700, context)
                return Uri.fromFile(file)
            }
        }

        return null

    }

    /**
     * Use this method from onRequestPermissionsResult within your activity or fragment
     * It will handle permission results for you
     */
    fun handlePermissionResponse(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray, launcher: ActivityResultLauncher<Intent>
    ) {
        when (requestCode) {
            AttachmentUtil.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera(launcher)
                }
            }

            AttachmentUtil.PICK_PHOTO_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery(launcher)
                }
            }

            AttachmentUtil.FILE_CODE -> {
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        openFileSystem(launcher)
                    }
                } else {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        openFileSystem(launcher)
                    }
                }
            }
        }
    }

    private fun prepareAttachment(
        uri: Uri,
        name: String,
        mimeType: String?,
        size: Long
    ): AttachmentDetail {

        val attachmentDetail = AttachmentDetail()
        attachmentDetail.uri = uri
        attachmentDetail.name = name
        attachmentDetail.path = uri.path
        attachmentDetail.mimeType = mimeType
        attachmentDetail.size = size
        return attachmentDetail
    }

    /**
     * Initiates AttachmentManager object for you
     */
    data class AttachmentBuilder(private var activityContext: AppCompatActivity) {


        var fragment: WeakReference<Fragment>? = null
        var title: String? = activityContext.getString(R.string.m_choose)
        var activity: WeakReference<AppCompatActivity>? = null
        var isMultiple: Boolean = false
        var isBottomSheet: Boolean = false
        var imagesColor: Int? = null
        var optionsTextColor: Int? = null
        var hideOption: HideOption? = null
        var galleryMimeTypes: Array<String>? = null
        var filesMimeTypes: Array<String>? = null

        var maxPhotoSize: Long? = null
        fun fragment(fragment: Fragment?) =
            apply { this.fragment = WeakReference<Fragment>(fragment) }

        /**
         * @param title of dialog or bottom sheet
         */
        fun setUiTitle(title: String?) = apply { this.title = title }

        fun allowMultiple(isMultiple: Boolean) = apply { this.isMultiple = isMultiple }
        fun asBottomSheet(isBottomSheet: Boolean) = apply { this.isBottomSheet = isBottomSheet }
        fun setImagesColor(imagesColor: Int) = apply { this.imagesColor = imagesColor }
        fun setOptionsTextColor(textColor: Int) = apply { this.optionsTextColor = textColor }
        fun hide(option: HideOption?) = apply { this.hideOption = option }
        fun setMaxPhotoSize(maxSize: Long) = apply {
            maxPhotoSize = maxSize
        }

        fun galleryMimeTypes(types: Array<String>) = apply {
            galleryMimeTypes = types
        }

        fun filesMimeTypes(types: Array<String>) = apply {
            filesMimeTypes = types
        }


        fun build() = AttachmentManager(this)

        init {
            activity = WeakReference(activityContext)
        }
    }


}