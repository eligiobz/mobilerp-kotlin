package com.mobilerp.quimera.mobilerp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
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

class OptionListAdapter(private val contx: Context, private val modelsArrayList: ArrayList<OptionListModel>) : ArrayAdapter<OptionListModel>(contx, R.layout.option_row, modelsArrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = contx.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val rowView: View
        if (!modelsArrayList[position].getIsGroupHeader()) {
            rowView = inflater.inflate(R.layout.option_row, parent, false)

            val imageView = rowView.findViewById(R.id.rowIcon) as ImageView
            val titleView = rowView.findViewById(R.id.rowTextView) as TextView

            imageView.setImageResource(modelsArrayList[position].getIcon())
            titleView.setText(modelsArrayList[position].getTitle())
        } else {
            rowView = inflater.inflate(R.layout.list_header, parent, false)
            val titleView = rowView.findViewById(R.id.listTitle) as TextView
            titleView.setText(modelsArrayList[position].getTitle())
        }

        return rowView
    }

}
