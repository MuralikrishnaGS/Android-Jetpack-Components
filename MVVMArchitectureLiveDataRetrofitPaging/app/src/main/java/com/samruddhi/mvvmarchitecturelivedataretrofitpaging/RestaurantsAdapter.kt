package com.samruddhi.mvvmarchitecturelivedataretrofitpaging

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.samruddhi.mvvmarchitecturelivedataretrofitpaging.model.RestaurantsDataList

class RestaurantsAdapter : RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>() {

    private var restaurantsList = mutableListOf<RestaurantsDataList>()
    private var mContext: Context? = null

    fun setAllRestaurantsNearBy(restaurantsDataList: List<RestaurantsDataList>, context: Context) {
        this.restaurantsList = restaurantsDataList.toMutableList()
        this.mContext = context
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.adapter_restaurants, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurantsDataList = restaurantsList[position]
        val restaurants = restaurantsDataList.restaurant
        holder.name.text = restaurants.name
        holder.timings.text = "Store Timings: " + restaurants.timings
        holder.costForTwo.text = "Avg Cost for Two: ${mContext!!.getString(R.string.rupee_symbol)}${restaurants.average_cost_for_two}"
        Glide.with(holder.itemView.context).load(restaurants.thumb).into(holder.imageView)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var timings: TextView = view.findViewById(R.id.timings)
        var costForTwo: TextView = view.findViewById(R.id.cost_for_two)
        var imageView: ImageView = view.findViewById(R.id.image_view)
    }

    override fun getItemCount(): Int {
        return restaurantsList.size
    }
}