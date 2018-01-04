package com.mobilerp.quimera.mobilerp.online_mode

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

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

class VolleySingleton private constructor(context: Context) {
    private var requestQueue: RequestQueue? = null

    init {
        VolleySingleton.context = context
        requestQueue = getRequestQueue()
    }

    fun getRequestQueue(): RequestQueue {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context!!.applicationContext)
        }
        return requestQueue
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        getRequestQueue().add(request)
    }

    companion object {

        private var instance: VolleySingleton? = null
        private var context: Context? = null

        @Synchronized
        fun getInstance(context: Context): VolleySingleton {
            if (instance == null) {
                instance = VolleySingleton(context)
            }
            return instance
        }
    }
}