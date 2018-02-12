package com.mobilerp.quimera.mobilerp

import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.mobilerp.quimera.mobilerp.ApiModels.UserModel
import com.mobilerp.quimera.mobilerp.online_mode.ServiceDiscovery
import com.mobilerp.quimera.mobilerp.online_mode.URLs

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    internal var user = UserModel._getInstance()
    internal lateinit var ds1: ServiceDiscovery

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val sett_manager = SettingsManager.getInstance(applicationContext)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        if (!user.logged){
            val uname = sett_manager.getString("username")
            when (uname) {
                null -> {
                    val fragment = LoginFragment()
                    val manager = supportFragmentManager
                    manager.beginTransaction()
                            .replace(R.id.main_content, fragment)
                            .addToBackStack("MainView")
                            .commit()
                }
                else -> {
                    user.name = sett_manager.getString("username")!!
                    user.pass = sett_manager.getString("password")!!
                    user.logged = true
                    Toast.makeText(applicationContext, "Welcome back", Toast.LENGTH_LONG).show()
                }
            }
        }

        /*********************** SERVICE FINDER  ***********************/
        if (URLs.BASE_URL == null) {
            val serverAddress = sett_manager.getString(getString(R.string.server_addr))
            when (serverAddress) {
                null -> {
                    Toast.makeText(applicationContext, "Buscando servidor", Toast.LENGTH_LONG).show()
                    ds1 = ServiceDiscovery(this)
                    ds1.doScan()
                }
                else -> {
                    Toast.makeText(applicationContext, "Cargando direccion desde preferencias",
                            Toast.LENGTH_LONG).show()
                    val URL = URLs._getInstance()
                    URL.setBASE_URL(serverAddress)
                }
            }
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val manager = supportFragmentManager
        when (item.itemId) {
            R.id.nav_sales -> {
                val fragment = SalesFragment()
                manager.beginTransaction()
                        .replace(R.id.main_content, fragment)
                        .addToBackStack("MainView")
                        .commit()
            }
            R.id.nav_manager -> {
                val fragment = AdminFragment()
                manager.beginTransaction()
                        .replace(R.id.main_content, fragment)
                        .addToBackStack("MainView")
                        .commit()
            }
            R.id.nav_settings -> {
                val fragment = AllSettings()
                manager.beginTransaction()
                        .replace(R.id.main_content, fragment)
                        .addToBackStack("MainView")
                        .commit()
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
