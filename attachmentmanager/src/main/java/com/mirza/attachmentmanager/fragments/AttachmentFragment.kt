package com.mirza.attachmentmanager.fragments


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mirza.attachmentmanager.R


enum class DialogAction {
    GALLERY, CAMERA, FILE
}

class AttachmentFragment(val listener: (DialogAction) -> Unit) : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        retainInstance = true
        return inflater.inflate(R.layout.fragment_attachment, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.layout_attachment_dialog, null)
        val imageLinearLayout = view.findViewById<LinearLayout>(R.id.image_linearLayout)
        val cameraLinearLayout = view.findViewById<LinearLayout>(R.id.camera_linearLayout)
        val fileLinearLayout = view.findViewById<LinearLayout>(R.id.file_linearLayout)
        val cancelImageView = view.findViewById<ImageView>(R.id.cancel_imageView)
        cancelImageView.setOnClickListener { dismiss() }

        imageLinearLayout.setOnClickListener {
            listener(DialogAction.GALLERY)
            dismiss()
        }
        cameraLinearLayout.setOnClickListener {
            listener(DialogAction.CAMERA)
            dismiss()
        }
        fileLinearLayout.setOnClickListener {
            listener(DialogAction.FILE)
            dismiss()
        }

        val dialog = AlertDialog.Builder(activity!!)
                .setView(view).create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }


}
