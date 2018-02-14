package com.mobilerp.quimera.mobilerp


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.mobilerp.quimera.mobilerp.OfflineMode.OperationsLog
import com.mobilerp.quimera.mobilerp.OnlineMode.Server
import com.mobilerp.quimera.mobilerp.ApiModels.ServiceModel
import com.mobilerp.quimera.mobilerp.OnlineMode.URLs
import kotlinx.android.synthetic.main.fragment_manage_services.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ManageServices : Fragment() {

    private var isNewService: Boolean = false
    private lateinit var server: Server
    internal lateinit var log: OperationsLog


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_manage_services, container, false)
    }

    override fun onViewCreated(view: View?, savedInstance: Bundle?) {

        server = Server(context)

        tvBarcode.setText(R.string.item_barcode)
        tvPrice.setText(R.string.item_price)
        etName.isEnabled = false
        etPrice.isEnabled = false

        activity.setTitle(R.string.add_service)

        btnSave.setOnClickListener {
            sendData()
        }

        etBarcode.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT){
                server.getRequest(URLs.FIND_SERVICE+etBarcode.text.toString(),
                success = { response ->
                    val service = response.map{ ServiceModel(response.obj("mobilerp")!!) }
                    etName.setText(service[0].name)
                    etPrice.setText(service[0].price.toString())
                    enableEntries()
                },
                failure = {
                    if (it.response.statusCode == 404){
                        isNewService = true
                        enableEntries()
                        Toast.makeText(context, R.string.new_item, Toast.LENGTH_LONG).show()
                    }
                })
            }
            true
        }
    }

    private fun sendData(){
        val data = this.prepareJSON()
        when (isNewService) {
            true -> {
                server.postRequest(URLs.NEW_SERVICE, data,
                        success = { _ ->
                            Toast.makeText(context, R.string.srv_op_success, Toast.LENGTH_LONG).show()
                        },
                        failure = {
                            server.genericErrors(it.response.statusCode)
                        }
                )
                isNewService = false
            }
            false -> {
                server.putRequest(URLs.UPDATE_SERVICE+data["barcode"].toString(), data,
                        success = { _ ->
                            Toast.makeText(context, R.string.srv_op_success, Toast.LENGTH_LONG).show()
                        },
                        failure = {
                            server.genericErrors(it.response.statusCode)
                        })
            }
        }
        cleanEntries()
    }

    private fun prepareJSON(): JsonObject {
        val data = JsonObject()
        data.put("barcode", etBarcode.text.toString())
        data.put("name", etName.text.toString())
        data.put("price", etPrice.text.toString())
        data.put("token", Calendar.getInstance().time.toString())
        return data
    }

    private fun enableEntries() {
        etPrice.isEnabled = true
        etName.isEnabled = true
        if (isNewService)
            etName.setText(R.string.service)
        etName.requestFocus()
    }

    private fun cleanEntries() {
        Toast.makeText(context, R.string.srv_op_success, Toast.LENGTH_LONG).show()
        etName.setText("")
        etPrice.setText("")
        etBarcode.setText("")
        etName.isEnabled = false
        etPrice.isEnabled = false
    }

}
