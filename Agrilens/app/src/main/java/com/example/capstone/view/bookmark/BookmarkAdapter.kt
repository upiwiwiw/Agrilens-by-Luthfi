package com.example.capstone.view.bookmark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone.R

class BookmarkAdapter(
    private val bookmarks: MutableList<Bookmark>
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    private var onItemClickListener: ((Bookmark) -> Unit)? = null
    private var onDeleteClickListener: ((Bookmark) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bookmark, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val bookmark = bookmarks[position]

        // Set title and diagnosis text
        holder.title.text = bookmark.title
        holder.diagnose.text = bookmark.diagnose

        // Load image using Glide
        Glide.with(holder.thumbnail.context)
            .load(bookmark.thumbnail)
            .placeholder(R.drawable.ic_placeholder_image)
            .error(R.drawable.ic_placeholder_image)
            .into(holder.thumbnail)

        // Set item click listener
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(bookmark)
        }

        // Set delete button click listener
        holder.deleteButton.setOnClickListener {
            onDeleteClickListener?.invoke(bookmark)
        }
    }

    override fun getItemCount(): Int = bookmarks.size

    // Set listener for item click
    fun setOnItemClickListener(listener: (Bookmark) -> Unit) {
        onItemClickListener = listener
    }

    // Set listener for delete click
    fun setOnDeleteClickListener(listener: (Bookmark) -> Unit) {
        onDeleteClickListener = listener
    }

    // Method untuk menghapus bookmark dari daftar lokal dengan animasi
    fun removeBookmark(bookmark: Bookmark) {
        val position = bookmarks.indexOf(bookmark)
        if (position >= 0) {
            bookmarks.removeAt(position)
            notifyItemRemoved(position)

            // Perbarui elemen yang ada di bawah item yang dihapus
            notifyItemRangeChanged(position, bookmarks.size - position)
        }
    }

    // ViewHolder to bind data to UI components
    inner class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_bookmark_title)
        val diagnose: TextView = itemView.findViewById(R.id.tv_bookmark_diagnose)
        val thumbnail: ImageView = itemView.findViewById(R.id.img_bookmark_thumbnail)
        val deleteButton: ImageView = itemView.findViewById(R.id.btn_delete_bookmark)
    }
}