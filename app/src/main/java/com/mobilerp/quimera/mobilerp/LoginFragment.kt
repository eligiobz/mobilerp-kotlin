package com.mobilerp.quimera.mobilerp

import android.support.v4.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mobilerp.quimera.mobilerp.ApiModels.UserModel
import com.mobilerp.quimera.mobilerp.OnlineMode.Server
import com.mobilerp.quimera.mobilerp.OnlineMode.URLs
import kotlinx.android.synthetic.main.fragment_login.*

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

    private val server : Server by lazy { Server(context) }
    internal val user : UserModel by lazy { UserModel._getInstance() }
    private lateinit var settingManager : SettingsManager

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_login, container, false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setContentView(R.layout.fragment_login)

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
            server.getRequest(URLs.LOGIN, success = {
                if (it.string("logged") == "true"){
                    if (remember_user.isChecked){
                        settingManager.saveString("username", user.name!!)
                        settingManager.saveString("password", user.pass!!)
                        user.logged = true
                        Toast.makeText(context, R.string.srv_op_success, Toast.LENGTH_LONG).show()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }, failure = {
                server.genericErrors(it.response.statusCode)
            })

        })
    }
}
