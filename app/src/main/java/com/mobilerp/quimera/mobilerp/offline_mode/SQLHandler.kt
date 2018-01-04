package com.mobilerp.quimera.mobilerp.offline_mode

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.widget.Toast
import com.mobilerp.quimera.mobilerp.R
import java.io.File

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

class SQLHandler protected constructor() {
    lateinit var db: SQLiteDatabase

    val isDatabaseOpen: Boolean
        get() = databaseOpen

    init {
        val file = checkFile()
        if (!file.exists()) {
            Toast.makeText(context, R.string.no_db_file, Toast.LENGTH_LONG).show()
            databaseOpen = false
        } else {
            db = SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READWRITE)
            databaseOpen = db.isOpen
        }
    }

    companion object {

        private var databaseOpen: Boolean = false
        private var context: Context? = null
        private var instance: SQLHandler? = null

        fun getInstance(_context: Context): SQLHandler {
            context = _context
            if (instance == null || !instance!!.isDatabaseOpen) {
                instance = SQLHandler()
            } else {
                val foo = checkFile()
                if (!foo.exists()) {
                    databaseOpen = false
                    instance = SQLHandler()
                }
            }
            return instance!!
        }

        fun checkFile(): File {
            var SDCardRoot = Environment.getExternalStorageDirectory()
            SDCardRoot = File(SDCardRoot.absolutePath + "/MobilERP")
            return File(SDCardRoot, context!!.getString(R.string.database_name))
        }
    }
}
