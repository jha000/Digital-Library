package com.wbsl.digitallibraray.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import com.wbsl.digitallibraray.Data.SliderData
import com.wbsl.digitallibraray.R

class SliderAdapter(private val context: Context, private val sliderDataArrayList: ArrayList<SliderData>) :
    SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterViewHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.slider_layout, parent, false)
        return SliderAdapterViewHolder(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterViewHolder, position: Int) {
        val sliderItem = sliderDataArrayList[position]

        Glide.with(viewHolder.itemView)
            .load(sliderItem.imgUrl)
            .fitCenter()
            .into(viewHolder.imageViewBackground)
    }

    override fun getCount(): Int {
        return sliderDataArrayList.size
    }

    inner class SliderAdapterViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {

        val imageViewBackground: ImageView = itemView.findViewById(R.id.myimage)
    }
}

