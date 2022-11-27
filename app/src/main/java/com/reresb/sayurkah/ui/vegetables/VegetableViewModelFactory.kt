package com.reresb.sayurkah.ui.vegetables

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.reresb.sayurkah.data.VegetablesRepository
import com.reresb.sayurkah.di.Injection

class VegetableViewModelFactory(private val vegetablesRepository: VegetablesRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(VegetablesViewModel::class.java) -> VegetablesViewModel(vegetablesRepository) as T
            else -> throw Throwable("Unknown ViewModel Class" + modelClass.name)
        }
    }

    companion object{
        @Volatile
        private var instance: VegetableViewModelFactory? = null

        fun getInstance(context: Context): VegetableViewModelFactory =
            instance ?: synchronized(this){
                instance ?: VegetableViewModelFactory(Injection.getRepository(context)).apply { instance = this }
            }
    }
}