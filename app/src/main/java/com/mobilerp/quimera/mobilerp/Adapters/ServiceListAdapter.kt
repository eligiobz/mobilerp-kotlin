package com.mobilerp.quimera.mobilerp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mobilerp.quimera.mobilerp.ApiModels.ProductModel
import com.mobilerp.quimera.mobilerp.ApiModels.ServiceModel
import com.mobilerp.quimera.mobilerp.R

/**
 * Created by Eligio Becerra on 20/02/2018.
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
class ServiceListAdapter (context: Context, private val serviceList: ArrayList<ServiceModel>, layout:
Int) : ArrayAdapter<ServiceModel>(context, layout, serviceList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context!!.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rowView: View

        when (!serviceList[position].isGroupHeader){
            true -> {

                rowView = inflater.inflate(R.layout.depleted_items_layout, parent, false)

                val itemNameView = rowView.findViewById<TextView>(R.id.itemName)
                val itemLastSaleDate = rowView.findViewById<TextView>(R.id.itemLastSaleDate)

                itemNameView.text = serviceList[position].name
                itemLastSaleDate.text = serviceList[position].price.toString()

                }
            false -> {

                rowView = inflater.inflate(R.layout.depleted_products_list_header, parent, false)

                val itemTitleView = rowView.findViewById<TextView>(R.id.itemName)
                val itemLastSaleDate = rowView.findViewById<TextView>(R.id.itemDate)

                itemTitleView.setText(R.string.item_name)
                itemLastSaleDate.setText(R.string.item_price)
            }
        }

        return rowView
    }
}