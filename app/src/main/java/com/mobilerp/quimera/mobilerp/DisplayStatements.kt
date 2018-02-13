package com.mobilerp.quimera.mobilerp


import android.icu.text.DateFormat.getDateTimeInstance
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.mobilerp.quimera.mobilerp.ApiModels.SaleReportModel
import com.mobilerp.quimera.mobilerp.online_mode.Server
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import kotlinx.android.synthetic.main.fragment_display_statement.*
import kotlinx.android.synthetic.main.product_sale_row.*
import java.text.SimpleDateFormat
import java.util.*

class DisplayStatements : Fragment() {

    private val datetime = Calendar.getInstance()


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_display_statement, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reportName = getString(R.string.daily_sales_report_filename)

        val server = Server(context)

        server.getRequest(URLs.DAILY_SALES_REPORT, success = {
            val salesReport = SaleReportModel(it["mobilerp"]!! as JsonObject)
            if (salesReport.sales != null) {
                tvReportDateValue.text = salesReport.title
                tvTotalSalesValue.text = salesReport.totalSales.toString()
                tvTotalItemsSoldValue.text = salesReport.totalItemsSold.toString()
                tvTotalEarningValue.text = salesReport.totalEarnings.toString()
            } else {
                Toast.makeText(context, "No data!", Toast.LENGTH_LONG).show()
            }

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
