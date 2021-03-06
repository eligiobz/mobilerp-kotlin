package com.mobilerp.quimera.mobilerp

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.camera.CameraSettings
import com.mobilerp.quimera.mobilerp.ApiModels.SalesItemModel
import com.mobilerp.quimera.mobilerp.OfflineMode.SQLHandler
import com.mobilerp.quimera.mobilerp.OfflineMode.Select
import com.mobilerp.quimera.mobilerp.OnlineMode.Server
import com.mobilerp.quimera.mobilerp.OnlineMode.URLs
import kotlinx.android.synthetic.main.fragment_sales.*
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
class SalesFragment : Fragment() {

    private var totalSale: Double = 0.0
    internal var barcode: String = ""
    val server : Server by lazy { Server(context) }

    private lateinit var items: ArrayList<SalesItemModel>
    private lateinit var appState: AppState
    private lateinit var settings: CameraSettings
    internal lateinit var context: Context
    internal lateinit var beepManager: BeepManager
    private var wasService = 0

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == barcode) {
                return
            }

            barcode = result.text
            barcodePreview.setStatusText(barcode)
            beepManager.playBeepSoundAndVibrate()
            findLastScannedProduct()
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context = activity!!.applicationContext!!

        activity!!.setTitle(R.string.new_sale)

        // Camera settings
        settings = CameraSettings()
        settings.focusMode = CameraSettings.FocusMode.MACRO

        //Barcode settings
        barcodePreview.barcodeView.cameraSettings = settings
        barcodePreview.decodeContinuous(callback)
        beepManager = BeepManager(activity)

        // Get settings
        appState = AppState.getInstance(context)
        if (appState.isOfflineMode)
            Toast.makeText(context, R.string.offline_mode_enabled, Toast.LENGTH_LONG).show()

        // Buttons
        addProduct.setOnClickListener { addProduct() }
        endSale.setOnClickListener { endSale() }

        etBarcode.setOnKeyListener(View.OnKeyListener{ _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER){
                barcode = etBarcode.text.toString()
                findLastScannedProduct()
                return@OnKeyListener true
            }

            return@OnKeyListener false
        })

        items = ArrayList()
        totalSale = 0.0


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales, container, false)
    }

    /**
     * Adds product to sale
     */
    private fun addProduct() {
        val amount = Integer.parseInt(etTotalUnits.text.toString())
        val price = java.lang.Double.parseDouble(etPrice.text.toString())
        val name = etName.text.toString()
        items.add(SalesItemModel(barcode, amount, price, name, wasService))
        totalSale += items[items.size - 1].price * amount
        etTotalSale.setText(totalSale.toString())
        cleanEntries()
        Toast.makeText(context, tvName.text.toString() + " " + getString(R.string.added), Toast.LENGTH_LONG).show()
    }

    private fun endSale() {
        appState.flushContext()
        val fragment = FinishSell.newInstance(items)
        val manager = activity!!.supportFragmentManager
        manager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .addToBackStack("Sales")
                .commit()
    }

    private fun findLastScannedProduct() {
        // -- OFFLINE SEARCH --
        when (appState.isOfflineMode) {
            true -> {
                val db = SQLHandler.getInstance(context)
                if (db.isDatabaseOpen) {
                    val select = Select(context)
                    select.query = "SELECT name, price FROM ProductModel WHERE barcode='$barcode'"
                    if (select.execute()) {
                        if (select.results.count > 0) {
                            Toast.makeText(getContext(), getString(R
                                    .string.app_op_success), Toast.LENGTH_LONG).show()
                            tvName.text = select.results.getString(0)
                            tvPrice.text = select.results.getFloat(1).toString()
                            tvTotalUnits.text = "1"
                        }
                    }
                }
            }
            false -> {
                server.getRequest(URLs.FIND_ARTICLE + "$barcode/${appState.currentStore}",
                        success = {
                            val item = it.obj("mobilerp")!!
                            etBarcode.setText(item.string("barcode"))
                            etName.setText(item.string("name"))
                            etPrice.setText(item.float("price").toString())
                            etTotalUnits.setText("1")
                            etTotalUnits.requestFocus()
                            when (item.containsKey("units")){
                                true -> wasService = 0
                                false -> wasService = 1
                            }
                        }, failure = {
                    server.genericErrors(it.response.statusCode)
                })
            }
        }
    }

    private fun cleanEntries() {
        Toast.makeText(context, R.string.srv_op_success, Toast.LENGTH_LONG).show()
        etName.setText("")
        etPrice.setText("")
//        tvTotalSale.text = ""
        etTotalUnits.setText("")
    }

    override fun onResume() {
        super.onResume()
        barcodePreview.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodePreview.pause()
    }
}// Required empty public constructor
