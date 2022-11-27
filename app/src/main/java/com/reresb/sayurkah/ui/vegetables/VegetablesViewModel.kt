package com.reresb.sayurkah.ui.vegetables

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.reresb.sayurkah.data.VegetablesRepository
import com.reresb.sayurkah.data.local.entity.VegetableEntity

class VegetablesViewModel(private val repository: VegetablesRepository) : ViewModel() {

    fun getVegetables(): LiveData<PagedList<VegetableEntity>>  = repository.getVegetables()

}