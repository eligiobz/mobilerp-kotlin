package com.mobilerp.quimera.mobilerp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.beust.klaxon.JsonObject
import com.mobilerp.quimera.mobilerp.Adapters.ProductListAdapter
import com.mobilerp.quimera.mobilerp.Adapters.ServiceListAdapter
import com.mobilerp.quimera.mobilerp.ApiModels.ProductModel
import com.mobilerp.quimera.mobilerp.ApiModels.ServiceModel
import com.mobilerp.quimera.mobilerp.OnlineMode.Server
import com.mobilerp.quimera.mobilerp.OnlineMode.URLs
import kotlinx.android.synthetic.main.fragment_list_items.*
import java.util.*

class ListItems : Fragment() {

    private var endpoint: String? = null
    internal lateinit var server: Server

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            endpoint = arguments!!.getString("ENDPOINT")

        server = Server(context!!)

        when (endpoint) {
            "LISTPRODUCTS" -> {
                listProducts()
                activity!!.setTitle(R.string.drug_list)
            }
            "LISTDEPLETED" -> {
                listDepleted()
                activity!!.setTitle(R.string.depleted_stock)
            }
            "LISTSERVICES" -> {
                listServices()
                activity!!.setTitle(R.string.depleted_stock)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_items, container, false)
    }

    private fun listProducts() {
        var items = ArrayList<ProductModel>()
        server.getRequest(URLs.LIST_PRODUCTS + AppState.getInstance(context!!).currentStore,
                success = { response ->
                    items.add(ProductModel("title"))
                    for (item: JsonObject in response.array<JsonObject>("mobilerp")!!) {
                        items.add(ProductModel(item))
                    }

                    itemList.adapter = ProductListAdapter(context!!, items, R.layout.product_row)
                }, failure = { error ->
            server.genericErrors(error.response.statusCode)
        })
    }

    private fun listDepleted() {
        var items = ArrayList<ProductModel>()
        server.getRequest(URLs.LIST_DEPLETED + AppState.getInstance(context!!).currentStore,
                success = { response ->
                    items.add(ProductModel("title"))
                    for (item: JsonObject in response.array<JsonObject>("mobilerp")!!) {
                        items.add(ProductModel(item))
                    }
                    itemList.adapter = ProductListAdapter(context!!, items, R.layout.product_row)
                }, failure = { error ->
            server.genericErrors(error.response.statusCode)
        })
    }

    private fun listServices() {
        var items = ArrayList<ServiceModel>()
        server.getRequest(URLs.LIST_SERVICES,
                success = { response ->
                    items.add(ServiceModel("title"))
                    for (item: JsonObject in response.array<JsonObject>("mobilerp")!!) {
                        items.add(ServiceModel(item))
                    }
                    itemList.adapter = ServiceListAdapter(context!!, items, R.layout.product_row)
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
