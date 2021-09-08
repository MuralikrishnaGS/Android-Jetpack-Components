package com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.databinding.AdapterRestaurantsBinding
import com.samruddhi.mvvmarchitecturedatabindcoroutinesretrofit.model.RestaurantsDataList

class RestaurantsAdapter : RecyclerView.Adapter<MainViewHolder>() {

    var restaurantsList = mutableListOf<RestaurantsDataList>()
    var mContext: Context? = null

    fun setAllRestaurantsNearBy(restaurantsDataList: List<RestaurantsDataList>, context: Context) {
        this.restaurantsList = restaurantsDataList.toMutableList()
        this.mContext = context
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterRestaurantsBinding.inflate(inflater, parent, false)
        return MainViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val restaurantsDataList = restaurantsList[position]
        val restaurants = restaurantsDataList.restaurant
        holder.binding.name.text = restaurants.name
        holder.binding.timings.text = "Store Timings: " + restaurants.timings
        holder.binding.costForTwo.text = "Avg Cost for Two: ${mContext!!.getString(R.string.rupee_symbol)}${restaurants.average_cost_for_two}"
        Glide.with(holder.itemView.context).load(restaurants.thumb).into(holder.binding.imageView)
    }

    override fun getItemCount(): Int {
        return restaurantsList.size
    }
}

class MainViewHolder(val binding: AdapterRestaurantsBinding) : RecyclerView.ViewHolder(binding.root) {

}