package com.mobilerp.quimera.mobilerp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.VolleyError
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.NoSuchAlgorithmException
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
 * Activities that contain this fragment must implement the
 * [FinishSell.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FinishSell.newInstance] factory method to
 * create an instance of this fragment.
 */
class FinishSell : Fragment() {

    internal val me: Fragment = this
    internal val appState = AppState.getInstance(context)
    // Objects
    internal var tvTotalSale: TextView
    internal var items: ArrayList<SalesItem>? = null
    internal var itemListAdapter: ItemListAdapter
    internal var lvSalesItems: ListView
    internal var itemsListModel: ArrayList<ItemListModel>
    internal var btnEndSale: Button
    internal var log: OperationsLog
    private var mListener: OnFragmentInteractionListener? = null

    private fun initUI() {
        activity.setTitle(R.string.finish_sale)
        log = OperationsLog.getInstance(context)
        tvTotalSale = view!!.findViewById(R.id.totalSale) as TextView
        lvSalesItems = view!!.findViewById(R.id.itemSalesList) as ListView
        btnEndSale = view!!.findViewById(R.id.finish_sale) as Button
        itemsListModel = ArrayList<ItemListModel>()


        btnEndSale.setOnClickListener {
            val data = JSONObject()
            try {
                val barcode = JSONArray()
                val units = JSONArray()
                for (i in items!!.indices) {
                    barcode.put(i, items!![i].barcode)
                    units.put(i, items!![i].amount)
                }
                data.put("barcode", barcode)
                data.put("units", units)
                try {
                    val md5digest = java.security.MessageDigest.getInstance("MD5")
                    val c_time = Calendar.getInstance().time
                    data.put("token", c_time.toString())
                } catch (e: NoSuchAlgorithmException) {
                    Log.d("MD5 Error", e.toString())
                }

            } catch (e: JSONException) {
                Log.d("JSON ERROR", e.message)
            }

            if (appState.isOfflineMode()) {
                // -- OFFLINE OPERATION --
                var q = "INSERT INTO Sale(date) values(DATETIME('now','localtime'));"
                val insert = Insert(context)
                insert.setQuery(q)
                var lastID = -1
                if (insert.execute()) {
                    val select = Select(context)
                    select.setQuery("SELECT MAX(id) FROM Sale;")
                    if (select.execute())
                        if (select.results.getCount() > 0)
                            lastID = select.results.getInt(0)
                }
                if (lastID != -1) {
                    for (i in items!!.indices) {
                        q = String.format("INSERT INTO " +
                                "SaleDetails(idSale, " +
                                "idProduct," +
                                " " +
                                "productPrice, units) VALUES (%d, '%s', %f, %d);", lastID, items!![i].barcode, items!![i].price, items!![i].amount)
                        insert.setQuery(q)
                        insert.execute()
                    }
                    log.add(Request.Method.POST, URLs.MAKE_SALE, data)
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
                // -- ONLINE OPERATION --
                val apiServer = APIServer(context)
                val URL = URLs.getInstance()

                apiServer.getResponse(Request.Method.POST, URLs.BASE_URL + URLs.MAKE_SALE,
                        data, object : VolleyCallback() {
                    fun onSuccessResponse(result: JSONObject) {
                        appState.flushContext()
                        Toast.makeText(context, R.string.srv_op_success, Toast
                                .LENGTH_LONG).show()
                        activity.setTitle(R.string.manager)
                        activity.supportFragmentManager
                                .beginTransaction()
                                .remove(me)
                                .commit()

                    }

                    fun onErrorResponse(error: VolleyError) {
                        Toast.makeText(context, R.string.srv_op_fail, Toast
                                .LENGTH_LONG).show()
                    }
                })
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
        if (items != null) {
            var total_sale = 0.0
            for (i in items!!.indices) {
                itemsListModel.add(ItemListModel(items!![i].name, items!![i].price, items!![i].amount))
                total_sale += items!![i].price * items!![i].amount
            }
            itemListAdapter = ItemListAdapter(context, itemsListModel, R.layout.item_sales_row)
            lvSalesItems.adapter = itemListAdapter
            tvTotalSale.text = getString(R.string.total_sale) + " : " + total_sale.toString()
        } else {
            tvTotalSale.setText(R.string.data_fail)
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
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
