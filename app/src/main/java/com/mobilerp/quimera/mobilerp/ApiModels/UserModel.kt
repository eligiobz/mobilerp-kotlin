package com.mobilerp.quimera.mobilerp.ApiModels

import android.util.Base64
import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject

/**
 *  Created by Eligio Becerra on 04/01/2018.
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
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

class UserModel protected constructor() {

    var logged : Boolean = false
    var name : String? = null
    var pass : String? = null
    var level : Int? = null

    constructor(jsonObject: JsonObject) : this() {
        this.name = jsonObject.string("name")
        this.pass= jsonObject.string("pass")
        this.level = jsonObject.int("level")
    }

    companion object {

        private var instance: UserModel? = null

        fun _getInstance(): UserModel {
            if (instance == null) {
                instance = UserModel()
            }
            return instance!!
        }
    }
}
