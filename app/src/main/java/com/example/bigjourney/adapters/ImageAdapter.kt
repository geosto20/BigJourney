package com.example.bigjourney.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bigjourney.ImagesActivity
import com.example.bigjourney.R

class ImageAdapter(
    private val context: Context,
    private val imageList: MutableList<Uri>,
    private val onItemClick: (Uri) -> Unit,
    private val onItemLongClick: () -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val selectedItems = mutableSetOf<Uri>() // Λίστα για τις επιλεγμένες εικόνες

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageList[position]
        holder.bind(imageUri, selectedItems.contains(imageUri))

        // Φόρτωση εικόνας
        Glide.with(holder.itemView.context)
            .load(imageUri)
            .into(holder.imageView)

        // Εμφάνιση οπτικής ένδειξης για επιλεγμένα στοιχεία
        holder.itemView.alpha = if (selectedItems.contains(imageUri)) 0.5f else 1.0f

        // Κανονικό κλικ
        holder.itemView.setOnClickListener {
            if (selectedItems.isEmpty()) {
                onItemClick(imageUri) // Άνοιγμα εικόνας
            } else {
                toggleSelection(imageUri) // Εναλλαγή επιλογής
            }
        }

        // Μακροχρόνιο κλικ
        holder.itemView.setOnLongClickListener {
            toggleSelection(imageUri) // Εναλλαγή επιλογής
            onItemLongClick() // Καλούμε το callback
            true
        }
    }

    override fun getItemCount(): Int = imageList.size

    fun getSelectedItems(): List<Uri> = selectedItems.toList() // Επιστρέφουμε τις επιλεγμένες εικόνες

    private fun toggleSelection(imageUri: Uri) {
        if (selectedItems.contains(imageUri)) {
            selectedItems.remove(imageUri)
        } else {
            selectedItems.add(imageUri)
        }
        val index = imageList.indexOf(imageUri)
        if (index != -1) {
            notifyItemChanged(index) // Notify only the affected item
        }

        (context as? ImagesActivity)?.updateActionMode()
    }


    fun deleteSelectedItems() {
        imageList.removeAll(selectedItems)
        selectedItems.clear()
        notifyDataSetChanged() // Refresh only when multiple deletions occur
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val overlay: View = itemView.findViewById(R.id.selectionOverlay)

        fun bind(uri: Uri, isSelected: Boolean) {
            imageView.setImageURI(uri)
            overlay.visibility = if (isSelected) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                onItemClick(uri)
            }

            itemView.setOnLongClickListener {
                toggleSelection(uri)
                onItemLongClick()
                true
            }
        }
    }
}


