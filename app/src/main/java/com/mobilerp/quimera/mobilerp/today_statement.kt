package com.mobilerp.quimera.mobilerp


import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.VolleyError
import com.mobilerp.quimera.mobilerp.online_mode.*
import kotlinx.android.synthetic.main.fragment_today_statement.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class today_statement : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_today_statement, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val genUrl = URLs.BASE_URL + URLs.DAILY_SALES_REPORT
        val reportURL = URLs.BASE_URL + URLs.SALES_REPORT_PDF
        val reportName = getString(R.string.daily_sales_report_filename)

        val apiServer = APIServer(context)

//        apiServer.getResponse(Request.Method.GET, genUrl, null, object : VolleyCallback {
//            override fun onSuccessResponse(result: JSONObject) {
//                val items_data = ArrayList<ItemListModel>()
//                items_data.add(ItemListModel("title"))
//                for (item_: JSONObject in result.getJSONArray("sales"))
//                //name, price, total
//                    items_data.add(ItemListModel(item_.getString("name"), item_.getDouble
//                    ("price"), item_.getInt("units")))
//                items.adapter = ItemListAdapter(context, items_data, R.layout.item_row)
//            }
//
//            override fun onErrorResponse(error: VolleyError) {
//                apiServer.genericErrors(error.networkResponse.statusCode)
//            }
//        })

        pdf_download.setOnClickListener {
            apiServer.getResponse(Request.Method.GET, genUrl, null, object : VolleyCallback {
                override fun onSuccessResponse(result: JSONObject) {
                    Toast.makeText(context, "Download started", Toast.LENGTH_LONG).show()
                    val date = SimpleDateFormat("dd-MM-yy")
                    val now = Date()
                    val fileDownloader = DownloadFileFromURL(object : FileDownloadListener {
                        override fun onFileDownloaded() {
                            Toast.makeText(context, R.string.download_finished, Toast.LENGTH_LONG).show()
                        }
                    })
                    fileDownloader.execute(reportURL, reportName + date.format(now) + ".pdf")
                    if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                        Log.d("DOWN_ERR", "SD \n" + Environment.getExternalStorageState())
                    }
                }

                override fun onErrorResponse(error: VolleyError) {
                    apiServer.genericErrors(error.networkResponse.statusCode)
                }
            })
        }
    }

}// Required empty public constructor
