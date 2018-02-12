package com.mobilerp.quimera.mobilerp


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.mobilerp.quimera.mobilerp.ApiModels.ProductModel
import com.mobilerp.quimera.mobilerp.online_mode.Server
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import kotlinx.android.synthetic.main.fragment_today_statement.*
import java.text.SimpleDateFormat
import java.util.*

class DisplayStatements : Fragment() {


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

        val server = Server(context)

        server.getRequest(URLs.DAILY_SALES_REPORT, success = {
            val itemsData = ArrayList<ProductModel>()
            itemsData.add(ProductModel("title"))
            val model = it["mobilerp"]!! as JsonObject
            val items_data = model.array<JsonObject>("sales")
            if ((items_data != null) and  (items_data!!.size > 0)) {
                for (item: JsonObject in items_data)
                    itemsData.add(ProductModel(item))
                items.adapter = ItemListAdapter(context, itemsData, R.layout.item_row)
            }
            else
                Toast.makeText(context, "No data!", Toast.LENGTH_LONG).show()

        }, failure = {
            server.genericErrors(it.response.statusCode)

        })

        pdf_download.setOnClickListener {
            val date = SimpleDateFormat("dd-MM-yy")
            val now = Date()
            server.downloadFile(URLs.SALES_REPORT_PDF, reportName + date.format(now)+".pdf",
                    success = {
                Toast.makeText(context, R.string.finish, Toast.LENGTH_LONG).show()
            },failure = {
                server.genericErrors(it.response.statusCode)
            })
        }
    }
}
