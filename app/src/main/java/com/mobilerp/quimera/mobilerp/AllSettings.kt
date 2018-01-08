package com.mobilerp.quimera.mobilerp

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TabHost
import android.widget.Toast
import com.mobilerp.quimera.mobilerp.offline_mode.OperationsLog
import com.mobilerp.quimera.mobilerp.offline_mode.SQLHandler
import com.mobilerp.quimera.mobilerp.online_mode.DownloadFileFromURL
import com.mobilerp.quimera.mobilerp.online_mode.FileDownloadListener
import com.mobilerp.quimera.mobilerp.online_mode.ServiceDiscovery
import com.mobilerp.quimera.mobilerp.online_mode.URLs
import kotlinx.android.synthetic.main.fragment_all_settings.*
import kotlinx.android.synthetic.main.fragment_server_settings.*
import kotlinx.android.synthetic.main.fragment_users_settings.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AllSettings.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AllSettings.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllSettings : Fragment() {

    private val set_manager : SettingsManager by lazy { SettingsManager.getInstance(context) }
    private var server_address: String? = null
    private var use_offline_mode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_all_settings, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        server_address = set_manager.getString(getString(R.string.server_addr))
        use_offline_mode = set_manager.getBoolean(getString(R.string.use_offline_mode))!!
        tabsSetup()
        prepareUsersTab()
        prepareServerTab()
    }

    private fun prepareUsersTab(){
        val uname = set_manager.getString("username")
        if (uname != null){
            etUserName.setText(uname)
            etUserPass.setText(set_manager.getString("password"))
        }
    }

    private fun prepareServerTab(){

        etServerAddr.setText(server_address)

        etServerAddr.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER ||
                    event.keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                val imm = v.context.getSystemService(Context
                        .INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                set_manager.saveString(getString(R.string.server_addr), etServerAddr.text.toString())

                Toast.makeText(context, R.string.server_addr_updated, Toast
                        .LENGTH_LONG).show()
            }
            false
        }

        btnScanServer.setOnClickListener {
            Toast.makeText(context, R.string.searching_server, Toast.LENGTH_LONG).show()
            val ds = ServiceDiscovery(context)
            ds.doScan()
            server_address = set_manager.getString(getString(R.string.server_addr))
            URLs.BASE_URL = server_address
            etServerAddr.setText(server_address)
        }

        btnBackupDB.setOnClickListener(View.OnClickListener {
            Toast.makeText(context, "Download db started", Toast.LENGTH_LONG).show()
            val fileDownloader = DownloadFileFromURL(object : FileDownloadListener {
                override fun onFileDownloaded() {
                    Toast.makeText(context, R.string.download_finished, Toast.LENGTH_LONG).show()
                }
            })
            fileDownloader.execute(URLs.BASE_URL + URLs.DB_BACKUP, getString(R.string.database_name))
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                Log.d("DOWN_ERR", "SD \n" + Environment.getExternalStorageState())
                return@OnClickListener
            }
        })

        cbOfflineMode.isChecked = use_offline_mode
        Toast.makeText(context, if (use_offline_mode) getString(R.string.offline_mode_enabled) else getString(R.string.offline_mode_disabled), Toast.LENGTH_LONG).show()
        cbOfflineMode.setOnClickListener {
            val manager = SettingsManager.getInstance(context)
            use_offline_mode = cbOfflineMode.isChecked
            if (use_offline_mode) {
                val handler = SQLHandler.getInstance(context)
                if (handler.isDatabaseOpen)
                    Toast.makeText(context, if (use_offline_mode) R.string.offline_mode_enabled else R.string.offline_mode_disabled, Toast.LENGTH_LONG).show()
                else {
                    Toast.makeText(context, R.string.no_db_file, Toast.LENGTH_LONG).show()
                    use_offline_mode = false
                    cbOfflineMode.isChecked = use_offline_mode
                }
            } else {
                Toast.makeText(context, R.string.offline_mode_disabled, Toast.LENGTH_LONG).show()
            }
            manager.saveBoolean(getString(R.string.use_offline_mode), use_offline_mode)
        }

        btnUploadPendingOps.setOnClickListener {
            val log = OperationsLog.getInstance(context)
            log.pushOperations()
        }
    }

    private fun tabsSetup() {
        tabHost.setup()

        // tab users
        var spec: TabHost.TabSpec = tabHost.newTabSpec(this.resources.getString(R.string.users_management))
        spec.setContent(R.id.users_tab)
        spec.setIndicator(this.resources.getString(R.string.users_management))
        tabHost.addTab(spec)

        // tab drugstores
        spec = tabHost.newTabSpec(this.resources.getString(R.string.drugstores))
        spec.setContent(R.id.drug_stores_tab)
        spec.setIndicator(this.resources.getString(R.string.drugstores))
        tabHost.addTab(spec)

        // tab server
        spec = tabHost.newTabSpec(this.resources.getString(R.string.server_management))
        spec.setContent(R.id.server_tab)
        spec.setIndicator(this.resources.getString(R.string.server_management))
        tabHost.addTab(spec)

    }
}
