package com.mobilerp.quimera.mobilerp

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

class ItemListModel {

    var name: String? = null
    var price: Double? = null
    var total: Int? = null
    var barcode: String? = null
    var date: String? = null

    private var isGroupHeader = false

    val priceString: String
        get() = price.toString()

    val totalString: String
        get() = total!!.toString()

    constructor(title: String) : this(title, 0.0, 0) {
        this.setIsGroupHeader(true)
    }

    constructor(name: String, price: Double?, total: Int?) : super() {
        this.name = name
        this.price = price
        this.total = total
    }

    constructor(barcode: String, name: String, price: Double?, total: Int?) : this(name, price, total) {
        this.barcode = barcode
    }

    constructor(name: String, date: String) {
        this.name = name
        this.date = date
    }

    fun getIsGroupHeader(): Boolean {
        return isGroupHeader()
    }

    fun setIsGroupHeader(groupHeader: Boolean) {
        this.isGroupHeader = groupHeader
    }

    fun isGroupHeader(): Boolean {
        return isGroupHeader
    }
}
