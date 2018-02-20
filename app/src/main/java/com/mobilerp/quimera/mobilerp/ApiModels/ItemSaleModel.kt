package com.mobilerp.quimera.mobilerp.ApiModels

import android.accessibilityservice.GestureDescription
import com.beust.klaxon.JsonObject
import com.beust.klaxon.json

/**
 * Created by Eligio Becerra on 10/02/2018.
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
class SaleReportModel {

    var title : String? =null
    var totalEarnings : Float? = null
    var totalItemsSold : Int? = null
    var totalSales : Int? = null

    constructor(jsonObject: JsonObject){
        this.title = jsonObject.string("title")
        this.totalEarnings = jsonObject.float("totalEarnings")
        this.totalItemsSold =jsonObject.int("totalItemsSold")
        this.totalSales = jsonObject.int("totalSales")
    }
}