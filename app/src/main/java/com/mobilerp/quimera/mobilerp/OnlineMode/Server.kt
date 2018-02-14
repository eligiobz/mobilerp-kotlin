package com.mobilerp.quimera.mobilerp.OnlineMode

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.mobilerp.quimera.mobilerp.ApiModels.UserModel
import com.mobilerp.quimera.mobilerp.R
import java.io.File
import java.nio.charset.Charset

/**
 * Created by Eligio Becerra on 09/02/2018.
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

class Server (val context: Context) {

    private val user: UserModel = UserModel._getInstance()

    init{
        FuelManager.instance.basePath = URLs.BASE_URL
        FuelManager.instance.baseHeaders = mapOf(Pair("Content-Type", "application/json"))
    }

    fun genericErrors(errorCode: Int) {
        val message: Int
        when (errorCode) {
            400 -> message = R.string.srv_err_400_bad_request
            401 -> message = R.string.srv_err_401_access_denied
            404 -> message = R.string.srv_err_404_not_found
            405 -> message = R.string.srv_err_405_not_allowed
            406 -> message = R.string.srv_err_406_not_accepted
            409 -> message = R.string.srv_err_409_conflict
            412 -> message = R.string.srv_err_412_precondition_failed
            428 -> message = R.string.srv_err_428_dup_precond
            500 -> message = R.string.srv_err_500_server_error
            else -> message = R.string.srv_err_unknown
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun getModel(data: String): JsonObject{
        val parser = Parser()
        val stringBuilder = StringBuilder(data)
        return parser.parse(stringBuilder) as JsonObject

    }

    fun getRequest(url: String, success: (JsonObject) -> Unit, failure: (FuelError) -> Unit){
        Fuel.get(url).authenticate(user.name!!, user.pass!!).responseString{
            _, _, result ->
            val (data,error) = result
            when (error)
            {
                null -> {
                    success(getModel(data?:return@responseString))
                }
                else ->{
                    failure(error)
                }
            }
        }
    }

    fun postRequest(url: String, data: JsonObject, success: (JsonObject) -> Unit, failure:
    (FuelError) -> Unit){
        Fuel.post(url)
                .authenticate(user.name!!, user.pass!!)
                .body(data.toJsonString(), Charset.forName("UTF-8"))
                .responseString{
                    _, _, result ->
                    val (_response,error) = result
                    when (error){
                        null -> {
                            success(getModel(_response?: return@responseString))
                        }
                        else ->{
                            failure(error)
                        }
                    }
                }
    }

    fun putRequest(url: String, data: JsonObject, success: (JsonObject) -> Unit, failure:
    (FuelError) -> Unit){
        Fuel.put(url).authenticate(user.name!!, user.pass!!).body(data.toJsonString())
        .responseString{
            _, _, result ->
            val (data,error) = result
            when (error)
            {
                null -> {
                    success(getModel(data?: return@responseString))
                }
                else ->{
                    failure(error)
                }
            }
        }
    }

    fun downloadFile(url: String, saveFile: String, success: () -> Unit,
                     failure: (FuelError) -> Unit) {
        Fuel.download(url)
                .authenticate(user.name!!, user.pass!!)
                .destination { _, _ ->
                    var SDCardRoot = Environment.getExternalStorageDirectory()
                    SDCardRoot = File(SDCardRoot.absolutePath + "/MobilERP/")
                    SDCardRoot.mkdir()
                    File(SDCardRoot, saveFile)
                }
                .responseString { _, _, result ->
                    val (data, error) = result
                    when (error) {
                        null -> {
                            success()
                        }
                        else -> {
                            failure(error)
                        }
                    }
                }
    }
}