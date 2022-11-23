package com.rezaharisz.sayurkah.ui.vegetables

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rezaharisz.sayurkah.data.local.entity.VegetableEntity
import com.rezaharisz.sayurkah.databinding.ItemVegetablesBinding
import com.rezaharisz.sayurkah.helper.DiffCallback

class VegetablesAdapter: PagedListAdapter<VegetableEntity, VegetablesAdapter.ViewHolder>(DiffCallback) {
    class ViewHolder(private val binding: ItemVegetablesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(vegetableEntity: VegetableEntity){
            Glide.with(itemView)
                .load(vegetableEntity.picture)
                .override(180,180)
                .into(binding.ivVegetable)
            binding.tvVegetable.text = vegetableEntity.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = ItemVegetablesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = getItem(position)
        if (itemPosition != null){
            holder.bind(itemPosition)
        }
    }
}