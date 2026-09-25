package com.example.timetrekbharat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timetrekbharat.R
import com.example.timetrekbharat.databinding.ItemGalleryImageBinding

class ImageGalleryAdapter(
    private val images: List<String>,
    private val onImageClickListener: (String) -> Unit
) : RecyclerView.Adapter<ImageGalleryAdapter.GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemGalleryImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    inner class GalleryViewHolder(private val binding: ItemGalleryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) {
            Glide.with(itemView.context)
                .load(url)
                .placeholder(R.drawable.placeholder_heritage)
                .error(R.drawable.placeholder_heritage)
                .centerCrop()
                .into(binding.ivGalleryThumb)

            itemView.setOnClickListener {
                // Interactive scale animation when tapped
                itemView.animate()
                    .scaleX(0.92f)
                    .scaleY(0.92f)
                    .setDuration(100)
                    .withEndAction {
                        itemView.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start()
                    }
                    .start()

                onImageClickListener(url)
            }
        }
    }
}
