package com.mobilerp.quimera.mobilerp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mobilerp.quimera.mobilerp.ApiModels.ProductSaleModel
import com.mobilerp.quimera.mobilerp.R
import kotlinx.android.synthetic.main.product_sale_header.view.*
import kotlinx.android.synthetic.main.product_sale_row.view.*


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

class ProductSaleListAdapter(context: Context, private val productList: ArrayList<ProductSaleModel>,
                             layout:
Int) : ArrayAdapter<ProductSaleModel>(context, layout, productList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context!!.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rowView: View

        when (productList[position].isGroupHeader) {
            true -> {
                rowView = inflater.inflate(R.layout.product_sale_header, parent, false)

                rowView.tvProductSaleIdHeader.setText(R.string.item_id_sale)
                rowView.tvProductSaleNameHeader.setText(R.string.item_name)
                rowView.tvProductSalePriceHeader.setText(R.string.item_price)
                rowView.tvProductSaleTotalEarningHeader.setText(R.string.total_earning)
            }
            false -> {
                rowView = inflater.inflate(R.layout.product_sale_row, parent, false)

                rowView.tvProductSaleId.text = productList[position].idsale.toString()
                rowView.tvProductSaleName.text = productList[position].name
                rowView.tvProductSalePrice.text = productList[position].price.toString()
                rowView.tvProductSaleTotalEarning.text = productList[position].units.toString()
            }
        }

        return rowView
    }
}