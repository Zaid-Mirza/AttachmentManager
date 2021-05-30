package com.mirza.attachmentmanager.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.mirza.attachmentmanager.R
import com.mirza.attachmentmanager.models.Tuple
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.GenericArrayType

object AttachmentUtil {
    const val APP_TAG = "AttachmentManager"
    const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1000
    const val PICK_PHOTO_CODE = 1001
    const val FILE_CODE = 125
    val types = arrayOf(

            "image/png",
            "image/jpg",
            "image/jpeg",
            "video/mp4",
            "video/MP2T"
    )

    /**
     * @param fileName name of the image
     * @return  File for a photo stored on disk given the fileName
     */
    private fun getPhotoFileUri(fileName: String, context: Context): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    fun resizeImage(photoUri: Uri, desiredWidth: Int, context: Context): File {

        // by this point we have the camera photo on disk
        val inputStream = context.contentResolver.openInputStream(photoUri)
        val rawTakenImage = BitmapFactory.decodeStream(inputStream)

        val resizedBitmap: Bitmap = ImageUtils.scaleToFitWidth(rawTakenImage, desiredWidth)
        // Configure byte output stream
        val bytes = ByteArrayOutputStream()
        // Compress the image further
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes)
        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
        val resizedFile = getPhotoFileUri("IMGR_" + System.currentTimeMillis() + ".jpg", context)
        resizedFile.createNewFile()
        val fos = FileOutputStream(resizedFile)
        // Write the bytes of the bitmap to file
        fos.write(bytes.toByteArray())
        fos.close()
        return resizedFile
    }

    fun onCamera(context: Context): Tuple {
        // create Intent to take a picture and return control to the caller

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference to access to future access
        val photoFileName = "IMG_" + System.currentTimeMillis() + ".jpg"
        val photoFile = getPhotoFileUri(photoFileName, context)

        val fileProvider = FileProvider.getUriForFile(context, context.packageName + ".attachmentmanager", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)


        val tuple = Tuple()
        tuple.intent = intent
        tuple.photoFile = photoFile
        return tuple
    }

    fun onPhoto(context: Context, isMultiple: Boolean, galleryMimeTypes: Array<String>? = types): Intent {
        // Create intent for picking a photo from the gallery

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiple)

        intent.type = galleryMimeTypes?.joinToString(separator = ",") ?: types.joinToString(",")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_MIME_TYPES, galleryMimeTypes)
        return intent

    }

    fun onFile(activity: AppCompatActivity?, fragmentContext: Fragment?, isMultiple: Boolean?, fileMimeTypes: Array<String>?): Intent {
        val intent = Intent()
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultiple ?: false)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        val fileMimeTypes = fileMimeTypes ?: FileUtil.mimeTypes
        intent.type = if (fileMimeTypes.size == 1) fileMimeTypes[0] else "*/*"
        if (!fileMimeTypes.isNullOrEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, fileMimeTypes)
        }


        val list = activity?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list?.size!! > 0) {
            if (fragmentContext == null) {
                activity.startActivityForResult(
                        Intent.createChooser(
                                intent,
                                activity.getString(R.string.m_selectFile_txt)
                        ), FILE_CODE
                )
            } else {
                fragmentContext.startActivityForResult(
                        Intent.createChooser(
                                intent,
                                activity.getString(R.string.m_selectFile_txt)
                        ), FILE_CODE
                )
            }
        }
        return intent
    }

    fun openCamera(tuple: Tuple, activity: AppCompatActivity?, fragmentContext: Fragment?) {

        if (tuple.intent?.resolveActivity(activity?.packageManager!!) != null) {
            if (fragmentContext == null) {
                activity?.startActivityForResult(tuple.intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            } else {
                fragmentContext.startActivityForResult(tuple.intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    fun openGallery(intent: Intent, activity: AppCompatActivity?, fragmentContext: Fragment?) {
        if (intent.resolveActivity(activity?.packageManager!!) != null) {
            // Bring up gallery to select a photo
            if (fragmentContext == null) {
                activity.startActivityForResult(intent, PICK_PHOTO_CODE)
            } else {
                fragmentContext.startActivityForResult(intent, PICK_PHOTO_CODE)
            }
        }
    }


}