package com.mobilerp.quimera.mobilerp

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.camera.CameraSettings
import com.mobilerp.quimera.mobilerp.offline_mode.SQLHandler
import com.mobilerp.quimera.mobilerp.offline_mode.Select
import com.mobilerp.quimera.mobilerp.online_mode.APIServer
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import com.mobilerp.quimera.mobilerp.online_mode.VolleyCallback
import kotlinx.android.synthetic.main.fragment_sales.*
import org.json.JSONException
import org.json.JSONObject
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

    internal var lastBarcode: String = ""
    private var totalSale: Double = 0.0
    internal var isNewProduct: Boolean = false

    internal lateinit var apiServer: APIServer
    private lateinit var items: ArrayList<SalesItem>
    private lateinit var appState: AppState

    internal lateinit var context: Context

    internal lateinit var beepManager: BeepManager
    private lateinit var settings: CameraSettings


    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastBarcode) {
                return
            }

            lastBarcode = result.text
            barcodePreview.setStatusText(lastBarcode)
            beepManager.playBeepSoundAndVibrate()
            findLastScannedProduct()

        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {

        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context = getContext()

        activity.setTitle(R.string.new_sale)

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

        //Server init
        apiServer = APIServer(context)

        // Init elements to display items

        addProduct.setOnClickListener { addProduct() }

        endSale.setOnClickListener { endSale() }

        items = ArrayList()
        totalSale = 0.0
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_sales, container, false)
    }

    private fun addProduct() {
        val amount = Integer.parseInt(tvAmountValue.text.toString())
        val price = java.lang.Double.parseDouble(tvPriceValue.text.toString())
        val name = tvName.text.toString()
        items.add(SalesItem(lastBarcode, amount, price, name))
        totalSale += items[items.size - 1].price!! * amount
        tvTotalSaleValue.text = totalSale.toString()
        Toast.makeText(context, tvName.text.toString() + " " + getString(R.string.added), Toast.LENGTH_LONG).show()
    }

    private fun endSale() {
        appState.flushContext()
        val fragment = FinishSell.newInstance(items)
        val manager = activity.supportFragmentManager
        manager.beginTransaction()
                .replace(R.id.main_content, fragment)
                .addToBackStack("Sales")
                .commit()
    }

    private fun findLastScannedProduct() {
        // -- OFFLINE SEARCH --
        if (appState.isOfflineMode) {
            val db = SQLHandler.getInstance(getContext())
            if (db.isDatabaseOpen) {
                val select = Select(getContext())
                select.query = "SELECT name, price FROM Product WHERE barcode='$lastBarcode'"
                if (select.execute()) {
                    if (select.results.count > 0) {
                        Toast.makeText(getContext(), getString(R
                                .string.app_op_success), Toast.LENGTH_LONG).show()
                        tvName.text = select.results.getString(0)
                        tvPriceValue.text = select.results.getFloat(1).toString()
                        tvAmountValue.setText("1")
                    }
                }
            }
        } else {
            // -- ONLINE SEARCH --
            apiServer.getResponse(Request.Method.GET, URLs.BASE_URL + URLs.FIND_PRODUCT + lastBarcode, null, object : VolleyCallback {
                override fun onSuccessResponse(result: JSONObject) {
                    isNewProduct = false
                    try {
                        val items_ = result.getJSONArray("mobilerp")
                        val item_ = items_.getJSONObject(0)
                        tvName.text = item_.getString("name")
                        tvPriceValue.text = item_.getString("price")
                        tvAmountValue.setText("1")
                    } catch (e: JSONException) {
                        Toast.makeText(context, R.string.srv_err_404_not_found, Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }

                }

                override fun onErrorResponse(error: VolleyError) {
                    apiServer.genericErrors(error.networkResponse.statusCode)
                }
            })
        }
    }

    private fun cleanEntries() {
        Toast.makeText(context, R.string.srv_op_success, Toast.LENGTH_LONG).show()
        tvName.text = ""
        tvPriceValue.text = ""
        tvTotalSaleValue.text = ""
        tvAmountValue.setText("")
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
