package com.reresb.sayurkah.data

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.reresb.sayurkah.data.local.entity.VegetableEntity
import com.reresb.sayurkah.data.local.room.VegetableDao

class VegetablesRepository private constructor(private val vegetableDao: VegetableDao) {

    companion object{
        @Volatile
        private var instance: VegetablesRepository? = null

        fun getInstance(vegetableDao: VegetableDao): VegetablesRepository =
            instance ?: synchronized(this){
                instance ?: VegetablesRepository(vegetableDao).apply { instance = this }
            }
    }

    fun getVegetables(): LiveData<PagedList<VegetableEntity>>{
        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .build()

        return LivePagedListBuilder(vegetableDao.getVegetables(), config).build()
    }

}