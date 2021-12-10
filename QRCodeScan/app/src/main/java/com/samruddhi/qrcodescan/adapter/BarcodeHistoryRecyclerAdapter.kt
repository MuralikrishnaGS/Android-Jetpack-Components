package com.samruddhi.qrcodescan.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.samruddhi.qrcodescan.R
import com.samruddhi.qrcodescan.fragments.ScanHistoryFragment
import com.samruddhi.qrcodescan.model.BarCode
import com.samruddhi.qrcodescan.utils.RecyclerTouchListener
import kotlinx.android.synthetic.main.barcode_history_item.view.*

class BarcodeHistoryRecyclerAdapter :
    RecyclerView.Adapter<BarcodeHistoryRecyclerAdapter.HistoryViewHolder>() {

    private var barCodeList = mutableListOf<BarCode>()
    private var mContext: Context? = null
    private var recyclerTouchListener: RecyclerTouchListener? = null

    fun setAllBarCodeList(barCodeList: List<BarCode>, context: Context, recyclerTouchListener: RecyclerTouchListener) {
        this.barCodeList = barCodeList.toMutableList()
        this.mContext = context
        this.recyclerTouchListener = recyclerTouchListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.barcode_history_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = barCodeList[position]
        holder.textViewDate.text = item.timeTaken.toString()
        holder.textViewType.text = item.type.toString()
        holder.textViewValue.text = item.value

        holder.deleteIcon.setOnClickListener(View.OnClickListener {
            recyclerTouchListener!!.onClick(
                holder.itemView,
                position
            )
        })
    }


    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deleteIcon = itemView.deleteIcon
        val textViewType = itemView.textViewType
        val textViewValue = itemView.textViewValue
        val textViewDate = itemView.textViewDate
    }

    override fun getItemCount(): Int {
        return barCodeList.size
    }
}