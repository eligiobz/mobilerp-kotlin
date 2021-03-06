package com.mobilerp.quimera.mobilerp.ApiModels

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

class SalesItemModel : Parcelable {

    var units: Int = 0
    var barcode: String
    var name: String
    var price: Double
    var isService: Int

//    var TABLE_NAME = "ProductModel"
//    var COLUMN_NAME_BARCODE = "barcode"
//    var COLUMN_NAME_AMOUNT = "amount"
//    var COLUMN_NAME_PRICE = "price"
//    var COLUMN_NAME_NAME = "name"

    /**
     * Basic constructor
     *
     * @param barcode
     * @param amount
     * @param price
     * @param name
     */

    constructor(barcode: String, amount: Int, price: Double, name: String, isService: Int) {
        this.barcode = barcode
        this.units = amount
        this.price = price
        this.name = name
        this.isService = isService
    }

    constructor(`in`: Parcel) {
        barcode = `in`.readString()
        units = `in`.readInt()
        price = `in`.readDouble()
        name = `in`.readString()
        isService = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(barcode)
        dest.writeInt(units)
    }

    companion object CREATOR : Parcelable.Creator<SalesItemModel> {
        override fun createFromParcel(parcel: Parcel): SalesItemModel {
            return SalesItemModel(parcel)
        }

        override fun newArray(size: Int): Array<SalesItemModel?> {
            return arrayOfNulls(size)
        }
    }
}
