package com.example.myapps

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MainAdapter: RecyclerView.Adapter<CustomViewHolder>() {

    //number of items
    override fun getItemCount(): Int {
        return 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val
    }

    override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
        TODO("Not yet implemented")
    }
}

class CustomViewHolder(v: View): RecyclerView.ViewHolder(v){

}