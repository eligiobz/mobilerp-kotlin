package com.mobilerp.quimera.mobilerp.online_mode

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.VolleyError
import org.json.JSONObject
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.UnknownHostException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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

class ServiceDiscovery(internal val context: Context?) {
    internal val LOG_TAG = "NetScan"
    internal val prefsFile: String
    internal val server_addr: String
    internal val manager: SettingsManager
    internal var netPrefix: String
    internal var wm: WifiManager
    internal var port: Int = 0
    internal var apiServer: APIServer
    var isServerFound: Boolean = false
        internal set

    init {
        this.netPrefix = ""
        this.port = 5000
        apiServer = APIServer(this.context)
        prefsFile = context!!.getString(R.string.preferences_file)
        server_addr = context.getString(R.string.server_addr)
        manager = SettingsManager.getInstance(context)



        if (context !=
                null) {

            wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectionInfo = wm.connectionInfo
            Log.d("NET_NAME", wm.connectionInfo.ssid)
            Log.d("NET_NAME", wm.connectionInfo.networkId.toString())
            val ipAddress = connectionInfo.ipAddress
            val ipString = Formatter.formatIpAddress(ipAddress)
            netPrefix = ipString.substring(0, ipString.lastIndexOf(".") + 1)
        }

    }

    fun doScan() {
        if (wm.wifiState == WifiManager.WIFI_STATE_DISABLED || wm.wifiState == WifiManager.WIFI_STATE_UNKNOWN) {
            Log.d("NET_NAME", "NOT CONNECTED")
            Toast.makeText(context, R.string.not_connected, Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(context, "Start scanning", Toast.LENGTH_LONG).show()

        val executor = Executors.newFixedThreadPool(NB_THREADS)
        for (dest in 0..254) {
            val host = netPrefix + dest
            executor.execute(pingRunnable(host))
        }

        Toast.makeText(context, "Waiting for executor to terminate...", Toast.LENGTH_LONG).show()
        executor.shutdown()
        try {
            executor.awaitTermination((60 * 1000).toLong(), TimeUnit.MILLISECONDS)
        } catch (ignored: InterruptedException) {
        }

        Toast.makeText(context, "Scan finished", Toast.LENGTH_LONG).show()
    }

    private fun pingRunnable(host: String): Runnable {
        return Runnable {
            try {
                val inet = InetAddress.getByName(host)
                if (inet.isReachable(1000)) {
                    val _url = "http:/" + (inet.toString() + ":" + port.toString())
                    if (testPort(inet)) {
                        Log.d(LOG_TAG, "Successful connection to port 5000 @ " + _url)
                        val URL = URLs.getInstance()
                        URL.setBASE_URL(_url)
                        Log.d(LOG_TAG, "BASE_URL set to :: " + _url)

                        manager.saveString(server_addr, _url)
                        Log.d(LOG_TAG, "Wrote to prefs file :: " + _url)

                        //testServer(_url);
                        //                            ExecutorService executor = Executors.newFixedThreadPool(2);
                        //                            executor.execute(testServer(_url));
                    }
                }
            } catch (e: UnknownHostException) {
                Log.e(LOG_TAG, "Not found", e)
            } catch (e: IOException) {
                Log.e(LOG_TAG, "IO Error", e)
            }
        }
    }

    private fun testServer(url: String) {
        Log.d(LOG_TAG, "Testing server response @ " + url)
        apiServer.getResponse(false, Request.Method.GET, url, null, object : VolleyCallback() {
            fun onSuccessResponse(result: JSONObject) {
                Log.d(LOG_TAG, "VALID SERVER @ " + url)
                val URL = URLs.getInstance()
                URL.setBASE_URL(url)
            }

            fun onErrorResponse(error: VolleyError) {
                Log.d(LOG_TAG, "Why am I here? " + url)
            }
        })
    }

    //    private Runnable testServer(final String url) {
    //        return new Runnable() {
    //            @Override;
    //            public void run() {
    //                Log.d(LOG_TAG, "Testing server response @ " + url);
    //                apiServer.getResponse(false, Request.Method.GET, url,
    //                        null, new VolleyCallback() {
    //                            @Override
    //                            public void onSuccessResponse(JSONObject result) {
    //                                Log.d(LOG_TAG, "VALID SERVER @ " + url);
    //                                URLs URL = URLs.getInstance();
    //                                URL.setBASE_URL(url);
    //                            }
    //
    //                            @Override
    //                            public void onErrorResponse(VolleyError error) {
    //                                Log.d(LOG_TAG, "Why am I here? " + url);
    //                            }
    //                        });
    //            }
    //        };
    //    }

    private fun testPort(ip: InetAddress): Boolean {
        try {
            val address = InetSocketAddress(ip, port)
            val socket = Socket()
            val timeout = 2000

            socket.connect(address, timeout)
            return true
        } catch (e: IOException) {
            return false
        }

    }

    companion object {

        private val NB_THREADS = 10
    }
}