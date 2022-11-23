package com.rezaharisz.sayurkah.helper

import androidx.recyclerview.widget.DiffUtil
import com.rezaharisz.sayurkah.data.local.entity.VegetableEntity

object DiffCallback: DiffUtil.ItemCallback<VegetableEntity>() {
    override fun areItemsTheSame(oldItem: VegetableEntity, newItem: VegetableEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: VegetableEntity, newItem: VegetableEntity): Boolean {
        return oldItem.name == newItem.name
    }
}