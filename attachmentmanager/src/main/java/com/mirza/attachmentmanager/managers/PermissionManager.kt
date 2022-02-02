package com.mirza.attachmentmanager.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.ArrayList
import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import com.mirza.attachmentmanager.utils.AttachmentUtil
import java.lang.Exception


object PermissionManager {




    val storagePermissionList: ArrayList<String>
        get() {
            val requiredPermissions = ArrayList<String>()
            requiredPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            return requiredPermissions
        }

    val cameraPermissionList: ArrayList<String>
        get() {
            val requiredPermissions = ArrayList<String>()
            requiredPermissions.add(Manifest.permission.CAMERA)

            return requiredPermissions
        }


    private fun checkForPermission(permissions: ArrayList<String>, context: Context?): ArrayList<String> {
        val missingPermission = ArrayList<String>()
        for (permission in permissions) {

            if (ContextCompat.checkSelfPermission(context!!, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermission.add(permission)
            }
        }
        return missingPermission

    }

    fun checkAndroidVersionAndPermission(appCompatActivity: AppCompatActivity?, fragment: Fragment?, pRequiredPermissions: ArrayList<String>, permissionCode: Int): Boolean {
        if(SDK_INT >= Build.VERSION_CODES.R) {
            if(Environment.isExternalStorageManager()){
                return true
            }
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(java.lang.String.format("package:%s", if(fragment == null) appCompatActivity?.packageName else fragment.requireActivity().packageName))
                if(fragment != null)
                    fragment.startActivityForResult(intent, AttachmentUtil.STORAGE_CODE)
                else{
                    appCompatActivity?.startActivityForResult(intent, AttachmentUtil.STORAGE_CODE)
                }
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                if(fragment != null)
                    fragment.startActivityForResult(intent, AttachmentUtil.STORAGE_CODE)
                else{
                    appCompatActivity?.startActivityForResult(intent, AttachmentUtil.STORAGE_CODE)
                }
            }
            return false
        }else{
            // below 11
            return checkForPermissions(appCompatActivity, fragment, pRequiredPermissions, permissionCode)
        }
    }
    fun checkForPermissions(appCompatActivity: AppCompatActivity?, fragment: Fragment?, pRequiredPermissions: ArrayList<String>, permissionCode: Int): Boolean {
        var requiredPermissions = pRequiredPermissions

        if (fragment != null) {
            requiredPermissions = checkForPermission(requiredPermissions, fragment.context)
        } else {
            requiredPermissions = checkForPermission(requiredPermissions, appCompatActivity)
        }
        if (requiredPermissions.size > 0) {
            askForPermission(requiredPermissions, appCompatActivity, fragment, permissionCode)
        } else {
            return true
        }
        return false

    }

    private fun askForPermission(requiredPermission: ArrayList<String>, appCompatActivity: AppCompatActivity?, fragment: Fragment?, permissionCode: Int) {


          if (fragment == null) {
              ActivityCompat.requestPermissions(
                  appCompatActivity!!,
                  requiredPermission.toTypedArray(),
                  permissionCode
              )
          } else {
              fragment.requestPermissions(requiredPermission.toTypedArray(), permissionCode)
          }

      }




}