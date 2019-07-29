package com.mirza.attachmentmanager.fragments


import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.DialogTitle
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.mirza.attachmentmanager.R


enum class DialogAction {
    GALLERY, CAMERA, FILE
}

class AttachmentFragment(val title: String? = null, val optionTextColor: Int? = null, val imagesColor: Int? = null, val listener: (DialogAction) -> Unit) : DialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        retainInstance = true
        return inflater.inflate(R.layout.fragment_attachment, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.layout_attachment_dialog, null)

        arguments?.let {

        }

        val imageLinearLayout = view.findViewById<LinearLayout>(R.id.image_linearLayout)
        val cameraLinearLayout = view.findViewById<LinearLayout>(R.id.camera_linearLayout)
        val fileLinearLayout = view.findViewById<LinearLayout>(R.id.file_linearLayout)
        val cancelImageView = view.findViewById<ImageView>(R.id.cancel_imageView)
        val titleTextView = view.findViewById<TextView>(R.id.title_textView)
        applySettings(view)

        title?.let {
            titleTextView.text = it
        }
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

    private fun applySettings(view: View) {
        val galleryImageView = view.findViewById<ImageView>(R.id.gallary_imageView)
        val cameraImageView = view.findViewById<ImageView>(R.id.camera_imageView)
        val fileImageView = view.findViewById<ImageView>(R.id.file_imageView)

        val galleryTextView = view.findViewById<TextView>(R.id.gallery_textView)
        val cameraTextView = view.findViewById<TextView>(R.id.camera_textView)
        val fileTextView = view.findViewById<TextView>(R.id.file_textView)

        imagesColor?.let {
            galleryImageView.setColorFilter(ContextCompat.getColor(context!!, imagesColor), PorterDuff.Mode.SRC_IN)
            cameraImageView.setColorFilter(ContextCompat.getColor(context!!, imagesColor), PorterDuff.Mode.SRC_IN)
            fileImageView.setColorFilter(ContextCompat.getColor(context!!, imagesColor), PorterDuff.Mode.SRC_IN)
        }
        optionTextColor?.let {
            galleryTextView.setTextColor(ContextCompat.getColor(context!!, optionTextColor))
            cameraTextView.setTextColor(ContextCompat.getColor(context!!, optionTextColor))
            fileTextView.setTextColor(ContextCompat.getColor(context!!, optionTextColor))


        }


    }


}
