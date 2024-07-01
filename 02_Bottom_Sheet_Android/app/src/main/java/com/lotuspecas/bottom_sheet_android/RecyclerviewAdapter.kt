package com.lotuspecas.bottom_sheet_android

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lotuspecas.bottom_sheet_android.databinding.AdapterCreateNewBinding


class RecyclerviewAdapter : RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder>() {

    private var items = listOf<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // R.layout.adapter_create_new
        val binding = AdapterCreateNewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateList(newList: List<Item>) {
        val diffCallback = MyDiffCallback(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class MyViewHolder(private val binding: AdapterCreateNewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.name.text = item.name
            binding.img.setImageResource(item.image)

            binding.name.setOnClickListener{
                Log.e("TIKLANDI", "isme tiklandi ${item.name}")
            }
            binding.img.setOnClickListener{
                Log.e("TIKLANDI", "resme tiklandi ${item.name}")
            }

        }

    }


    class MyDiffCallback(
        private val oldList: List<Item>, private val newList: List<Item>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].name == newList[newItemPosition].name
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }

}
