package com.mobilerp.quimera.mobilerp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
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
import com.mobilerp.quimera.mobilerp.offline_mode.*
import com.mobilerp.quimera.mobilerp.online_mode.APIServer
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import com.mobilerp.quimera.mobilerp.online_mode.VolleyCallback
import kotlinx.android.synthetic.main.fragment_stock_update.*
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
class StockUpdate : Fragment(), View.OnClickListener {

    internal var isNewProduct: Boolean = false
    internal lateinit var beepManager: BeepManager
    internal var lastBarcode: String = ""
    internal lateinit var settings: CameraSettings
    internal lateinit var apiServer: APIServer
    internal var isOfflineEnabled: Boolean = false
    internal lateinit var log: OperationsLog

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
        return inflater!!.inflate(R.layout.fragment_stock_update, container, false)
    }

    override fun onViewCreated(view: View?, savedInstance: Bundle?) {
        // Offline Mode
        isOfflineEnabled = SettingsManager.getInstance(context).getBoolean(getString(R.string
                .use_offline_mode))!!

        // Camera settings
        settings = CameraSettings()
        settings.focusMode = CameraSettings.FocusMode.MACRO

        //Barcode settings
        barcodePreview.barcodeView.cameraSettings = settings
        barcodePreview.decodeContinuous(callback)

        beepManager = BeepManager(activity)

        //Server init
        apiServer = APIServer(context)

        // -- INIT elements to display items

        tvBarcode.setText(R.string.item_barcode)
        tvPrice.setText(R.string.item_price)
        tvTotal.setText(R.string.item_total)

        etName.isEnabled = false
        etPrice.isEnabled = false
        etTotal.isEnabled = false

        // ----- END OF INIT -----

        if (isOfflineEnabled) {
            Toast.makeText(context, getString(R
                    .string.offline_mode_enabled), Toast.LENGTH_LONG).show()
            log = OperationsLog.getInstance(context)
        } else {
            Toast.makeText(context, getString(R
                    .string.offline_mode_disabled), Toast.LENGTH_LONG).show()
        }

        btnSave.setOnClickListener(this)
    }

    private fun findLastScannedProduct(barcode: String) {
        tvBarcodeValue.text = barcode
        // -- FIND SCANNED PRODUCT ONLINE --
        if (!isOfflineEnabled) {
            apiServer.getResponse(Request.Method.GET, URLs.BASE_URL + URLs.FIND_PRODUCT + barcode, null, object : VolleyCallback {
                override fun onSuccessResponse(result: JSONObject) {
                    isNewProduct = false
                    try {
                        val _itms = result.getJSONArray("mobilerp")
                        val _itm = _itms.getJSONObject(0)
                        etName.setText(_itm.getString("name"))
                        etPrice.setText(_itm.getString("price"))
                        enableEntries()
                    } catch (e: JSONException) {
                        Toast.makeText(context, R.string.srv_err_404_not_found, Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }

                }

                override fun onErrorResponse(error: VolleyError) {
                    val response = error.networkResponse
                    if (response.statusCode == 404) {
                        Toast.makeText(context, R.string.srv_err_404_not_found, Toast.LENGTH_LONG).show()
                        isNewProduct = true
                        enableEntries()
                    } else {
                        apiServer.genericErrors(response.statusCode)
                    }
                }
            })
        } else {
            // -- FIND SCANNED PRODUCT OFFLINE --
            isNewProduct = false
            val db = SQLHandler.getInstance(context)
            if (db.isDatabaseOpen) {
                val select = Select(context)
                select.query = "SELECT name, price FROM Product WHERE barcode='$barcode'"
                if (select.execute()) {
                    if (select.results.count > 0) {
                        Toast.makeText(context, getString(R
                                .string.app_op_success), Toast.LENGTH_LONG).show()
                        etName.setText(select.results.getString(0))
                        etPrice.setText(select.results.getFloat(1).toString())
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
    }

    override fun onClick(v: View) {
        var jsonObject = JSONObject()
        jsonObject = prepareJSON()
        // -- FIND SCANNED PRODUCT ONLINE --
        if (!isOfflineEnabled) {
            if (isNewProduct) {
                // -- NEW PRODUCT SAVED TO SERVER --
                apiServer.getResponse(Request.Method.POST, URLs.BASE_URL + URLs.NEW_PRODUCT,
                        jsonObject, object : VolleyCallback {
                    override fun onSuccessResponse(result: JSONObject) {
                        cleanEntries()
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        Toast.makeText(context, R.string.srv_op_fail, Toast.LENGTH_LONG).show()
                    }
                })
            } else {
                // -- PRODUCT UPDATED TO SERVER --
                apiServer.getResponse(Request.Method.PUT, URLs.BASE_URL + URLs
                        .UPDATE_PRODUCT + lastBarcode,
                        jsonObject, object : VolleyCallback {
                    override fun onSuccessResponse(result: JSONObject) {
                        cleanEntries()
                    }

                    override fun onErrorResponse(error: VolleyError) {
                        Toast.makeText(context, R.string.srv_op_fail, Toast.LENGTH_LONG).show()
                    }
                })
            }
        } else {
            // -- FIND PRODUCT OFFLINE --
            if (isNewProduct) {
                // -- NEW PRODUCT SAVED LOCALLY --
                // -- OPERATION ADDED TO LOGFILE --
                log.add(Request.Method.POST, URLs.NEW_PRODUCT, jsonObject)
                cleanEntries()
                val insert = Insert(context)
                try {
                    val q = String.format(Locale.getDefault(), "INSERT INTO Product(barcode," +
                            " name, " +
                            "price, " +
                            "units) " +
                            "values('%s', '%s', %s, %s);", jsonObject.getString("barcode"),
                            jsonObject.getString("name"), jsonObject.getString("price"), jsonObject
                            .getString("units"))
                    insert.query = q
                    insert.execute()
                    Log.d("SQL Query :: ", insert.query)
                    Toast.makeText(context, R.string.app_op_success, Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    Log.d("JSON_EXEC", e.message)
                }

            } else {
                // -- PRODUCT UPDATED LOCALLY --
                // -- OPERATION SAVED TO LOG--
                // TODO: ADD UPDATE TO DB -- DONE??
                log.add(Request.Method.PUT, URLs
                        .UPDATE_PRODUCT + lastBarcode, jsonObject)
                cleanEntries()
                val update = Update(context)
                try {
                    val q = String.format(Locale.getDefault(), "UPDATE Product " +
                            "SET name='%s', " +
                            "price=%s, " +
                            "units=%s " +
                            "WHERE barcode='%s';",
                            jsonObject.getString("name"),
                            jsonObject.getString("price"),
                            jsonObject.getString("units"),
                            jsonObject.getString("barcode"))
                    update.query = q
                    update.execute()
                    Toast.makeText(context, R.string.app_op_success, Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    Toast.makeText(context, R.string.app_op_fail, Toast.LENGTH_LONG).show()
                }

            }
        }
    }

    private fun prepareJSON(): JSONObject {
        val `object` = JSONObject()
        try {
            if (isNewProduct) {
                `object`.put("barcode", lastBarcode)
                `object`.put("name", etName.text)
                `object`.put("price", etPrice.text)
                `object`.put("units", etTotal.text)
            } else {
                `object`.put("price", etPrice.text)
                `object`.put("units", etTotal.text)
            }
            `object`.put("token", Calendar.getInstance().time.toString())
            return `object`
        } catch (ex: JSONException) {
            Log.d("JSON_ERROR", ex.toString())
        }

        return `object`
    }

    private fun enableEntries() {
        etPrice.isEnabled = true
        etTotal.isEnabled = true
        if (isNewProduct) {
            etName.setText(R.string.new_item)
            etName.isEnabled = true
        }
    }

    private fun cleanEntries() {
        Toast.makeText(context, R.string.srv_op_success, Toast.LENGTH_LONG).show()
        etName.setText("")
        etPrice.setText("")
        etTotal.setText("")
        tvBarcodeValue.text = ""
        etName.isEnabled = false
        etPrice.isEnabled = false
        etTotal.isEnabled = false
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

    //    @Override
    //    public boolean onKeyDown(int keyCode, KeyEvent event) {
    //        return barcodePreview.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    //    }

}// Required empty public constructor
