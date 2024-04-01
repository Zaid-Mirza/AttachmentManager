package com.mirza.attachmentmanager.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mirza.attachmentmanager.R
import com.mirza.attachmentmanager.managers.AttachmentManager
import com.mirza.attachmentmanager.managers.HideOption
import android.graphics.PorterDuffColorFilter

import android.graphics.drawable.Drawable
import com.google.android.material.button.MaterialButton


class AttachmentBottomSheet(
    var title: String? = null,
    private val optionTextColor: Int? = null,
    private val imagesColor: Int? = null,
    private val hideOption: HideOption?,
    val listener: (DialogAction) -> Unit
) : BottomSheetDialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.layout_attachment_sheet, container, false)

        val titleTextView = view.findViewById<TextView>(R.id.title_textView)
        val imageTextView = view.findViewById<TextView>(R.id.gallery_textView)
        val cameraTextView = view.findViewById<TextView>(R.id.camera_textView)
        val fileTextView = view.findViewById<TextView>(R.id.file_textView)
        val cancelMaterialButton = view.findViewById<MaterialButton>(R.id.cancel_materialButton)

        setTextViewDrawableColor(imageTextView, imagesColor, optionTextColor)
        setTextViewDrawableColor(cameraTextView, imagesColor, optionTextColor)
        setTextViewDrawableColor(fileTextView, imagesColor, optionTextColor)

        when (hideOption) {
            HideOption.GALLERY -> {
                imageTextView.visibility = GONE
            }
            HideOption.CAMERA -> {
                cameraTextView.visibility = GONE
            }
            HideOption.DOCUMENT -> {
                fileTextView.visibility = GONE
            }
            else->{}
        }
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
        cancelMaterialButton.setOnClickListener { dismiss() }
        return view
    }


    private fun setTextViewDrawableColor(textView: TextView, imageColor: Int?, textColor: Int?) {
        imageColor?.let {
            for (drawable in textView.compoundDrawablesRelative) {
                if (drawable != null) {
                    drawable.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(textView.context, imageColor),
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }
        }

        textColor?.let {
            textView.setTextColor(ContextCompat.getColor(context!!, textColor))
        }

    }


}