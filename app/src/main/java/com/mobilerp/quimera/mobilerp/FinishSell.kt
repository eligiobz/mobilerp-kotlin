package com.mobilerp.quimera.mobilerp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.mobilerp.quimera.mobilerp.Adapters.ProductListAdapter
import com.mobilerp.quimera.mobilerp.ApiModels.ProductModel
import com.mobilerp.quimera.mobilerp.offline_mode.Insert
import com.mobilerp.quimera.mobilerp.offline_mode.OperationsLog
import com.mobilerp.quimera.mobilerp.offline_mode.Select
import com.mobilerp.quimera.mobilerp.online_mode.Server
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import kotlinx.android.synthetic.main.fragment_finish_sell.*
import java.util.*
import kotlin.collections.ArrayList

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
 * Activities that contain this fragment must implement the
 * [FinishSell.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FinishSell.newInstance] factory method to
 * create an instance of this fragment.
 */
class FinishSell : Fragment() {

    internal val me: Fragment = this
    internal lateinit var appState: AppState
    // Objects
    private lateinit var items: ArrayList<SalesItem>
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var itemsListModel: ArrayList<ProductModel>
    private lateinit var log: OperationsLog

    private fun initUI() {
        activity.setTitle(R.string.finish_sale)
        appState = AppState.getInstance(context)
        log = OperationsLog.getInstance(context)
        itemsListModel = ArrayList()

        finish_sale.setOnClickListener {
            val data = JsonObject()
            val barcode = JsonArray<String>()
            val units = JsonArray<Int>()
            for (i in items.indices) {
                barcode.add(i,items[i].barcode)
                units.add(i, items[i].amount)
            }
            data.put("barcode", barcode)
            data.put("units", units)
            data.put("token", Calendar.getInstance().time.toString())
            data.put("storeid", AppState.getInstance(context).currentStore)

            if (appState.isOfflineMode) {
                // -- OFFLINE OPERATION --
                var q = "INSERT INTO Sale(date) values(DATETIME('now','localtime'));"
                val insert = Insert(context)
                insert.query = q
                var lastID = -1
                if (insert.execute()) {
                    val select = Select(context)
                    select.query = "SELECT MAX(id) FROM Sale;"
                    if (select.execute())
                        if (select.results.count > 0)
                            lastID = select.results.getInt(0)
                }
                if (lastID != -1) {
                    for (i in items!!.indices) {
                        q = String.format("INSERT INTO " +
                                "SaleDetails(idSale, " +
                                "idProduct," +
                                " " +
                                "productPrice, units) VALUES (%d, '%s', %f, %d);", lastID, items!![i].barcode, items!![i].price, items!![i].amount)
                        insert.query = q
                        insert.execute()
                    }
//                    log.add(Request.Method.POST, URLs.MAKE_SALE, data)
                    appState.flushContext()
                    Toast.makeText(context, R.string.app_op_success, Toast
                            .LENGTH_LONG).show()
                    activity.setTitle(R.string.manager)
                    activity.supportFragmentManager
                            .beginTransaction()
                            .remove(me)
                            .commit()
                }

            } else {
                val server = Server(context)
                server.postRequest(URLs.MAKE_SALE, data, success = {
                    appState.flushContext()
                    Toast.makeText(context, R.string.srv_op_success, Toast
                            .LENGTH_LONG).show()
                    activity.setTitle(R.string.manager)
                    activity.supportFragmentManager
                            .beginTransaction()
                            .remove(me)
                            .commit()
                }, failure = {
                    server.genericErrors(it.response.statusCode)
                })
                // -- ONLINE OPERATION --
//                val apiServer = APIServer(context)
//
//                apiServer.getResponse(Request.Method.POST, URLs.BASE_URL + URLs.MAKE_SALE,
//                        data, object : VolleyCallback {
//                    override fun onSuccessResponse(result: JSONObject) {
//                        appState.flushContext()
//                        Toast.makeText(context, R.string.srv_op_success, Toast
//                                .LENGTH_LONG).show()
//                        activity.setTitle(R.string.manager)
//                        activity.supportFragmentManager
//                                .beginTransaction()
//                                .remove(me)
//                                .commit()
//
//                    }
//
//                    override fun onErrorResponse(error: VolleyError) {
//                        Toast.makeText(context, R.string.srv_op_fail, Toast
//                                .LENGTH_LONG).show()
//                    }
//                })
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            items = arguments.getParcelableArrayList("SalesData")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_finish_sell, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

        var total_sale = 0.0
        for (i in items!!.indices) {
//            itemsListModel.add(JsonObject(items!![i].name, items!![i].price, items!![i]
//                        .amount))
            total_sale += items!![i].price!! * items!![i].amount
        }
        productListAdapter = ProductListAdapter(context, itemsListModel, R.layout.product_sale_check_row)
        itemSalesList.adapter = productListAdapter
        totalSale.text = getString(R.string.total_sale) + " : " + total_sale.toString()
    }

    companion object {

        fun newInstance(_items: ArrayList<SalesItem>): FinishSell {
            val fragment = FinishSell()
            val args = Bundle()
            args.putParcelableArrayList("SalesData", _items)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
