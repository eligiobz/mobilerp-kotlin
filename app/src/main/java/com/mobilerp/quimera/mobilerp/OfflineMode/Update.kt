package com.mobilerp.quimera.mobilerp.OfflineMode

import android.content.Context

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
class Update(var context: Context) {

    var sqlHandler: SQLHandler
    // TODO: Write update query builder
    /*String... params*/ var query: String? = null
    val isQueryReady: Boolean

    init {
        sqlHandler = SQLHandler.getInstance(this.context)
        isQueryReady = false
    }

    fun execute(): Boolean {
        if (!isQueryReady) {
            return false
        }
        sqlHandler.db.execSQL(this.query)
        return true
    }
}
