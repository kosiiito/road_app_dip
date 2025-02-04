package com.example.road_app_dip.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.road_app_dip.R
import com.example.road_app_dip.models.Post

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val postCaption: TextView = itemView.findViewById(R.id.postCaption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.postCaption.text = post.description

        val decodedBitmap = convertBufferToBitmap(post.image?.data)

        if (decodedBitmap != null) {
            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(decodedBitmap)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.postImage)
        } else {
            holder.postImage.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    override fun getItemCount(): Int = posts.size

    private fun convertBufferToBitmap(buffer: List<Int>?): Bitmap? {
        if (buffer.isNullOrEmpty()) return null
        return try {
            val byteArray = buffer.map { it.toByte() }.toByteArray()
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
