package com.mobilerp.quimera.mobilerp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TabHost
import android.widget.Toast
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
 */
class AdminFragment : Fragment() {

    internal var user = User._getInstance()
    internal lateinit var host: TabHost
    internal lateinit var pharmacyList: ListView
    internal lateinit var salesList: ListView
    internal lateinit var pharmacyListAdapter: OptionListAdapter
    internal lateinit var salesListAdapter: OptionListAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        user.name = "carlo"
        user.pass = "123"
        user.isLoginIn = true

        tabsSetup()

        // Pharmacy list setup
        pharmacyList = getView()!!.findViewById(R.id.lvPharmacyOptions)
        pharmacyListAdapter = OptionListAdapter(context, pharmacyList())
        pharmacyList.adapter = pharmacyListAdapter
        pharmacyList.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    val fragment: Fragment = StockUpdate()
                    val manager = fragmentManager
                    manager.beginTransaction()
                            .replace(R.id.main_content, fragment)
                            .addToBackStack("Admin")
                            .commit()
                }
                1, 2 -> {
                    val fragment = ListItems.newInstance(pharmacyListAdapter
                            .getItem(position).endpoint!!)
                    val manager = fragmentManager
                    manager.beginTransaction()
                            .replace(R.id.main_content, fragment)
                            .addToBackStack("Admin")
                            .commit()
                }
                else -> Toast.makeText(context, "DAYUM " + position,
                        Toast.LENGTH_SHORT).show()
            }
        }


        // Sales list setup
        salesList = getView()!!.findViewById(R.id.lvSalesOptions)
        salesListAdapter = OptionListAdapter(context, salesList())
        salesList.adapter = salesListAdapter
        activity.setTitle(R.string.title_activity_admin)

    }

    private fun pharmacyList(): ArrayList<OptionListModel> {
        val models = ArrayList<OptionListModel>()
        //models.add(new OptionListModel("Acciones"));
        models.add(OptionListModel(R.mipmap.ic_launcher, this.resources.getString(R
                .string.stock_input), "UPDATESTOCK"))
        models.add(OptionListModel(R.mipmap.ic_launcher, this.resources.getString(R.string.drug_list), "LISTPRODUCTS"))
        models.add(OptionListModel(R.mipmap.ic_launcher, this.resources.getString(R.string.depleted_stock), "LISTDEPLETED"))

        return models
    }

    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        // inflate the menu, this adds items to th action bar if present
    //        getMenuInflater().inflate(R.menu.menu_admin, menu);
    //        return true;
    //    }

    private fun salesList(): ArrayList<OptionListModel> {
        val models = ArrayList<OptionListModel>()
        //models.add(new OptionListModel("Acciones"));
        models.add(OptionListModel(R.mipmap.ic_launcher, this.resources.getString(R.string.today_sales), ""))
        models.add(OptionListModel(R.mipmap.ic_launcher, this.resources.getString(R.string.month_sales), ""))
        models.add(OptionListModel(R.mipmap.ic_launcher, this.resources.getString(R
                .string.most_sold_products), ""))
        models.add(OptionListModel(R.mipmap.ic_launcher, this.resources.getString(R.string.least_sold_products), ""))
        models.add(OptionListModel(R.mipmap.ic_launcher, this.resources.getString(R.string.custom_statement), ""))

        return models
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId

        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    private fun tabsSetup() {
        host = view!!.findViewById(R.id.tabHost)
        host.setup()

        // tab 1
        var spec: TabHost.TabSpec = host.newTabSpec(this.resources.getString(R.string.pharmacy))
        spec.setContent(R.id.pharmacy_tab)
        spec.setIndicator(this.resources.getString(R.string.pharmacy))
        host.addTab(spec)

        // tab 2
        spec = host.newTabSpec(this.resources.getString(R.string.sales))
        spec.setContent(R.id.sales_tab)
        spec.setIndicator(this.resources.getString(R.string.sales))
        host.addTab(spec)

        // tab 3
        spec = host.newTabSpec(this.resources.getString(R.string.patients))
        spec.setContent(R.id.px_tab)
        spec.setIndicator(this.resources.getString(R.string.patients))
        host.addTab(spec)
    }

}// Required empty public constructor
