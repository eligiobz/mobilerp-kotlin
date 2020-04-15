package com.mobilerp.quimera.mobilerp.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobilerp.quimera.mobilerp.ApiModels.ProductSaleModel
import com.mobilerp.quimera.mobilerp.R
import kotlinx.android.synthetic.main.fragment_finish_sell.view.*
import kotlinx.android.synthetic.main.product_sale_check_row.view.*


/**
 * Created by Eligio Becerra on 04/01/2018.
 * Copyright (C) 2018 Eligio Becerra
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

//class ProductSaleListAdapter(context: Context, itemView: View, price: Double, earning: Double, productName: String, total_items: Int) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
class ProductSaleListAdapter(val itemList: ArrayList<ProductSaleModel>) : RecyclerView.Adapter<ProductSaleListAdapter.UserViewHolder>() {

    fun updateItems(newItems: List<ProductSaleModel>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.product_sale_check_row, parent, false)
    )

    override fun onBindViewHolder(holder: ProductSaleListAdapter.UserViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.itemName
        private val itemTotal = itemView.itemTotal
        private val totalSale = itemView.totalSale
        //private val removebtn = itemView.remove_btn

        fun bind(data: ProductSaleModel) {
            name.text = data.name
            itemTotal.text = data.units.toString()
            totalSale.text = data.total_earning.toString()
        }
    }
}