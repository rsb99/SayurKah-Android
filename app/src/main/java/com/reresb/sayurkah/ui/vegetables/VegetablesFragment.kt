package com.reresb.sayurkah.ui.vegetables

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.reresb.sayurkah.data.local.entity.VegetableEntity
import com.reresb.sayurkah.databinding.FragmentVegetablesBinding

class VegetablesFragment : Fragment() {

    private var _binding: FragmentVegetablesBinding? = null
    private val binding get() = _binding!!
    private lateinit var vegetablesViewModel: VegetablesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVegetablesBinding.inflate(inflater, container, false)

        with(binding.rvVegetables){
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }

        val factory = context?.let { VegetableViewModelFactory.getInstance(it) }
        vegetablesViewModel = factory?.let { ViewModelProvider(this, it) }!![VegetablesViewModel::class.java]

        vegetablesViewModel.getVegetables().observe(viewLifecycleOwner) {
            if (it != null) {
                setAdapter(it)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter(data: PagedList<VegetableEntity>){
        val adapter = VegetablesAdapter()

        adapter.submitList(data)
        adapter.notifyDataSetChanged()
        binding.rvVegetables.adapter = adapter
    }

}