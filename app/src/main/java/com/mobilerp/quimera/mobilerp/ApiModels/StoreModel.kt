package com.mobilerp.quimera.mobilerp.ApiModels

import com.beust.klaxon.JsonObject

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

class StoreModel {

    var id : Int? = null
    var name : String? = null

    constructor(jsonObject : JsonObject){
        this.id = jsonObject.int("id")
        this.name = jsonObject.string("name")
    }

}