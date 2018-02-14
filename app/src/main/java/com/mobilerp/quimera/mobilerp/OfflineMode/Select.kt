package com.mobilerp.quimera.mobilerp.OfflineMode

import android.content.Context
import android.database.Cursor

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

class Select(var context: Context) {
    var sqlHandler: SQLHandler
    lateinit var results: Cursor
    var query: String? = null

    init {
        sqlHandler = SQLHandler.getInstance(this.context)
    }

    fun execute(): Boolean {
        if (query == null)
            return false
        results = sqlHandler.db.rawQuery(query, null)
        results.moveToFirst()
        return true
    }
}