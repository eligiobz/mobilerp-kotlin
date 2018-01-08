package com.mobilerp.quimera.mobilerp

import android.support.v4.app.Fragment
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.VolleyError
import com.mobilerp.quimera.mobilerp.online_mode.APIServer
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import com.mobilerp.quimera.mobilerp.online_mode.VolleyCallback
import kotlinx.android.synthetic.main.fragment_login.*
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

class LoginFragment() : Fragment() {

    internal lateinit var context: Context
    internal lateinit var pd: ProgressDialog
    internal lateinit var apiServer: APIServer
    internal var user = User._getInstance()
    private lateinit var settingManager : SettingsManager

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_login, container, false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setContentView(R.layout.fragment_login)
        context = getContext()
        pd = ProgressDialog(context, ProgressDialog.STYLE_SPINNER)
        user = User._getInstance()
        apiServer = APIServer(context)
        settingManager = SettingsManager.getInstance(context)

        val uname = settingManager.getString("username")
        val upass = settingManager.getString("password")
        if (uname != null && upass != null){
            etUserName.setText(uname)
            etUserPass.setText(upass)
            remember_user.isChecked = true
        }
        btnLogin.setOnClickListener({
            user.name = etUserName.text.toString()
            user.pass = etUserPass.text.toString()
            val url = URLs.BASE_URL + URLs.LOGIN
            apiServer.getResponse(Request.Method.GET, url, null, object : VolleyCallback {
                override fun onSuccessResponse(result: JSONObject) {
                    try {
                        if (result.getBoolean("logged")) {
                            if (remember_user.isChecked){
                                settingManager.saveString("username", user.name)
                                settingManager.saveString("password", user.pass)
                            }
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
        })
    }
}
