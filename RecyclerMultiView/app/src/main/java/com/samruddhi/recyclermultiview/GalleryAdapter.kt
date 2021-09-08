package com.samruddhi.recyclermultiview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class GalleryAdapter(var galleryItemsList: ArrayList<GalleryItem>, var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == GALLERY_SMALL_IMAGE) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_small_photo, parent, false)
            PhotoSmallViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_large_photo, parent, false)
            PhotoLargeViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val photoGalleryItem = galleryItemsList[position]
        if (holder is PhotoSmallViewHolder) {
            holder.ivPhotoSmall.setImageResource(photoGalleryItem.intPhoto)
        }
        if (holder is PhotoLargeViewHolder) {
            holder.ivPhotoLarge.setImageResource(photoGalleryItem.intPhoto)
        }
    }

    override fun getItemCount(): Int {
        return galleryItemsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (galleryItemsList[position].size == "big") {
            GALLERY_LARGE_IMAGE
        } else {
            GALLERY_SMALL_IMAGE
        }
    }

    internal inner class PhotoSmallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivPhotoSmall: ImageView = itemView.findViewById(R.id.img_small_photo)
    }

    internal inner class PhotoLargeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivPhotoLarge: ImageView = itemView.findViewById(R.id.img_large_photo)
    }

    companion object {
        private const val GALLERY_LARGE_IMAGE = 0
        private const val GALLERY_SMALL_IMAGE = 1
    }
}