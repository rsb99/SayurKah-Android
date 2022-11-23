package com.rezaharisz.sayurkah.ui.vegetables

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.rezaharisz.sayurkah.data.local.entity.VegetableEntity
import com.rezaharisz.sayurkah.databinding.FragmentVegetablesBinding

class VegetablesFragment : Fragment() {

    private var _binding: FragmentVegetablesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVegetablesBinding.inflate(inflater, container, false)

        with(binding.rvVegetables){
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }

        val vegetablesViewModel = ViewModelProvider(this)[VegetablesViewModel::class.java]

        val data = vegetablesViewModel.getVegetables().value
        if (data != null) {
            setAdapter(data)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAdapter(data: PagedList<VegetableEntity>){
        val adapter = VegetablesAdapter()

        adapter.submitList(data)
        adapter.notifyDataSetChanged()
        binding.rvVegetables.adapter = adapter
    }

}