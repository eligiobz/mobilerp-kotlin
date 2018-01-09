package com.mobilerp.quimera.mobilerp.online_mode

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

        val LOGIN = "api/v1.0/user/checkLogin/"
        val LIST_PRODUCTS = "api/v1.0/listProducts/"
        val LIST_DEPLETED = "api/v1.0/listDepletedProducts/"
        val FIND_PRODUCT = "api/v1.0/findProduct/"
        val NEW_PRODUCT = "api/v1.0/newProduct/"
        val UPDATE_PRODUCT = "api/v1.0/updateProduct/"
        val MAKE_SALE = "api/v1.0/makeSale/"
        val SALES_REPORT = "api/v1.0/getReport/salesreport.pdf"
        val DEPLETED_REPORT = "api/v1.0/getReport/depletedreport.pdf"
        val DB_BACKUP = "api/v1.0/dbBackup/"
        val LIST_DRUGSTORES = "api/v1.0/listDrugstores/"
        var BASE_URL: String? = null

        private var instance: URLs? = null

        fun _getInstance(): URLs {
            if (instance == null)
                instance = URLs()
            return instance!!
        }
    }
}
