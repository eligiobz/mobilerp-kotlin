package com.mobilerp.quimera.mobilerp

import android.util.Base64

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

class User protected constructor() {
    var isLoginIn = false
    var name = ""
        set(name) {
            field = name
            this.setAuthString()
        }
    var pass = ""
        set(pass) {
            field = pass
            this.setAuthString()
        }

    private var authString = ""

    fun setAuthString() {
        val loginEncoded = String(Base64.encode((this.name + ":" + this.pass).toByteArray(),
                Base64.NO_WRAP))
        authString = "Basic " + loginEncoded
    }

    fun getAuthString(): String {
        if (authString.isEmpty())
            this.setAuthString()
        return authString
    }

    companion object {

        private var instance: User? = null

        fun _getInstance(): User {
            if (instance == null) {
                instance = User()
            }
            return instance!!
        }
    }
}
