package com.mirza.attachmentmanager.managers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.ArrayList

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

    fun checkForPermissions(appCompatActivity: AppCompatActivity?, fragment: Fragment?, pRequiredPermissions: ArrayList<String>, permissionCode: Int): Boolean {
        var requiredPermissions = pRequiredPermissions

        if (appCompatActivity != null) {
            requiredPermissions = checkForPermission(requiredPermissions, appCompatActivity)
        } else {
            requiredPermissions = checkForPermission(requiredPermissions, fragment?.context)
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
            ActivityCompat.requestPermissions(appCompatActivity!!, requiredPermission.toTypedArray(), permissionCode)
        } else {
            fragment.requestPermissions(requiredPermission.toTypedArray(), permissionCode)
        }


    }

}