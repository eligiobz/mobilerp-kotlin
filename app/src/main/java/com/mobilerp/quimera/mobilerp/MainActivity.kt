package com.mobilerp.quimera.mobilerp

import android.content.Context
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
import com.mobilerp.quimera.mobilerp.online_mode.ServiceDiscovery
import com.mobilerp.quimera.mobilerp.online_mode.URLs

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, FinishSell.OnFragmentInteractionListener {

    internal var user = User._getInstance()
    internal lateinit var ds1: ServiceDiscovery

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        /********************* DISABLE ON RELEASE  */
        user.name = "carlo"
        user.pass = "123"
        user.isLoginIn = true

        /*********************** SERVICE FINDER  */
        if (URLs.BASE_URL == null) {
            val context = applicationContext
            val sharedPrefs = context.getSharedPreferences(getString(R.string
                    .preferences_file), Context.MODE_PRIVATE)
            val serverAddress = sharedPrefs.getString(getString(R.string.server_addr), null)
            if (serverAddress == null) {
                Toast.makeText(applicationContext, "Buscando servidor", Toast.LENGTH_LONG).show()
                ds1 = ServiceDiscovery(this)
                ds1.doScan()
            } else {
                Toast.makeText(applicationContext, "Cargando direccion desde preferencias",
                        Toast.LENGTH_LONG).show()
                val URL = URLs._getInstance()
                URL.setBASE_URL(serverAddress)
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
        val id = item.itemId

        if (id == R.id.nav_sales) {
            val fragment = SalesFragment()
            val manager = supportFragmentManager
            manager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack("MainView")
                    .commit()
        } else if (id == R.id.nav_manager) {
            val fragment = AdminFragment()
            val manager = supportFragmentManager
            manager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack("MainView")
                    .commit()
        } else if (id == R.id.nav_settings) {
            val fragment = Settings()
            val manager = supportFragmentManager
            manager.beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack("MainView")
                    .commit()
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {

    }
}
