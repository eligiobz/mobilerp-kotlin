package com.mobilerp.quimera.mobilerp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.VolleyError
import com.mobilerp.quimera.mobilerp.online_mode.APIServer
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import com.mobilerp.quimera.mobilerp.online_mode.VolleyCallback
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject

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

class LoginActivity : AppCompatActivity() {

    internal lateinit var context: Context
    internal lateinit var pd: ProgressDialog
    internal lateinit var apiServer: APIServer
    internal var user = User._getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context = this
        pd = ProgressDialog(context, ProgressDialog.STYLE_SPINNER)
        user = User._getInstance()
        apiServer = APIServer(context)
    }

    fun checkLogin(view: View) {
        user.name = editText.text.toString()
        user.pass = editText2.text.toString()
        val url = URLs.BASE_URL + URLs.LOGIN
        apiServer.getResponse(Request.Method.GET, url, null, object : VolleyCallback {
            override fun onSuccessResponse(result: JSONObject) {
                try {
                    if (result.getBoolean("logged")) {
                        user.isLoginIn = true
                        Toast.makeText(context, R.string.srv_op_success, Toast.LENGTH_LONG).show()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    Toast.makeText(context, R.string.srv_op_fail, Toast.LENGTH_LONG).show()
                }

            }

            override fun onErrorResponse(error: VolleyError) {
                val response = error.networkResponse
                apiServer.genericErrors(response.statusCode)
            }
        })
    }
}
