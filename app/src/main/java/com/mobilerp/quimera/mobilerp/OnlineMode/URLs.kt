package com.mobilerp.quimera.mobilerp.OnlineMode

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
class URLs protected constructor() {

    fun setBASE_URL(u: String) {
        BASE_URL = u + "/"
    }

    companion object {

        private const val ApiVersion = "v1.1"

        const val LOGIN = "api/$ApiVersion/user/check_login/" //Done

        // Reports
        const val DAILY_SALES_REPORT = "api/$ApiVersion/daily_report/" //Done
        const val MONTHLY_SALES_REPORT = "api/$ApiVersion/monthly_report/" //Done
        const val CUSTOM_REPORT = "api/$ApiVersion/custom_report/"
        const val DEPLETED_ITEMS_REPORT = "api/$ApiVersion/list_depleted_products/" //Done
        const val SALES_REPORT_PDF = "api/$ApiVersion/get_report/salesreport.pdf" //Done
        const val DEPLETED_REPORT_PDF = "api/$ApiVersion/get_report/depletedreport.pdf" //Done
        const val DB_BACKUP = "api/$ApiVersion/dbBackup/" //Done

        // Stores
        const val LIST_DRUGSTORES = "api/$ApiVersion/list_drugstores/" //Done
        const val ADD_STORE = "api/$ApiVersion/add_drugstore/" //Done

        // Products 
        const val NEW_PRODUCT = "api/$ApiVersion/add_product/" //Done
        const val UPDATE_PRODUCT = "api/$ApiVersion/update_product/" //Done
        const val FIND_PRODUCT = "api/$ApiVersion/find_product/" //Done
        const val LIST_PRODUCTS = "api/$ApiVersion/list_products/" //Done
        const val LIST_DEPLETED = "api/$ApiVersion/list_depleted_products/"

        // Services
        const val NEW_SERVICE = "api/$ApiVersion/add_service/" //Done
        const val UPDATE_SERVICE = "api/$ApiVersion/update_service/" //Done
        const val FIND_SERVICE = "api/$ApiVersion/find_service/" //Done
        const val LIST_SERVICES = "api/$ApiVersion/list_services/" //Done

        // Sales
        const val MAKE_SALE = "api/$ApiVersion/make_sale/"
        const val FIND_ARTICLE = "api/$ApiVersion/find_article/"

        var BASE_URL: String? = null

        private var instance: URLs? = null

        fun _getInstance(): URLs {
            if (instance == null)
                instance = URLs()
            return instance!!
        }
    }
}
