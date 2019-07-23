package com.mirza.attachmentmanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mirza.attachmentmanager.R

class AttachmentBottomSheet(var title: String? = null, val listener: (DialogAction) -> Unit) : BottomSheetDialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater.inflate(R.layout.layout_attachment_sheet, container, false)

        val titleTextView = view.findViewById<TextView>(R.id.title_textView)
        val imageTextView = view.findViewById<TextView>(R.id.gallery_textView)
        val cameraTextView = view.findViewById<TextView>(R.id.camera_textView)
        val fileTextView = view.findViewById<TextView>(R.id.file_textView)
        title?.let {
            titleTextView.text = it
        }


        imageTextView.setOnClickListener {
            listener(DialogAction.GALLERY)
            dismiss()
        }
        cameraTextView.setOnClickListener {
            listener(DialogAction.CAMERA)
            dismiss()
        }
        fileTextView.setOnClickListener {
            listener(DialogAction.FILE)
            dismiss()
        }
        return view
    }


}