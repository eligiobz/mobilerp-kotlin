package com.mobilerp.quimera.mobilerp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.VolleyError
import com.mobilerp.quimera.mobilerp.online_mode.APIServer
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import com.mobilerp.quimera.mobilerp.online_mode.VolleyCallback
import com.mobilerp.quimera.mobilerp.online_mode.iterator
import kotlinx.android.synthetic.main.fragment_list_items.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ListItems : Fragment() {

    internal lateinit var items: ArrayList<ItemListModel>
    internal lateinit var apiServer: APIServer
    private var endpoint: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            endpoint = arguments.getString("ENDPOINT")

        apiServer = APIServer(context)
        items = ArrayList()

        when (endpoint) {
            "LISTPRODUCTS" -> {
                listProducts()
                activity.setTitle(R.string.drug_list)
            }
            "LISTDEPLETED" -> {
                listDepleted()
                activity.setTitle(R.string.depleted_stock)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_list_items, container, false)
    }

    private fun listProducts() {
        val url = URLs.BASE_URL + URLs.LIST_PRODUCTS + AppState.getInstance(context).currentStore
        apiServer.getResponse(Request.Method.GET, url, null, object : VolleyCallback {
            override fun onSuccessResponse(result: JSONObject) {
                try {
                    items.add(ItemListModel("title"))
                    for (item_: JSONObject in result.getJSONArray("mobilerp"))
                        //name, price, total
                        items.add(ItemListModel(item_.getString("name"), item_.getDouble("price"), item_.getInt("units")))

                    itemList.adapter = ItemListAdapter(context, items, R.layout.item_row)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(error: VolleyError) {
                apiServer.genericErrors(error.networkResponse.statusCode)
            }
        })
    }

    private fun listDepleted() {
        val url = URLs.BASE_URL + URLs.LIST_DEPLETED + AppState.getInstance(context).currentStore
        apiServer.getResponse(Request.Method.GET, url, null, object : VolleyCallback {
            override fun onSuccessResponse(result: JSONObject) {
                try {
                    items.add(ItemListModel("title_"))
                    for (item_: JSONObject in result.getJSONArray("mobilerp"))
                        //name, price, total
                        items.add(ItemListModel(item_.getString("name"), item_.getString("date")))

                    itemList.adapter = ItemListAdapter(context, items, R.layout.item_row)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onErrorResponse(error: VolleyError) {
                apiServer.genericErrors(error.networkResponse.statusCode)
            }
        })
    }

    companion object {

        fun newInstance(endpoint: String): ListItems {
            val fragment = ListItems()
            val args = Bundle()
            args.putString("ENDPOINT", endpoint)
            fragment.arguments = args
            return fragment
        }
    }
}
