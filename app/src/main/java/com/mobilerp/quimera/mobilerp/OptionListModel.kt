package com.mobilerp.quimera.mobilerp

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


class OptionListModel(icon: Int, title: String, endpoint: String) {
    var icon: Int = 0
    var title: String? = null
    var endpoint: String? = null

    var isGroupHeader = false

    constructor(title: String) : this(-1, title, "") {
        isGroupHeader = true
    }

    init {
        this.icon = icon
        this.title = title
        this.endpoint = endpoint
    }
}
