package com.mobilerp.quimera.mobilerp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobilerp.quimera.mobilerp.ApiModels.SalesItemModel
import com.mobilerp.quimera.mobilerp.R
import kotlinx.android.synthetic.main.product_sale_check_row.view.*

class SalesItemAdapter(val itemList: ArrayList<SalesItemModel>, val clickListener: (SalesItemModel) -> Unit) : RecyclerView.Adapter<SalesItemAdapter.UserViewHolder>() {
//class SalesItemAdapter (val itemList : ArrayList<SalesItemModel>) : RecyclerView.Adapter<SalesItemAdapter.UserViewHolder> (){

    fun updateItems(newItems: ArrayList<SalesItemModel>, is_new: Boolean = false) {
        if (is_new) {
        itemList.clear()
        itemList.addAll(newItems)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.product_sale_check_row, parent, false)
    )

    override fun onBindViewHolder(holder: SalesItemAdapter.UserViewHolder, position: Int) {
        holder.bind(itemList[position], clickListener)
//            holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.itemName
        private val itemPrice = itemView.itemPrice
        private val units = itemView.units
        private val totalSale = itemView.itemTotal

        fun bind(data: SalesItemModel, clickListener: (SalesItemModel) -> Unit) {
//            fun bind(data: SalesItemModel){
            name.text = data.name
            itemPrice.text = data.price.toString()
            units.text = data.units.toString()
            val ts = data.price * data.units
            totalSale.text = ts.toString()
            itemView.setOnClickListener { clickListener(data) }
        }
    }
}