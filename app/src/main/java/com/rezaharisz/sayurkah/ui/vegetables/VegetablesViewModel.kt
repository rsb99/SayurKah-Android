package com.rezaharisz.sayurkah.ui.vegetables

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.rezaharisz.sayurkah.data.VegetablesRepository
import com.rezaharisz.sayurkah.data.local.entity.VegetableEntity

class VegetablesViewModel(private val repository: VegetablesRepository) : ViewModel() {

    fun getVegetables(): LiveData<PagedList<VegetableEntity>> = repository.getVegetables()

}