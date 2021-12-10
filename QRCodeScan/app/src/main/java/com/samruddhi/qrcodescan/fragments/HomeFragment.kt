package com.samruddhi.qrcodescan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.samruddhi.qrcodescan.R
import com.samruddhi.qrcodescan.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeFragmentBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return homeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeFragmentBinding.readBarCode.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainFragment_to_permissionsFragment)
        }
        homeFragmentBinding.readHistory.setOnClickListener {
            view.findNavController().navigate(R.id.action_mainFragment_to_scanHistoryFragment)
        }
    }
}