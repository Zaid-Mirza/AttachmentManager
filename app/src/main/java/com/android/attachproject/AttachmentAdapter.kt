package com.android.attachproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.attachproject.databinding.RowAttachmentBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.shape.CornerFamily
import com.mirza.attachmentmanager.models.AttachmentDetail



class AttachmentAdapter(private var list: ArrayList<AttachmentDetail>) : RecyclerView.Adapter<AttachmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowAttachmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list, this)
    }

    class ViewHolder(val binding: RowAttachmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(list: ArrayList<AttachmentDetail>, attachmentAdapter: AttachmentAdapter) = with(itemView) {
            val attachmentDetail = list[adapterPosition]
            binding.deleteAttachmentImageView.tag = adapterPosition
           // val bitmap = attachmentDetail.uri?.let { ImageUtils.getCapturedImage(it) }

            binding.attachmentImageView.shapeAppearanceModel = binding.attachmentImageView.shapeAppearanceModel
                    .toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, resources.getDimension(R.dimen.small_margin))
                    .build()

            val requestOptions = RequestOptions()
            requestOptions.format(DecodeFormat.PREFER_ARGB_8888)
            requestOptions.disallowHardwareConfig()

            if(attachmentDetail.mimeType?.contains("pdf") == true) {
                Glide.with(itemView).applyDefaultRequestOptions(requestOptions)
                    .load(R.drawable.ic_pdf).into(binding.attachmentImageView)
            }else{
                if (attachmentDetail.uri != null)
                    Glide.with(itemView).applyDefaultRequestOptions(requestOptions)
                        .load(attachmentDetail.uri).into(binding.attachmentImageView)
                else {
                    Glide.with(itemView).applyDefaultRequestOptions(requestOptions)
                        .load(R.drawable.ic_add).into(binding.attachmentImageView)
                }
            }


            binding.deleteAttachmentImageView.setOnClickListener {
                removeAndUpdate(list, attachmentAdapter)

            }
        }

        private fun removeAndUpdate(
            list: ArrayList<AttachmentDetail>,
            attachmentAdapter: AttachmentAdapter
        ) {
            list.removeAt(adapterPosition)
            attachmentAdapter.notifyDataSetChanged()
        }

    }


    fun updateData(arrayList: ArrayList<AttachmentDetail>) {
        list = arrayList
        notifyDataSetChanged()
    }

    fun getItems(): ArrayList<AttachmentDetail> {
        return list
    }
}