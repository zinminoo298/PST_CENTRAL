package com.example.ui

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.ui.DataBasrHandler.DataBase
import com.example.ui.DataBasrHandler.Details
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_1.*
import net.vidageek.mirror.dsl.Mirror
import java.io.File
import java.lang.Exception

var address:Any = ""

class MainActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager
    private lateinit var tabs: TabLayout
    private lateinit var mToggle: ActionBarDrawerToggle
    internal lateinit var db: DataBase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DataBase(this)
        Details.clear()
        db.getFileDetail
        val root= File( "/storage/emulated/0/Stock Export/")
        if (!root.exists()) {
            root.mkdirs()
        }
        initViews()
        setupViewPager()

//        getBtAddressViaReflection()

        /*Drawer for menu setting*/
        val mDrawerLayout=findViewById<DrawerLayout>(R.id.drawerlayout)
        mToggle= ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)

        mDrawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView =findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->

            // close drawer when item is tapped
            mDrawerLayout.closeDrawers()
            menuItem.isChecked = !menuItem.isChecked

            // Handle navigation view item clicks here.
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_checkstock -> {
                    val intent = Intent(this,UserRecord::class.java)
                    startActivity(intent)
                }
                R.id.nav_master -> {
                    val intent = Intent(this,Frag2::class.java)
                    startActivity(intent)
                }
                R.id.nav_upload -> {
                    val intent = Intent(this,Frag3::class.java)
                    startActivity(intent)
                }
                R.id.nav_clear -> {
                    val intent = Intent(this,Setting::class.java)
                    startActivity(intent)
                }
                R.id.nav_setting -> {
                    val intent = Intent(this,Setting::class.java)
                    startActivity(intent)
                }
                R.id.nav_logout -> {
                    val intent = Intent(this,Login::class.java)
                    startActivity(intent)
                }
            }
            true
        }

    }

    private fun initViews() {
        tabs = findViewById(R.id.tabs)
        viewpager = findViewById(R.id.viewpager)
    }

    private fun setupViewPager() {

        val adapter = ViewPagerAdapter(getSupportFragmentManager())
        adapter.addFragment(Frag1(), "CHECK STOCK")
        adapter.addFragment(Frag2(this), "MASTER")
        adapter.addFragment(Frag3(), "UPLOAD FILES")

        viewpager!!.adapter = adapter
        tabs!!.setupWithViewPager(viewpager)

    }

    private fun getBtAddressViaReflection(){
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        try{
            val bluetoothManagerService: Any =
                Mirror().on(bluetoothAdapter).get().field("mService")
            if (bluetoothManagerService == null) {
                println("couldn't find bluetoothManagerService")
                Toast.makeText(this,"couldn't find bluetoothManagerService",Toast.LENGTH_SHORT).show()
            }
            address = Mirror().on(bluetoothManagerService).invoke().method("getAddress").withoutArgs()
            if (address != null && address is String) {
                println("using reflection to get the BT MAC address: $address")
                Toast.makeText(this,"$address",Toast.LENGTH_SHORT).show()
            } else {
            }
        }
        catch (e:Exception){
            Toast.makeText(this,"Please Enable Bluetooth",Toast.LENGTH_LONG).show()

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (mToggle.onOptionsItemSelected(item)) {

            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
