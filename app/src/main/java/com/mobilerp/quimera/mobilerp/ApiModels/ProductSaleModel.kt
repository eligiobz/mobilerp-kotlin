package com.mobilerp.quimera.mobilerp.ApiModels

import com.beust.klaxon.JsonObject

/**
 * Created by Eligio Becerra on 12/02/2018.
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
class ProductSaleModel : ProductModel{

    var idsale: Int? = null
    var total_earning: Float? = null

    constructor(jsonObject: JsonObject) : super(jsonObject){
        this.idsale = jsonObject.int("idsale")
        this.total_earning = jsonObject.float("total_earning")

    }

    constructor(name: String): super(name)
}