package com.link_statu.stickers.features.sticker.view.adapter

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.link_statu.stickers.R
import com.link_statu.stickers.core.extensions.others.inflate
import com.link_statu.stickers.databinding.ItemStickimageBinding
import com.link_statu.stickers.features.sticker.models.StickerView
import com.link_statu.stickers.features.sticker.view.StickersPreviewDialog

class StickersDetailsAdapter :
    ListAdapter<StickerView, StickersDetailsAdapter.ViewHolder>(StickersDiffCallback()) {

    lateinit var supportFragmentManager: FragmentManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            parent.inflate(R.layout.item_stickimage)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sticker = getItem(position)
        holder.bind(sticker, supportFragmentManager)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemStickimageBinding.bind(itemView)

        fun bind(newView: StickerView, supportFragmentManager: FragmentManager) {
            binding.ivSticker.load(newView.imageFile)
            itemView.setOnClickListener {
                StickersPreviewDialog(
                    newView.imageFile
                ).show(supportFragmentManager, "StickersPreviewDialog")
            }
        }
    }

    class StickersDiffCallback : DiffUtil.ItemCallback<StickerView>() {
        override fun areItemsTheSame(oldItem: StickerView, newItem: StickerView): Boolean {
            return oldItem.imageFile == newItem.imageFile
        }

        override fun areContentsTheSame(oldItem: StickerView, newItem: StickerView): Boolean = false
    }
}