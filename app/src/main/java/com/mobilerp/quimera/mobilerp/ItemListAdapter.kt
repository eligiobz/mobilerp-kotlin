package com.mobilerp.quimera.mobilerp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.*

//depleted_items_layout
//list_header_multi_items
//list_header_depleted_items

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

class ItemListAdapter(context: Context, private val modelsArrayList: ArrayList<ItemListModel>, layout: Int) : ArrayAdapter<ItemListModel>(context, layout, modelsArrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context!!.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rowView: View

        if (!modelsArrayList[position].getIsGroupHeader()) {
            if (modelsArrayList[position].date == null) {
                rowView = inflater.inflate(R.layout.item_row, parent, false)

                val itemNameView = rowView.findViewById<TextView>(R.id.itemName)
                val itemPriceView = rowView.findViewById<TextView>(R.id.itemPrice)
                val itemTotalView = rowView.findViewById<TextView>(R.id.itemTotal)

                itemNameView.text = modelsArrayList[position].name
                itemPriceView.text = modelsArrayList[position].priceString
                itemTotalView.text = modelsArrayList[position].totalString
            } else {
                rowView = inflater.inflate(R.layout.depleted_items_layout, parent, false)

                val itemNameView = rowView.findViewById<TextView>(R.id.itemName)
                val itemLastSaleDate = rowView.findViewById<TextView>(R.id.itemLastSaleDate)

                itemNameView.text = modelsArrayList[position].name
                itemLastSaleDate.text = modelsArrayList[position].date.toString()
            }
        } else {
            if (modelsArrayList[position].name.equals("title")) {
                rowView = inflater.inflate(R.layout.list_header_multi_items, parent, false)
                val itemTitleView = rowView.findViewById<TextView>(R.id.itemName)
                val itemPriceView = rowView.findViewById<TextView>(R.id.itemPrice)
                val itemTotalView = rowView.findViewById<TextView>(R.id.itemTotal)
                itemTitleView.setText(R.string.item_name)
                itemPriceView.setText(R.string.item_price)
                itemTotalView.setText(R.string.item_total)
            } else {
                rowView = inflater.inflate(R.layout.list_header_depleted_items, parent, false)
                val itemTitleView = rowView.findViewById<TextView>(R.id.itemName)
                val itemLastSaleDate = rowView.findViewById<TextView>(R.id.itemDate)
                itemTitleView.setText(R.string.item_name)
                itemLastSaleDate.setText(R.string.last_sale_date)
            }
        }

        return rowView
    }
}