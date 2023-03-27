package com.example.imagesgallery

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImagesGalleryAdapter(
    private val context: Context,
    private var images: ArrayList<ImageFromInternet>,
    private var onScrolledToEnd: () -> Unit
) : RecyclerView.Adapter<ImagesGalleryAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var imageUrls: ImageUrls? = null
        private val thumbView: ImageView = itemView.findViewById(R.id.thumbView)

        init {
            view.setOnClickListener {
                val intent = Intent(context, DetailedImageActivity::class.java)
                imageUrls?.full?.let { urlOfFullImg -> intent.putExtra("imageUrl", urlOfFullImg) }
                view.context.startActivity(intent)
            }
        }

        fun bindView(image: ImageFromInternet) {
            this.imageUrls = image.urls
            val urlThumb = imageUrls!!.thumb
            Glide.with(thumbView)
                .load(urlThumb)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(thumbView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
        ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.feed_item, parent, false))

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bindView(images[position])

        if (position == images.size - 1) {
            onScrolledToEnd()
        }
    }

    override fun getItemCount(): Int = images.size

    fun addImages(newImages: List<ImageFromInternet>?) {
        if (newImages != null) {
            this.images.addAll(newImages)
            notifyDataSetChanged()
        }
    }
}
