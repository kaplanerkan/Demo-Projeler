package com.lotuspecas.captureandpickimageandroid

import android.graphics.BitmapFactory
import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lotuspecas.captureandpickimageandroid.databinding.LayoutImageAdapterBinding

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {

    private var selectedImagePath = listOf<ImageModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LayoutImageAdapterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(selectedImagePath[position].path)
    }


    override fun getItemCount(): Int {
        return selectedImagePath.size
    }


    fun updateList(newList: List<ImageModel>) {
        val diffCallback = MyDiffCallback(selectedImagePath, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        selectedImagePath = newList
        diffResult.dispatchUpdatesTo(this)
    }


    class MyViewHolder(private val binding: LayoutImageAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemPath: String) {

            binding.imgSelected.setImageBitmap(BitmapFactory.decodeFile(itemPath))
            binding.tvSelected.text = itemPath.split("/").last().toString()
        }
    }


    class MyDiffCallback(
        private val oldList: List<ImageModel>, private val newList: List<ImageModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].path == newList[newItemPosition].path
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }

}