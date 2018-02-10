package com.mobilerp.quimera.mobilerp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beust.klaxon.JsonObject
import com.mobilerp.quimera.mobilerp.ApiModels.ProductModel
import com.mobilerp.quimera.mobilerp.online_mode.APIServer
import com.mobilerp.quimera.mobilerp.online_mode.Server
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import kotlinx.android.synthetic.main.fragment_list_items.*
import java.util.*

class ListItems : Fragment() {

    internal lateinit var items: ArrayList<ProductModel>
    internal lateinit var apiServer: APIServer
    private var endpoint: String? = null
    internal lateinit var server: Server

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            endpoint = arguments.getString("ENDPOINT")

        apiServer = APIServer(context)
        server = Server(context)
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
        server.getRequest(URLs.LIST_PRODUCTS + AppState.getInstance(context).currentStore,
                success = { response ->
                    items.add(ProductModel("title"))
                    for (item: JsonObject in response.array<JsonObject>("mobilerp")!!) {
                        items.add(ProductModel(item))
                    }

                    itemList.adapter = ItemListAdapter(context, items, R.layout.item_row)
                }, failure = { error ->
            server.genericErrors(error.response.statusCode)
        })
    }

    private fun listDepleted() {
        server.getRequest(URLs.LIST_DEPLETED + AppState.getInstance(context).currentStore,
                success = { response ->
                    items.add(ProductModel("title"))
                    for (item: JsonObject in response.array<JsonObject>("mobilerp")!!) {
                        items.add(ProductModel(item))
                    }
                    itemList.adapter = ItemListAdapter(context, items, R.layout.item_row)
                }, failure = { error ->
            server.genericErrors(error.response.statusCode)
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
