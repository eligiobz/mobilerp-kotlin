package com.mobilerp.quimera.mobilerp

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.mobilerp.quimera.mobilerp.online_mode.URLs

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

    internal var etServerAddr: EditText
    internal var btnScanServer: Button
    internal var btnBackupDB: Button
    internal var btnUpladPendingOps: Button
    internal var cbOfflineMode: CheckBox
    internal var ablMainBar: AppBarLayout
    internal var manager: SettingsManager


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

        ablMainBar = getView()!!.findViewById(R.id.ablMainBar) as AppBarLayout
        etServerAddr = getView()!!.findViewById(R.id.etServerAddr) as EditText
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

        btnScanServer = getView()!!.findViewById(R.id.btnScanServer) as Button
        btnScanServer.setOnClickListener {
            val context = getContext()

            Toast.makeText(context, R.string.searching_server, Toast.LENGTH_LONG).show()
            val ds = ServiceDiscovery(context)
            ds.doScan()
            val serverAddress = manager.getString(getString(R.string.server_addr))
            etServerAddr.setText(serverAddress)
        }

        btnBackupDB = getView()!!.findViewById(R.id.btnBackupDB) as Button
        btnBackupDB.setOnClickListener(View.OnClickListener {
            Toast.makeText(getContext(), "Download db started", Toast.LENGTH_LONG).show()
            val fileDownloader = DownloadFileFromURL(object : FileDownloadListener() {
                fun onFileDownloaded() {
                    Toast.makeText(getContext(), R.string.download_finished, Toast.LENGTH_LONG).show()
                }
            })
            fileDownloader.execute(URLs.BASE_URL + URLs.DB_BACKUP, getString(R.string.database_name))
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                Log.d("DOWN_ERR", "SD \n" + Environment.getExternalStorageState())
                return@OnClickListener
            }
        })

        cbOfflineMode = getView()!!.findViewById(R.id.cbOfflineMode) as CheckBox
        cbOfflineMode.isChecked = useOfflineMode!!
        Toast.makeText(getContext(), if (useOfflineMode) getString(R.string.offline_mode_enabled) else getString(R.string.offline_mode_disabled), Toast.LENGTH_LONG).show()
        cbOfflineMode.setOnClickListener {
            val context = getContext()
            val manager = SettingsManager.getInstance(context)
            var useOfflineMode: Boolean?
            useOfflineMode = cbOfflineMode.isChecked
            if (useOfflineMode) {
                val handler = SQLHandler.getInstance(context)
                if (handler.isDatabaseOpen())
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

        btnUpladPendingOps = getView()!!.findViewById(R.id.btnUploadPendingOps) as Button
        btnUpladPendingOps.setOnClickListener {
            val log = OperationsLog.getInstance(context)
            log.pushOperations()
        }
    }
}// Required empty public constructor
