package com.mobilerp.quimera.mobilerp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.mobilerp.quimera.mobilerp.Adapters.SalesItemAdapter
import com.mobilerp.quimera.mobilerp.ApiModels.SalesItemModel
import com.mobilerp.quimera.mobilerp.OfflineMode.Insert
import com.mobilerp.quimera.mobilerp.OfflineMode.OperationsLog
import com.mobilerp.quimera.mobilerp.OfflineMode.Select
import com.mobilerp.quimera.mobilerp.OnlineMode.Server
import com.mobilerp.quimera.mobilerp.OnlineMode.URLs
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

class FinishSell : Fragment() {

    internal val me: Fragment = this
    internal lateinit var appState: AppState
    // Objects
    private lateinit var items: ArrayList<SalesItemModel>
    private lateinit var salesItemAdapter: SalesItemAdapter
    private lateinit var itemsListModel: ArrayList<SalesItemModel>
    private lateinit var log: OperationsLog

    private fun itemSaleClicked(item: SalesItemModel) {
        Log.d("DATA_CLICKED_LOG", "${item.name} ${item.units}")
        val data = item.name
        Toast.makeText(context, "clicked: $data", Toast.LENGTH_LONG).show()
    }

    private fun initUI() {
        activity.setTitle(R.string.finish_sale)
        appState = AppState.getInstance(context)
        log = OperationsLog.getInstance(context)
        itemsListModel = ArrayList()
        itemSalesList.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        itemSalesList.adapter = salesItemAdapter

        finish_sale.setOnClickListener {
            val data = JsonObject()
            val barcode = JsonArray<String>()
            val units = JsonArray<Int>()
            val is_service = JsonArray<Int>()
            for (i in items.indices) {
                barcode.add(i,items[i].barcode)
                units.add(i, items[i].units)
                is_service.add(i, items[i].isService)
            }
            data.put("barcode", barcode)
            data.put("units", units)
            data.put("is_service", is_service)
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
                    for (i in items.indices) {
                        q = String.format("INSERT INTO " +
                                "SaleDetails(idSale, " +
                                "idProduct," +
                                " " +
                                "productPrice, units) VALUES (%d, '%s', %f, %d);", lastID, items[i].barcode, items[i].price, items[i].units)
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
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            items = arguments.getParcelableArrayList("SalesData")
            salesItemAdapter = SalesItemAdapter(items) { item: SalesItemModel -> itemSaleClicked(item) }
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

        for (item in items) {
            val jsonObject = JsonObject()
            jsonObject.put("name", item.name)
            jsonObject.put("price", item.price)
            jsonObject.put("units", item.units)
            itemsListModel.add(SalesItemModel(item.barcode, item.units, item.price, item.name, 0))
            total_sale += item.price * item.units
        }

        itemSalesList.adapter = salesItemAdapter

        totalSale.text = getString(R.string.total_sale) + " : " + total_sale.toString()
        itemSalesList.setHasFixedSize(true)
    }

    companion object {

        fun newInstance(_items: ArrayList<SalesItemModel>): FinishSell {
            val fragment = FinishSell()
            val args = Bundle()
            args.putParcelableArrayList("SalesData", _items)
            fragment.arguments = args
            return fragment
        }
    }
}
