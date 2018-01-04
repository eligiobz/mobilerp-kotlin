package com.mobilerp.quimera.mobilerp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.*

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

class ItemListAdapter(private val context: Context, private val modelsArrayList: ArrayList<ItemListModel>, layout: Int) : ArrayAdapter<ItemListModel>(context, layout, modelsArrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rowView: View

        if (!modelsArrayList[position].getIsGroupHeader()) {
            if (modelsArrayList[position].getDate() == null) {
                rowView = inflater.inflate(R.layout.item_row, parent, false)

                val itemNameView = rowView.findViewById(R.id.itemName) as TextView
                val itemPriceView = rowView.findViewById(R.id.itemPrice) as TextView
                val itemTotalView = rowView.findViewById(R.id.itemTotal) as TextView

                itemNameView.setText(modelsArrayList[position].getName())
                itemPriceView.setText(modelsArrayList[position].getPriceString())
                itemTotalView.setText(modelsArrayList[position].getTotalString())
            } else {
                rowView = inflater.inflate(R.layout.depleted_items_layout, parent, false)

                val itemNameView = rowView.findViewById(R.id.itemName) as TextView
                val itemLastSaleDate = rowView.findViewById(R.id.itemLastSaleDate) as TextView

                itemNameView.setText(modelsArrayList[position].getName())
                itemLastSaleDate.setText(modelsArrayList[position].getDate().toString())
            }
        } else {
            if (modelsArrayList[position].getName().equals("title")) {
                rowView = inflater.inflate(R.layout.list_header_multi_items, parent, false)
                val itemTitleView = rowView.findViewById(R.id.itemName) as TextView
                val itemPriceView = rowView.findViewById(R.id.itemPrice) as TextView
                val itemTotalView = rowView.findViewById(R.id.itemTotal) as TextView
                itemTitleView.setText(R.string.item_name)
                itemPriceView.setText(R.string.item_price)
                itemTotalView.setText(R.string.item_total)
            } else {
                rowView = inflater.inflate(R.layout.list_header_depleted_items, parent, false)
                val itemTitleView = rowView.findViewById(R.id.itemName) as TextView
                val itemLastSaleDate = rowView.findViewById(R.id.itemDate) as TextView
                itemTitleView.setText(R.string.item_name)
                itemLastSaleDate.setText(R.string.last_sale_date)
            }
        }

        return rowView
    }
}