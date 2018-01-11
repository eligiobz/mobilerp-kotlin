package com.mobilerp.quimera.mobilerp.online_mode

/**
 * Created by Eligio Becerra on 06/12/2017.
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


import android.content.Context
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.mobilerp.quimera.mobilerp.R
import com.mobilerp.quimera.mobilerp.User
import org.json.JSONObject

class APIServer(internal var context: Context) {
    private var queue: VolleySingleton? = null

    fun getResponse(method: Int, url: String, jsonValues: JSONObject?, callback: VolleyCallback) {

        queue = VolleySingleton.getInstance(context)

        val request = object : JsonObjectRequest(method, url, jsonValues, object : Response.Listener<JSONObject> {
            override fun onResponse(response: JSONObject) {
                callback.onSuccessResponse(response)
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                val response = error.networkResponse
                if (response != null) {
                    callback.onErrorResponse(error)
                }
            }
        })
        //set headers
        {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization", USER.getAuthString())
                return headers
            }
        }

        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }

    fun genericErrors(errorCode: Int) {
        val message: Int
        when (errorCode) {
            401 -> message = R.string.srv_err_401_access_denied
            500 -> message = R.string.srv_err_500_server_error
            404 -> message = R.string.srv_err_404_not_found
            405 -> message = R.string.srv_err_405_not_allowed
            406 -> message = R.string.srv_err_406_not_accepted
            428 -> message = R.string.srv_err_428_dup_precond
            else -> message = R.string.srv_err_unknown
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun getResponse(no_auth: Boolean, method: Int, url: String, jsonValues: JSONObject?,
                    callback: VolleyCallback) {
        queue = VolleySingleton.getInstance(context)

        val request = JsonObjectRequest(method, url, jsonValues, object : Response.Listener<JSONObject> {
            override fun onResponse(response: JSONObject) {
                callback.onSuccessResponse(response)
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                val response = error.networkResponse
                if (response != null) {
                    callback.onErrorResponse(error)
                }
            }
        })
        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }

    companion object {
        private val USER = User._getInstance()
        private val URL = URLs._getInstance()
    }
}
