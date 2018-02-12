package com.mobilerp.quimera.mobilerp.offline_mode

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.mobilerp.quimera.mobilerp.R
import com.mobilerp.quimera.mobilerp.SettingsManager
import org.json.JSONObject
import java.io.*
import java.util.*

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

class OperationsLog protected constructor() {
    var filename: String
    var isFileOpen: Boolean = false
    internal var SDCardRoot: File
    internal var file: File
    internal lateinit var output: FileOutputStream
    internal var input: FileInputStream? = null


    init {
        filename = context.getString(R.string.logfile)
        SDCardRoot = Environment.getExternalStorageDirectory()
        SDCardRoot = File(SDCardRoot.absolutePath + "/MobilERP")
        file = File(SDCardRoot, filename)
    }

    protected fun openFile() {
        try {
            output = FileOutputStream(file, true)
            isFileOpen = true
        } catch (e: FileNotFoundException) {
            isFileOpen = false
        }

    }

    protected fun closeFile() {
        isFileOpen = false
        try {
            output.flush()
            output.close()
        } catch (e: IOException) {

        }

    }

    fun add(method: Int, endPoint: String, data: JSONObject) {
        openFile()
        if (isFileOpen) {
            try {
                val finalString = method.toString() + "\t" + endPoint + "\t" + data.toString() + "\n"
                Log.d("Final Data", finalString)
                output.write(finalString.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            Toast.makeText(context, R.string.error_saving_operation, Toast.LENGTH_LONG).show()
        }
        closeFile()
    }

    fun pushOperations() {
        if (SettingsManager.getInstance(context).getBoolean(context.getString(R.string
                .use_offline_mode))!!) {
            Toast.makeText(context, R.string.disable_offline_mode_first, Toast.LENGTH_LONG).show()
            return
        }
        val operations = loadOperations()
        if (operations == null) {
            Toast.makeText(context, "Nothing done", Toast.LENGTH_LONG).show()
            return
        }
//        apiServer = APIServer(context)
//        for (op in operations) {
//            val `object` = JSONObject()
//            val _op = op.split("\t".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//            try {
//                apiServer.getResponse(Integer.valueOf(_op[0]), URLs.BASE_URL + _op[1], JSONObject(_op[2]), object : VolleyCallback {
//                    override fun onSuccessResponse(result: JSONObject) {
//                        Toast.makeText(context, R.string.srv_op_success, Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onErrorResponse(error: VolleyError) {
//                        Toast.makeText(context, R.string.srv_op_fail, Toast.LENGTH_SHORT).show()
//                    }
//                })
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }

//        }
    }

    private fun loadOperations(): ArrayList<String>? {
        try {
            val operations = ArrayList<String>()
            val stream = FileInputStream(file)
            val reader = InputStreamReader(stream)
            val input = BufferedReader(reader)
            var line: String?
            while (true) {
                line = input.readLine()
                if (line != null)
                    operations.add(line)
                else
                    break
            }
            destroyFile()
            return operations
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun destroyFile() {
        if (file.exists())
            file.delete()
    }

    companion object {

        var instance: OperationsLog? = null
        internal lateinit var context: Context

        fun getInstance(_context: Context): OperationsLog {
            if (instance == null) {
                context = _context
                instance = OperationsLog()
            }
            return instance!!
        }
    }
}
