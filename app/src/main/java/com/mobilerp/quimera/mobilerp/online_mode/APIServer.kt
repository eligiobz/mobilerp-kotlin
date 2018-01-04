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
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.mobilerp.quimera.mobilerp.User
import org.json.JSONObject
import java.util.*

class APIServer(internal var context: Context) {
    private var queue: VolleySingleton? = null

    fun getResponse(method: Int, url: String, jsonValues: JSONObject, callback: VolleyCallback) {

        queue = VolleySingleton.getInstance(context)

        val request = object : JsonObjectRequest(method, url, jsonValues, object : Response.Listener<JSONObject>() {
            fun onResponse(response: JSONObject) {
                callback.onSuccessResponse(response)
            }
        }, object : Response.ErrorListener() {
            fun onErrorResponse(error: VolleyError) {
                val response = error.networkResponse
                if (response != null) {
                    callback.onErrorResponse(error)
                }
            }
        })
        //set headers
        {
            val headers: Map<String, String>
                @Throws(com.android.volley.AuthFailureError::class)
                get() {
                    val params = HashMap<String, String>()
                    params.put("Authorization", USER.getAuthString())
                    return params
                }
        }

        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }

    fun genericErrors(errorCode: Int) {
        if (errorCode == 401)
            Toast.makeText(context, R.string.srv_err_401_access_denied, Toast.LENGTH_LONG).show()
        if (errorCode == 500)
            Toast.makeText(context, R.string.srv_err_500_server_error, Toast.LENGTH_LONG).show()
        if (errorCode == 404)
            Toast.makeText(context, R.string.srv_err_404_not_found, Toast.LENGTH_LONG).show()
    }

    fun getResponse(no_auth: Boolean, method: Int, url: String, jsonValues: JSONObject, callback: VolleyCallback) {
        queue = VolleySingleton.getInstance(context)

        val request = JsonObjectRequest(method, url, jsonValues, object : Response.Listener<JSONObject>() {
            fun onResponse(response: JSONObject) {
                callback.onSuccessResponse(response)
            }
        }, object : Response.ErrorListener() {
            fun onErrorResponse(error: VolleyError) {
                val response = error.networkResponse
                if (response != null) {
                    callback.onErrorResponse(error)
                }
            }
        })
        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }

    companion object {


        private val USER = User.getInstance()
        private val URL = URLs.getInstance()
    }
}
