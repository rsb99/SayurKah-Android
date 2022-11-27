package com.reresb.sayurkah.di

import android.content.Context
import com.reresb.sayurkah.data.VegetablesRepository
import com.reresb.sayurkah.data.local.room.VegetableDatabase

object Injection {
    fun getRepository(context: Context): VegetablesRepository{
        val db = VegetableDatabase.getInstance(context)

        return VegetablesRepository.getInstance(db.vegetableDao())
    }
}