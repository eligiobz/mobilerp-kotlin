package com.mobilerp.quimera.mobilerp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mobilerp.quimera.mobilerp.ApiModels.ProductModel
import com.mobilerp.quimera.mobilerp.R

//depleted_items_layout
//products_multi_list_header
//depleted_products_list_header

/**
 * Created by Eligio Becerra on 04/01/2018.
 * Copyright (C) 2017 Eligio Becerra
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

class ProductListAdapter(context: Context, private val productList: ArrayList<ProductModel>, layout:
Int) : ArrayAdapter<ProductModel>(context, layout, productList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context!!.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rowView: View

        when (!productList[position].isGroupHeader){
            true -> {
                when (productList[position].date){
                    null -> {
                        rowView = inflater.inflate(R.layout.product_row, parent, false)

                        val itemNameView = rowView.findViewById<TextView>(R.id.itemName)
                        val itemPriceView = rowView.findViewById<TextView>(R.id.itemPrice)
                        val itemTotalView = rowView.findViewById<TextView>(R.id.itemTotal)

                        itemNameView.text = productList[position].name
                        itemPriceView.text = productList[position].price.toString()
                        itemTotalView.text = productList[position].units.toString()
                    }
                    else -> {

                        rowView = inflater.inflate(R.layout.depleted_items_layout, parent, false)

                        val itemNameView = rowView.findViewById<TextView>(R.id.itemName)
                        val itemLastSaleDate = rowView.findViewById<TextView>(R.id.itemLastSaleDate)

                        itemNameView.text = productList[position].name
                        itemLastSaleDate.text = productList[position].date.toString()
                    }
                }
            }
            false -> {
                when (productList[position].name){
                    "title" -> {
                        rowView = inflater.inflate(R.layout.products_multi_list_header, parent, false)
                        val itemTitleView = rowView.findViewById<TextView>(R.id.itemName)
                        val itemPriceView = rowView.findViewById<TextView>(R.id.itemPrice)
                        val itemTotalView = rowView.findViewById<TextView>(R.id.itemTotal)
                        itemTitleView.setText(R.string.item_name)
                        itemPriceView.setText(R.string.item_price)
                        itemTotalView.setText(R.string.item_total)
                    }
                    else -> {
                        rowView = inflater.inflate(R.layout.depleted_products_list_header, parent, false)
                        val itemTitleView = rowView.findViewById<TextView>(R.id.itemName)
                        val itemLastSaleDate = rowView.findViewById<TextView>(R.id.itemDate)
                        itemTitleView.setText(R.string.item_name)
                        itemLastSaleDate.setText(R.string.last_sale_date)
                    }
                }
            }
        }

        return rowView
    }
}