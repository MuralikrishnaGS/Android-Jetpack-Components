package com.samruddhi.qrcodescan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.samruddhi.qrcodescan.R
import com.samruddhi.qrcodescan.adapter.BarcodeHistoryRecyclerAdapter
import com.samruddhi.qrcodescan.databinding.FragmentScanHistoryBinding
import com.samruddhi.qrcodescan.model.BarCode
import com.samruddhi.qrcodescan.utils.RecyclerTouchListener


class ScanHistoryFragment : Fragment(), RecyclerTouchListener {

    private lateinit var scanHistoryFragmentBinding: FragmentScanHistoryBinding

    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(
            ScanHistoryFragmentViewModel::class.java
        )
    }

    private val adapter = BarcodeHistoryRecyclerAdapter()
    private var localList: List<BarCode>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        scanHistoryFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_scan_history, container, false)
        val layoutManager = LinearLayoutManager(requireContext())
        scanHistoryFragmentBinding.recyclerView.adapter = adapter
        scanHistoryFragmentBinding.recyclerView.layoutManager = layoutManager

        return scanHistoryFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.allHistoryItems.observe(viewLifecycleOwner, { barcodesList ->
            barcodesList?.let {
                adapter.setAllBarCodeList(it, requireContext(), this)
                localList = it
            }
            if (localList != null && localList!!.isNotEmpty()) {
                scanHistoryFragmentBinding.linearLayoutNothing.visibility = View.GONE
            }
        })
    }

    override fun onClick(view: View, position: Int) {
        val barCode = localList!![position]
        viewModel.deleteBarCode(barcode = barCode)
    }
}