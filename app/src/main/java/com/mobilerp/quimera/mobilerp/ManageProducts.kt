package com.mobilerp.quimera.mobilerp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.camera.CameraSettings
import com.mobilerp.quimera.mobilerp.ApiModels.ProductModel
import com.mobilerp.quimera.mobilerp.OfflineMode.*
import com.mobilerp.quimera.mobilerp.OnlineMode.Server
import com.mobilerp.quimera.mobilerp.OnlineMode.URLs
import kotlinx.android.synthetic.main.fragment_manage_products.*
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

/**
 * A simple [Fragment] subclass.
 */
class ManageProducts : Fragment(){

    private var isNewProduct: Boolean = false
    internal var lastBarcode: String = ""
    private var isOfflineEnabled: Boolean = false
    private val appState: AppState by lazy { AppState.getInstance(context) }
    private lateinit var log: OperationsLog
    internal lateinit var beepManager: BeepManager
    private lateinit var settings: CameraSettings
    internal lateinit var server: Server


    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastBarcode) {
                return
            }

            lastBarcode = result.text
            barcodePreview.setStatusText(lastBarcode)
            beepManager.playBeepSoundAndVibrate()
            findLastScannedProduct(result.text)
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_manage_products, container, false)
    }

    override fun onViewCreated(view: View?, savedInstance: Bundle?) {
        // Offline Mode
        isOfflineEnabled = appState.isOfflineMode

        // Camera settings
        settings = CameraSettings()
        settings.focusMode = CameraSettings.FocusMode.MACRO

        //Barcode settings
        barcodePreview.barcodeView.cameraSettings = settings
        barcodePreview.decodeContinuous(callback)

        beepManager = BeepManager(activity)

        //Server init
        server = Server(context)

        // -- INIT elements to display items

        tvBarcode.setText(R.string.item_barcode)
        tvPrice.setText(R.string.item_price)
        tvNewUnits.setText(R.string.item_total)

        etName.isEnabled = false
        etPrice.isEnabled = false
        etNewUnits.isEnabled = false

        // ----- END OF INIT -----

        if (isOfflineEnabled) {
            Toast.makeText(context, getString(R
                    .string.offline_mode_enabled), Toast.LENGTH_LONG).show()
            log = OperationsLog.getInstance(context)
        } else {
            Toast.makeText(context, getString(R
                    .string.offline_mode_disabled), Toast.LENGTH_LONG).show()
        }

        btnSave.setOnClickListener{
            val data = prepareJSON()
            when (isOfflineEnabled) {
                true -> {

                }
                false -> {
                    when (isNewProduct) {
                        true -> {
                            server.postRequest(URLs.NEW_PRODUCT, data, success = {
                                cleanEntries()
                            }, failure = {
                                server.genericErrors(it.response.statusCode)
                            })
                            isNewProduct = false
                        }
                        false ->
                            server.putRequest(URLs.UPDATE_PRODUCT + lastBarcode, data, success = {
                                cleanEntries()
                            }, failure = {
                                server.genericErrors(it.response.statusCode)
                            })
                    }
                }
            }
        }

        etBarcode.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                lastBarcode = etBarcode.text.toString()
                findLastScannedProduct(lastBarcode)
                return@OnKeyListener true
            }
            false
        })
    }


    private fun findLastScannedProduct(barcode: String) {
        etBarcode.setText(barcode)
        when (isOfflineEnabled) {
            true -> {
                isNewProduct = false
                val db = SQLHandler.getInstance(context)
                if (db.isDatabaseOpen) {
                    val select = Select(context)
                    select.query = "SELECT name, price FROM ProductModel WHERE barcode='$barcode'"
                    if (select.execute()) {
                        if (select.results.count > 0) {
                            Toast.makeText(context, getString(R
                                    .string.app_op_success), Toast.LENGTH_LONG).show()
                            etName.setText(select.results.getString(0))
                            etPrice.setText(select.results.getFloat(1).toString())
//                            etExistingUnits.setText(select.results.gets)
                            enableEntries()
                        } else {
                            isNewProduct = true
                            Toast.makeText(context, getString(R.string.app_err_not_found), Toast
                                    .LENGTH_LONG).show()
                            enableEntries()
                        }
                    }
                }
            }
            false -> {
                server.getRequest(URLs.FIND_PRODUCT + "${appState.currentStore}/$barcode",
                        success = {response ->
                            cleanEntries()
                            val product = response.map{ ProductModel(response.obj("mobilerp")!!) }
                            etName.setText(product[0].name)
                            etPrice.setText(product[0].price.toString())
                            etTotalUnits.setText(product[0].units.toString())
                            etBarcode.setText(product[0].barcode)
                            enableEntries()
                        },
                        failure = {
                            server.genericErrors(it.response.statusCode)
                            isNewProduct = true
                            enableEntries()
                })
            }
        }
    }

    private fun prepareJSON(): JsonObject {
        val data = JsonObject()
        data.put("name", etName.text.toString())
        data.put("price", etPrice.text.toString())
        data.put("units", etNewUnits.text.toString())
        data.put("token", Calendar.getInstance().time.toString())
        data.put("storeid", AppState.getInstance(context).currentStore)
        if (isNewProduct)
            data.put("barcode", lastBarcode)
        return data
    }

    private fun enableEntries() {
        etPrice.isEnabled = true
        etNewUnits.isEnabled = true
        etName.isEnabled = true
        if (isNewProduct) {
            etName.setText(R.string.new_item)
            etName.requestFocus()
        } else{
            etNewUnits.requestFocus()
        }
    }

    private fun cleanEntries() {
        Toast.makeText(context, R.string.srv_op_success, Toast.LENGTH_LONG).show()
        etName.setText("")
        etPrice.setText("")
        etNewUnits.setText("")
        etBarcode.setText("")
        etTotalUnits.setText("")
        etName.isEnabled = false
        etPrice.isEnabled = false
        etNewUnits.isEnabled = false
    }

    override fun onResume() {
        super.onResume()
        barcodePreview.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodePreview.pause()
    }

    fun pause(view: View) {
        barcodePreview.pause()
    }

    fun resume(view: View) {
        barcodePreview.resume()
    }

    fun triggerScan(view: View) {
        barcodePreview.decodeSingle(callback)
    }

}
