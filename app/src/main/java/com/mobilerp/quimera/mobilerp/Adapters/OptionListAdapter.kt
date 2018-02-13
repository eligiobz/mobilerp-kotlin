package com.mobilerp.quimera.mobilerp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mobilerp.quimera.mobilerp.OptionListModel
import com.mobilerp.quimera.mobilerp.R
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

class OptionListAdapter(private val contx: Context, private val modelsArrayList: ArrayList<OptionListModel>) : ArrayAdapter<OptionListModel>(contx, R.layout.option_row, modelsArrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = contx.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rowView: View
        if (!modelsArrayList[position].isGroupHeader) {
            rowView = inflater.inflate(R.layout.option_row, parent, false)

            val imageView = rowView.findViewById<ImageView>(R.id.rowIcon)
            val titleView = rowView.findViewById<TextView>(R.id.rowTextView)

            imageView.setImageResource(modelsArrayList[position].icon)
            titleView.text = modelsArrayList[position].title
        } else {
            rowView = inflater.inflate(R.layout.list_header, parent, false)
            val titleView = rowView.findViewById<TextView>(R.id.listTitle)
            titleView.text = modelsArrayList[position].title
        }

        return rowView
    }

}
