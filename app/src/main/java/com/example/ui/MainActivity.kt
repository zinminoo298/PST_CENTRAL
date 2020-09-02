package com.example.ui

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.example.ui.DataBasrHandler.DataBase
import com.example.ui.DataBasrHandler.Details
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import net.vidageek.mirror.dsl.Mirror
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

var address:Any = ""

class MainActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager
    private lateinit var tabs: TabLayout
    private lateinit var mToggle: ActionBarDrawerToggle
    internal lateinit var db: DataBase
    private var MASTER:String? = null
    private var Tran:String? = null
    private var All:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DataBase(this)
//        Details.clear()
//        db.getFileDetail
//        db.checkSaveFiles()
        AsyncLoadMain(this).execute()
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
                R.id.nav_checkstock -> {
                    AsyncTaskRunner(this,File("/sdcard/Download/database.db"),File("/data/data/com.example.ui/databases/database.db")).execute()
//                    copy(File("/sdcard/Download/database.db.zip"),File("/data/data/com.example.ui/databases/database.db.zip"))

                }
                R.id.nav_clear -> {
                    clearDialog(this)
                }
                R.id.nav_setting -> {
                    val intent = Intent(this,Setting::class.java)
                    startActivity(intent)
                }
                R.id.nav_logout -> {
                    val a=Intent(this, Login::class.java)
                    a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(a)
                }
            }
            true
        }

    }

    fun copy(src: File?, dst: File?) {
//            FileInputStream(src).use { `in` ->
//                FileOutputStream(dst).use { out ->
//                    // Transfer bytes from in to out
//                    val buf = ByteArray(1024)
//                    var len = 0
//                    while (`in`.read(buf).also { len = it } > 0) {
//                        out.write(buf, 0, len)
//                        println("Writing")
//                    }
//                }
//            }

        val `is`=FileInputStream(src)
        val os= FileOutputStream(dst)

        val buffer=ByteArray(1024)
        while(`is`.read(buffer)>0) {
            os.write(buffer)
            Log.d("#DB", "writing>>")
        }

        os.flush()
        os.close()
        `is`.close()
        Log.d("#DB", "completed..")
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

    fun clearDB(){
        lateinit var dialog: AlertDialog
        db=DataBase(this)

        // Initialize a new instance of alert dialog builder object
        val builder= AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("Clear Database")

        // Set a message for alert dialog
        builder.setMessage("Are you sure??")


        // On click listener for dialog buttons
        val dialogClickListener= DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val db=this.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
                    val db1=this.openOrCreateDatabase("summery.db", Context.MODE_PRIVATE, null)

                    db1.execSQL("DELETE FROM summery")
                    db1.execSQL("DELETE FROM date")

                    db.execSQL("DELETE FROM masters")
                    db.execSQL("DELETE FROM locations")
                    db.execSQL("DELETE FROM variances")

                    val a=Intent(this, MainActivity::class.java)
                    a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(a)
                }
                DialogInterface.BUTTON_NEUTRAL -> {
                    dialog.dismiss()
                    Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("YES", dialogClickListener)

        // Set the alert dialog neutral/cancel button
        builder.setNeutralButton("CANCEL", dialogClickListener)


        // Initialize the AlertDialog using builder object
        dialog=builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }

    private fun clearDialog(context: Context){
        lateinit var dialog: android.app.AlertDialog
        val listItems=arrayOf("Master Data", "Scanned Data","All Data")
        val builder= android.app.AlertDialog.Builder(this)

        val checkvalue=booleanArrayOf(
            false,
            false        )
        builder.setTitle("Choose Database to Delete")
        builder.setSingleChoiceItems(listItems,-1,DialogInterface.OnClickListener(){dialoginterface,i->

            if (i == 0) {
                MASTER = "Master"
                Tran = null
                All = null
                println(MASTER)
                println(Tran)
                println(All)
            }

            if (i == 1) {
                Tran = "Transaction_table"
                MASTER = null
                All = null
                println(MASTER)
                println(Tran)
                println(All)
            }

            if (i== 2){
                All = "Transaction_table"
                Tran = null
                MASTER = "Master"
                println(MASTER)
                println(Tran)
                println(All)
            }

            if (i!=0 && i!=1 && i!=2){
                MASTER = null
                Tran = null
                All = null
                println(MASTER)
                println(Tran)
                println(All)
            }
        })
//        builder.setMultiChoiceItems(listItems,checkvalue,DialogInterface.OnMultiChoiceClickListener() {dialogInterface,i,_->
//
//
//            if (i == 0) {
//                MASTER = "Master"
//                println(MASTER)
//            }
//
//            if (i == 1) {
//                Tran = "Transaction_table"
//                println(Tran)
//            }
//
//        })

        builder.setPositiveButton("Clear",  DialogInterface.OnClickListener(){ _, _ ->

            val db=this.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
            val db1=this.openOrCreateDatabase("summery.db", Context.MODE_PRIVATE, null)

            if(MASTER==null){
                println("Master is null")
            }
            else {
                db.execSQL("DELETE FROM masters")
                db.execSQL("DELETE FROM locations")
                db.execSQL("DELETE FROM variances")
            }


            if(Tran==null){
                println("Tran is null")
            }
            else {
                db1.execSQL("DELETE FROM summery")
                db1.execSQL("DELETE FROM date")
            }

            if(All==null){
                println("ALL is null")
            }
            else {
                db.execSQL("DELETE FROM masters")
                db.execSQL("DELETE FROM locations")
                db.execSQL("DELETE FROM variances")

                db1.execSQL("DELETE FROM summery")
                db1.execSQL("DELETE FROM date")
            }

            if(MASTER == null && Tran == null && All == null){
                Toast.makeText(this,"Select one option", Toast.LENGTH_SHORT).show()
            }

            MASTER = null
            Tran = null
            All = null

            println(MASTER+Tran+All)

            dialog.dismiss()
            val a=Intent(this, MainActivity::class.java)
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(a)        })

        dialog= builder.create()
        dialog.show()
    }

    private class AsyncLoadMain(val context: Context) : AsyncTask<String,String,String>(){
        internal lateinit var pgd: ProgressDialog
        internal lateinit var db:DataBase
        override fun doInBackground(vararg params: String?): String {
            db = DataBase(context)
            Details.clear()
//            db.getFileDetail
            db.checkSaveFiles()
            return "gg"
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Loading")
            pgd.setTitle("Loading Data")
//            pgd.setButton(
//                DialogInterface.BUTTON_NEGATIVE,
//                "Cancel",
//                DialogInterface.OnClickListener { dialog, which ->
//                    dialog.dismiss()
//                })
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            pgd.dismiss()
//            Toast.makeText(context,"^_^ Download Completed", Toast.LENGTH_LONG).show()
//            val a=Intent(context, MainActivity::class.java)
//            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            context!!.startActivity(a)
            super.onPostExecute(result)
        }
    }

    private class AsyncTaskRunner(val context: Context?,val src: File?, val output: File?) : AsyncTask<String, String, String>() {
        internal lateinit var pgd: ProgressDialog
        private var running = true


        override fun doInBackground(vararg params: String?): String {
            while(running){
                copy(src,output)
            }
            return "gg"
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Restoring")
            pgd.setTitle("Restoring Master")
            pgd.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                "Cancel",
                DialogInterface.OnClickListener { dialog, which ->
                    running = false
                    dialog.dismiss()
                })
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            pgd.dismiss()
            Toast.makeText(context,"^_^ Download Completed", Toast.LENGTH_LONG).show()
            val a=Intent(context, MainActivity::class.java)
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context!!.startActivity(a)
            super.onPostExecute(result)
        }

        override fun onCancelled() {
            running = false
        }

        fun copy(src: File?, dst: File?) {
//            FileInputStream(src).use { `in` ->
//                FileOutputStream(dst).use { out ->
//                    // Transfer bytes from in to out
//                    val buf = ByteArray(1024)
//                    var len = 0
//                    while (`in`.read(buf).also { len = it } > 0) {
//                        out.write(buf, 0, len)
//                        println("Writing")
//                    }
//                }
//            }

            val `is`=FileInputStream(src)
            val os= FileOutputStream(dst)

            val buffer=ByteArray(1024)
            while(`is`.read(buffer)>0) {
                os.write(buffer)
                Log.d("#DB", "writing>>")
            }

            os.flush()
            os.close()
            `is`.close()
            running = false
            Log.d("#DB", "completed..")
        }

    }

}
