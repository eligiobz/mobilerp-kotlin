package com.mobilerp.quimera.mobilerp

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.VolleyError
import com.mobilerp.quimera.mobilerp.online_mode.APIServer
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ListItems.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ListItems.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListItems : Fragment() {

    internal var itemList: ListView
    internal var itemListAdapter: ItemListAdapter
    internal var items: ArrayList<ItemListModel>
    internal var apiServer: APIServer
    internal var URL = URLs.getInstance()
    internal var endpoint: String? = null
    internal var reportURL: String
    internal var reportName: String
    internal var btnDownloadPDF: Button
    internal var count: Int = 0

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            endpoint = arguments.getString("ENDPOINT")

        apiServer = APIServer(context)
        items = ArrayList<ItemListModel>()

        if (endpoint == "LISTPRODUCTS") {
            listProducts()
            reportURL = URLs.BASE_URL + URLs.SALES_REPORT
            reportName = getString(R.string.sales_report_filename)
            activity.setTitle(R.string.drug_list)
        }
        if (endpoint == "LISTDEPLETED") {
            listDepleted()
            reportURL = URLs.BASE_URL + URLs.DEPLETED_REPORT
            reportName = getString(R.string.depleted_report_filename)
            activity.setTitle(R.string.depleted_stock)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnDownloadPDF = getView()!!.findViewById(R.id.btn_download_pdf) as Button
        btnDownloadPDF.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "Download started", Toast.LENGTH_LONG).show()
            val date = SimpleDateFormat("dd-MM-yy")
            val now = Date()
            val fileDownloader = DownloadFileFromURL(object : FileDownloadListener() {
                fun onFileDownloaded() {
                    Toast.makeText(context, R.string.download_finished, Toast.LENGTH_LONG).show()
                }
            })
            fileDownloader.execute(reportURL, reportName + date.format(now) + ".pdf")
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                Log.d("DOWN_ERR", "SD \n" + Environment.getExternalStorageState())
                return@OnClickListener
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_list_items, container, false)
    }

    private fun listProducts() {
        val url = URLs.BASE_URL + URLs.LIST_PRODUCTS
        apiServer.getResponse(Request.Method.GET, url, null, object : VolleyCallback() {
            fun onSuccessResponse(result: JSONObject) {
                try {
                    items.add(ItemListModel("title"))
                    val _itms = result.getJSONArray("mobilerp")
                    for (i in 0 until _itms.length()) {
                        val _itm = _itms.getJSONObject(i)
                        //name, price, total
                        items.add(ItemListModel(_itm.getString("name"), _itm.getDouble("price"), _itm.getInt("units")))
                    }
                    itemList = activity.findViewById(R.id.itemList) as ListView
                    itemListAdapter = ItemListAdapter(context, items, R.layout.item_row)
                    itemList.adapter = itemListAdapter
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            fun onErrorResponse(error: VolleyError) {
                val response = error.networkResponse
                apiServer.genericErrors(response.statusCode)
            }
        })
    }

    fun listDepleted() {
        val url = URLs.BASE_URL + URLs.LIST_DEPLETED
        apiServer.getResponse(Request.Method.GET, url, null, object : VolleyCallback() {
            fun onSuccessResponse(result: JSONObject) {
                try {
                    items.add(ItemListModel("title_"))
                    val _itms = result.getJSONArray("mobilerp")
                    for (i in 0 until _itms.length()) {
                        val _itm = _itms.getJSONObject(i)
                        //name, price, total
                        items.add(ItemListModel(_itm.getString("name"), _itm.getString("date")))
                    }
                    itemList = activity.findViewById(R.id.itemList) as ListView
                    itemListAdapter = ItemListAdapter(context, items, R.layout.item_row)
                    itemList.adapter = itemListAdapter
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            fun onErrorResponse(error: VolleyError) {
                val response = error.networkResponse
                apiServer.genericErrors(response.statusCode)
            }
        })
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

        fun newInstance(endpoint: String): ListItems {
            val fragment = ListItems()
            val args = Bundle()
            args.putString("ENDPOINT", endpoint)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
