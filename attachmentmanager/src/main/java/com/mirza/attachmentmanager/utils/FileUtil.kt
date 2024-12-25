package com.mirza.attachmentmanager.utils

//import okhttp3.MediaType
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import java.io.*


object FileUtil {

    const val DOCUMENTS_DIR = "documents"

    fun getFileDisplayName(fileUri: Uri, activity: Context, file: File?): String {
        var displayName: String = "File_" + System.currentTimeMillis()
        if (fileUri.toString().startsWith("content://")) {
            var cursor: Cursor? = null
            try {
                cursor = activity.contentResolver.query(fileUri, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    displayName =
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        } else if (fileUri.toString().startsWith("file://") && file != null) {
            displayName = file.name
        }
        return displayName
    }

    fun getMimeType(uri: Uri, context: Context): String? {
        var mimeType: String? = null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr = context.contentResolver
            mimeType = cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase()
            )
        }
        return mimeType
    }

    fun generateFileName(name: String?, directory: File): File? {
        var name1 = name

        var file = File(directory, name1)


        if (file.exists()) {
            var fileName: String = name1!!
            var extension = ""
            val dotIndex = name1.lastIndexOf('.')
            if (dotIndex > 0) {
                fileName = name1.substring(0, dotIndex)
                extension = name1.substring(dotIndex)
            }

            var index = 0

            while (file.exists()) {
                index++
                name1 = "$fileName($index)$extension"
                file = File(directory, name1)
            }
        }

        try {
            if (!file.createNewFile()) {
                return null
            }
        } catch (e: IOException) {

            return null
        }


        return file
    }

    var mimeTypes = arrayOf(
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .ppt & .pptx
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/pdf"
    )

    @SuppressLint("NewApi")
    fun getPath(uri1: Uri, context: Context): String? {
        var uri = uri1
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isExternalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri)
            } else if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else if ("home".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory()
                        .toString() + "/documents/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {

                var id = DocumentsContract.getDocumentId(uri)

                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4)
                }


                val contentUriPrefixesToTry =
                    arrayOf(
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads",
                        "content://downloads/all_downloads"
                    )

                for (contentUriPrefix in contentUriPrefixesToTry) {
                    Log.e("DASHT", id.toString())
                    var contentUri: Uri? = null
                    if (id != null && !id.startsWith("msf:")) {
                        contentUri = ContentUris.withAppendedId(
                            Uri.parse(contentUriPrefix),
                            java.lang.Long.valueOf(id)
                        )
                    } else {
                        contentUri = uri
                    }

                    try {
                        val path = getDataColumn(context, contentUri, null, null)
                        if (path != null) {
                            return path
                        }
                    } catch (e: Exception) {
                    }

                }


                return copyFileToAppCache(uri, context)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }


                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return contentUri?.let { getDataColumn(context, it, selection, selectionArgs) }
                    ?: return copyFileToAppCache(uri, context)
            }
            // MediaProvider
            // DownloadsProvider
            // ExternalStorageProvider
            else if ("content".equals(uri.scheme, ignoreCase = true)) {

                // Return the remote address
                return if (isGooglePhotosUri(uri)) {
                    uri.lastPathSegment
                } else if (isGoogleocumentUri(uri)) {
                    return getDriveFilePath(uri, context)
                } else getDataColumn(context, uri, null, null)

            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
        } else if ("content".equals((uri.scheme))) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.getPath();
        }

        return null
    }

    private fun copyFileToAppCache(uri: Uri, context: Context): String? {
        // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
        val fileName = getFileDisplayName(uri, context, null)
        val cacheDir = getDocumentCacheDir(context)
        val file = generateFileName(fileName, cacheDir)
        var destinationPath: String? = null
        if (file != null) {
            destinationPath = file.getAbsolutePath()
            saveFileFromUri(context, uri, destinationPath)
        }

        return destinationPath
    }


    fun saveBitmapToFile(context: Context, file: File): File? {
        return try {

            // BitmapFactory options to downsize the image
            val o: BitmapFactory.Options = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image
            var inputStream = context.contentResolver.openInputStream(file.toUri())
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream?.close()

            // The new size we want to scale to
            val REQUIRED_SIZE = 75

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            val o2: BitmapFactory.Options = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = context.contentResolver.openInputStream(file.toUri())
            val selectedBitmap: Bitmap? = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream?.close()

            // here i override the original image file
            file.createNewFile()
            val outputStream = context.contentResolver.openOutputStream(file.toUri())
            selectedBitmap?.compress(Bitmap.CompressFormat.JPEG, 20, outputStream!!)
            outputStream?.close()
            file
        } catch (e: java.lang.Exception) {
            null
        }
    }

    private fun getDriveFilePath(uri: Uri, context: Context): String {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(context.cacheDir, name)
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable = inputStream!!.available()
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)

            val buffers = ByteArray(bufferSize)
            while ((inputStream.read(buffers).also { read = it }) != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            //Log.e("Exception", e.message)
        }

        return file.path
    }

    private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String) {
        var `is`: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            `is` = context.contentResolver.openInputStream(uri)
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            `is`!!.read(buf)
            do {
                bos.write(buf)
            } while (`is`.read(buf) != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    private fun getDocumentCacheDir(context: Context): File {
        val dir = File(context.cacheDir, DOCUMENTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    private fun isGoogleocumentUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority
    }

//    fun getMultiParts(attachments: ArrayList<AttachmentDetail>, context: Context, paraName: String): ArrayList<MultipartBody.Part> {
//        val list = ArrayList<MultipartBody.Part>()
//
//        attachments.forEach { attachment ->
//            attachment.uri?.let { uri ->
//                val file: File? = getFileFromUri(uri, context);
//
//                file?.let {
//                    val mimeType = getMimeType(uri, context)
//                    mimeType?.let {
//                        val requestBody = RequestBody.create(MediaType.parse(mimeType), file)
//                        val part = MultipartBody.Part.createFormData(paraName + System.currentTimeMillis(), file.name, requestBody)
//                        list.add(part)
//                    }
//                }
//
//            }
//
//        }
//
//        return list
//    }

    private fun getFileFromUri(uri: Uri, context: Context): File? {
        val path = null// getPath(uri, context)
        var file: File? = null
        if (path == null) {
            file = File((uri.toString()))
        } else {
            // file = File(path)
        }
        return file
    }

    private fun getDataColumn(
        context: Context, uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null;
        val column = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs,
                null
            );
            if (cursor != null && cursor.moveToFirst()) {

                val columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        } catch (e: Exception) {
            val ss = ""
        } finally {
            cursor?.close();
        }
        return null;
    }

    fun getFileSize(uri: Uri, context: Context): Long {


//        val file = getFileFromUri(uri, context)
//        var fileSize: Long? = null
//        // Get length of file in bytes
//        file?.let {
//            fileSize = file.length();
//
//            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
////            fileSize = fileSize?.let {
////                (it / 1024) / 1024;
////
////            }
//        }


        // return fileSize ?: 0;
        return uri.length(context.contentResolver)
    }

    private fun Uri.length(contentResolver: ContentResolver)
            : Long {

        val assetFileDescriptor = try {
            contentResolver.openAssetFileDescriptor(this, "r")
        } catch (e: FileNotFoundException) {
            null
        }
        // uses ParcelFileDescriptor#getStatSize underneath if failed
        val length = assetFileDescriptor?.use { it.length } ?: -1L
        if (length != -1L) {
            return length
        }

        // if "content://" uri scheme, try contentResolver table
        if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            return contentResolver.query(this, arrayOf(OpenableColumns.SIZE), null, null, null)
                ?.use { cursor ->
                    // maybe shouldn't trust ContentResolver for size: https://stackoverflow.com/questions/48302972/content-resolver-returns-wrong-size
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex == -1) {
                        return@use -1L
                    }
                    cursor.moveToFirst()
                    return try {
                        cursor.getLong(sizeIndex)
                    } catch (_: Throwable) {
                        -1L
                    }
                } ?: -1L
        } else {
            return -1L
        }
    }

}
