package com.mobilerp.quimera.mobilerp

import android.content.Context
import android.content.SharedPreferences

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

class SettingsManager protected constructor() {
    internal val fileMode = Context.MODE_PRIVATE
    internal var sharedPrefs: SharedPreferences
    internal var editor: SharedPreferences.Editor

    init {

        sharedPrefs = context.getSharedPreferences(fileName, fileMode)
    }

    fun saveString(key: String, _value: String) {
        editor = sharedPrefs.edit()
        editor.putString(key, _value)
        editor.apply()
    }

    fun saveBoolean(key: String, _value: Boolean) {
        editor = sharedPrefs.edit()
        editor.putBoolean(key, _value)
        editor.apply()
    }

    fun getString(key: String): String? {
        return sharedPrefs.getString(key, null)
    }

    fun getBoolean(key: String): Boolean? {
        return sharedPrefs.getBoolean(key, false)
    }

    companion object {

        /**
         * Singleton instance
         */
        var instance: SettingsManager? = null
        internal var fileName: String? = null
        internal var context: Context

        fun getInstance(_context: Context): SettingsManager {
            context = _context
            if (fileName == null) {
                fileName = context.getString(R.string.preferences_file)
            }
            if (instance == null) {
                instance = SettingsManager()
            }
            return instance
        }
    }
}
