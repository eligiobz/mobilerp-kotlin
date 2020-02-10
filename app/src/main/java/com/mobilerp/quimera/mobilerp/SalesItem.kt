package com.mobilerp.quimera.mobilerp

import android.os.Parcel
import android.os.Parcelable

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

class SalesItem : Parcelable {

    var amount: Int = 0
    var barcode: String
    var name: String
    var price: Double? = null
    var isService: Int? = null

    var TABLE_NAME = "ProductModel"
    var COLUMN_NAME_BARCODE = "barcode"
    var COLUMN_NAME_AMOUNT = "amount"
    var COLUMN_NAME_PRICE = "price"
    var COLUMN_NAME_NAME = "name"

    /**
     * Basic constructor
     *
     * @param barcode
     * @param amount
     * @param price
     * @param name
     */

    constructor(barcode: String, amount: Int, price: Double?, name: String, isService: Int) {
        this.barcode = barcode
        this.amount = amount
        this.price = price
        this.name = name
        this.isService = isService
    }

    protected constructor(`in`: Parcel) {
        barcode = `in`.readString()
        amount = `in`.readInt()
        price = `in`.readDouble()
        name = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(barcode)
        dest.writeInt(amount)
    }

    fun InsertItem() {

    }

    companion object CREATOR : Parcelable.Creator<SalesItem> {
        override fun createFromParcel(parcel: Parcel): SalesItem {
            return SalesItem(parcel)
        }

        override fun newArray(size: Int): Array<SalesItem?> {
            return arrayOfNulls(size)
        }
    }
}
