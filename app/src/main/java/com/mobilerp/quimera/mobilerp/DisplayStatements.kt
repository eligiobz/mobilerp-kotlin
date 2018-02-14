package com.mobilerp.quimera.mobilerp


import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.mobilerp.quimera.mobilerp.ApiModels.SaleReportModel
import com.mobilerp.quimera.mobilerp.OnlineMode.Server
import com.mobilerp.quimera.mobilerp.OnlineMode.URLs
import kotlinx.android.synthetic.main.fragment_display_statement.*
import java.text.SimpleDateFormat
import java.util.*



class DisplayStatements : Fragment() {

    private val datetime = Calendar.getInstance()
    private val server : Server  by lazy { Server(context) }
    private lateinit var reportName : String
    private lateinit var initDate: String
    private var endDate: String? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_display_statement, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reportName = getString(com.mobilerp.quimera.mobilerp.R.string.daily_sales_report_filename)

        prepareDatePickers()

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
            initDate = date.format(now)
            server.downloadFile(URLs.SALES_REPORT_PDF, reportName + initDate,
                    success = {
                Toast.makeText(context, R.string.finish, Toast.LENGTH_LONG).show()
            },failure = {
                server.genericErrors(it.response.statusCode)
            })
        }
    }

    private fun prepareDatePickers(){
        tvDate1.inputType = 0
        tvDate2.inputType = 0

        tvDate1.setOnFocusChangeListener{_ , focus ->
            if (focus){
                setDate(tvDate1)
            }
        }

        tvDate2.setOnFocusChangeListener{_ , focus ->
            if (focus){
                setDate(tvDate2)
            }
        }

        tvDate1.setOnClickListener{
            setDate(tvDate1)
        }

        tvDate2.setOnClickListener{
            setDate(tvDate2)
        }
    }

    private fun setDate(tv : TextView){
        val datePicker = DatePickerDialog(context, DatePickerDialog.OnDateSetListener {
            _, year, monthOfYear, dayOfMonth ->

            tv.setText( dayOfMonth.toString() + "-" + (monthOfYear+1).toString() +
                    "-" + year.toString())
        }, datetime.get(Calendar.YEAR),
                datetime.get(Calendar.MONTH),
                datetime.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }
}
