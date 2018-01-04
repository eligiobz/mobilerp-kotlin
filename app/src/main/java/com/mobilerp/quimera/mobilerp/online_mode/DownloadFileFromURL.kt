package com.mobilerp.quimera.mobilerp.online_mode

import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.mobilerp.quimera.mobilerp.User
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

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

class DownloadFileFromURL(private val listener: FileDownloadListener) : AsyncTask<String, String, String>() {

    override fun onPostExecute(s: String) {
        super.onPostExecute(s)
        listener.onFileDownloaded()
    }

    /**
     * Downloading file in background thread
     */
    override fun doInBackground(vararg f_url: String): String? {
        var count: Int
        try {
            val url = URL(f_url[0])
            val connection = url.openConnection() as HttpURLConnection
            Log.d("AUTH/HEADER", User._getInstance().getAuthString())
            connection.setRequestProperty("Authorization", User._getInstance().getAuthString())
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                connection.connect()
                val input = BufferedInputStream(connection.inputStream, 8192)
                var SDCardRoot = Environment.getExternalStorageDirectory()
                SDCardRoot = File(SDCardRoot.absolutePath + "/MobilERP")
                SDCardRoot.mkdir()
                Log.d("CON/FILENAME", f_url[1])
                val file = File(SDCardRoot, f_url[1])
                val output = FileOutputStream(file)
                val data = ByteArray(1024)


                do {
                    count = input.read(data)
                    if (count != 1)
                        output.write(data, 0, count)
                } while (count != -1)

                output.flush()
                output.close()
                input.close()
            } else
                Log.d("CON/ERR", "Auth failed " + responseCode.toString())

        } catch (e: Exception) {
            Log.d("Error: ", e.message)
        }

        return null
    }
}
