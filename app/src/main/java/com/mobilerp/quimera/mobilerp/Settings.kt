package com.mobilerp.quimera.mobilerp

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.mobilerp.quimera.mobilerp.offline_mode.OperationsLog
import com.mobilerp.quimera.mobilerp.offline_mode.SQLHandler
import com.mobilerp.quimera.mobilerp.online_mode.DownloadFileFromURL
import com.mobilerp.quimera.mobilerp.online_mode.FileDownloadListener
import com.mobilerp.quimera.mobilerp.online_mode.ServiceDiscovery
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import kotlinx.android.synthetic.main.fragment_settings.*

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

/**
 * A simple [Fragment] subclass.
 */
class Settings : Fragment() {

    internal lateinit var manager: SettingsManager


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context
        manager = SettingsManager.getInstance(context)

        val serverAddress = manager.getString(getString(R.string.server_addr))
        val useOfflineMode = manager.getBoolean(getString(R.string.use_offline_mode))

        etServerAddr.setText(serverAddress)

        etServerAddr.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER ||
                    event.keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                val context = getContext()
                val imm = v.context.getSystemService(Context
                        .INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                manager.saveString(getString(R.string.server_addr), etServerAddr.text.toString())

                Toast.makeText(getContext(), R.string.server_addr_updated, Toast
                        .LENGTH_LONG).show()
            }
            false
        }

        btnScanServer.setOnClickListener {
            val context = getContext()

            Toast.makeText(context, R.string.searching_server, Toast.LENGTH_LONG).show()
            val ds = ServiceDiscovery(context)
            ds.doScan()
            val serverAddress = manager.getString(getString(R.string.server_addr))
            etServerAddr.setText(serverAddress)
        }

        btnBackupDB.setOnClickListener(View.OnClickListener {
            Toast.makeText(getContext(), "Download db started", Toast.LENGTH_LONG).show()
            val fileDownloader = DownloadFileFromURL(object : FileDownloadListener {
                override fun onFileDownloaded() {
                    Toast.makeText(getContext(), R.string.download_finished, Toast.LENGTH_LONG).show()
                }
            })
            fileDownloader.execute(URLs.BASE_URL + URLs.DB_BACKUP, getString(R.string.database_name))
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                Log.d("DOWN_ERR", "SD \n" + Environment.getExternalStorageState())
                return@OnClickListener
            }
        })

        cbOfflineMode.isChecked = useOfflineMode!!
        Toast.makeText(getContext(), if (useOfflineMode) getString(R.string.offline_mode_enabled) else getString(R.string.offline_mode_disabled), Toast.LENGTH_LONG).show()
        cbOfflineMode.setOnClickListener {
            val context = getContext()
            val manager = SettingsManager.getInstance(context)
            var useOfflineMode: Boolean?
            useOfflineMode = cbOfflineMode.isChecked
            if (useOfflineMode) {
                val handler = SQLHandler.getInstance(context)
                if (handler.isDatabaseOpen)
                    Toast.makeText(context, if (useOfflineMode) R.string.offline_mode_enabled else R.string.offline_mode_disabled, Toast.LENGTH_LONG).show()
                else {
                    Toast.makeText(context, R.string.no_db_file, Toast.LENGTH_LONG).show()
                    useOfflineMode = false
                    cbOfflineMode.isChecked = useOfflineMode
                }
            } else {
                Toast.makeText(context, R.string.offline_mode_disabled, Toast.LENGTH_LONG).show()
            }
            manager.saveBoolean(getString(R.string.use_offline_mode), useOfflineMode)
        }

        btnUploadPendingOps.setOnClickListener {
            val log = OperationsLog.getInstance(context)
            log.pushOperations()
        }
    }
}// Required empty public constructor
