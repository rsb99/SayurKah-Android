package com.rezaharisz.sayurkah.data

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.rezaharisz.sayurkah.data.local.entity.VegetableEntity
import com.rezaharisz.sayurkah.data.local.room.VegetableDao

class VegetablesRepository (private val vegetableDao: VegetableDao) {

    fun getVegetables(): LiveData<PagedList<VegetableEntity>>{
        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .build()

        return LivePagedListBuilder(vegetableDao.getVegetables(), config).build()
    }

}