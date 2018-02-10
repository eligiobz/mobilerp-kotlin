package com.mobilerp.quimera.mobilerp

import android.content.Context

/**
 * Created by Eligio Becerra on 04/01/2018.
 * Copyright (C) 2017 Eligio Becerra
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

class AppState private constructor() {
    private var offlineMode: Boolean = false
    private var hasPendingOperations: Boolean = false
    private var current_store: Int = -1
    private var set_manager: SettingsManager

    var currentStore: Int
        get() {
            return set_manager.getInt("current_store")
        }
        set(v) {
            set_manager.saveInt("current_store", v)
            current_store = v
        }

    var isOfflineMode: Boolean
        get() {
            return set_manager.getBoolean("Use offline mode")!!
        }
        set(v) {
            set_manager.saveBoolean(context!!.getString(R.string
                    .use_offline_mode), v)
            offlineMode = v
        }

    init {
        set_manager = SettingsManager.getInstance(context!!)
        offlineMode = set_manager.getBoolean(context!!.getString(R.string.use_offline_mode))!!
        hasPendingOperations = set_manager.getBoolean(context!!
                .getString(R.string.has_pending_ops))!!
        current_store = set_manager.getInt("current_store")
    }

    fun setHasPendingOperations(v: Boolean) {
        SettingsManager.getInstance(context!!).saveBoolean(context!!.getString(R.string
                .has_pending_ops), v)
        hasPendingOperations = v
    }

    fun HasPendingOperations(): Boolean {
        return hasPendingOperations
    }

    fun flushContext() {
        context = null
    }

    companion object {


        private var instance: AppState? = null
        private var context: Context? = null

        fun getInstance(c: Context): AppState {
            context = c
            if (instance == null)
                instance = AppState()
            return instance!!
        }
    }
}
