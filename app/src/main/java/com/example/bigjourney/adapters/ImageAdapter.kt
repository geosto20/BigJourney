package com.example.bigjourney.adapters


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bigjourney.FullScreenImageActivity
import com.example.bigjourney.R

class ImageAdapter(
    private val imageList: List<Pair<String, String>>,
    private val onSelectionChanged: (Boolean) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val selectedImages = mutableSetOf<String>()
    private var isSelectionMode = false

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPhoto)
        val overlay: View = itemView.findViewById(R.id.selectionOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val (imageUrl, imagePath) = imageList[position]

        Glide.with(holder.itemView.context).load(imageUrl).into(holder.imageView)

        // Αν η εικόνα είναι επιλεγμένη, εμφανίζουμε overlay
        holder.overlay.visibility = if (selectedImages.contains(imagePath)) View.VISIBLE else View.GONE

        holder.itemView.setOnLongClickListener {
            toggleSelection(imagePath)
            true
        }

        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                toggleSelection(imagePath)
            } else {
                val intent = Intent(holder.itemView.context, FullScreenImageActivity::class.java).apply {
                    putExtra("imageUrl", imageUrl)
                    putExtra("imagePath", imagePath)
                }
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = imageList.size

    private fun toggleSelection(imagePath: String) {
        if (selectedImages.contains(imagePath)) {
            selectedImages.remove(imagePath)
        } else {
            selectedImages.add(imagePath)
        }

        isSelectionMode = selectedImages.isNotEmpty()
        onSelectionChanged(isSelectionMode)

        notifyDataSetChanged()
    }

    fun getSelectedImages(): List<String> = selectedImages.toList()

    fun clearSelection() {
        selectedImages.clear()
        isSelectionMode = false
        notifyDataSetChanged()
    }
}





