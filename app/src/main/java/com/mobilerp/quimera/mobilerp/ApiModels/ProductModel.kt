package com.mobilerp.quimera.mobilerp.ApiModels

import com.beust.klaxon.JsonObject

/**
 * Created by Eligio Becerra on 08/02/2018.
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

class ProductModel {

    var barcode: String? = null
    var name: String? = null
    var price: Double? = null
    var units: Int? = null
    var storeid: Int? = null
    var date: String? = null

    var isGroupHeader = false

    constructor(title: String)  {
        this.name = title
        this.isGroupHeader = true
    }

    constructor(name: String, date: String) {
        this.name = name
        this.date = date
    }

    constructor (jsonObject: JsonObject) {
        this.barcode = jsonObject.string("barcode")
        this.name = jsonObject.string("name")
        this.price = jsonObject.double("price")
        this.units = jsonObject.int("units")
        this.storeid = jsonObject.int("storeid")
        this.date = jsonObject.string("date")
    }
}
