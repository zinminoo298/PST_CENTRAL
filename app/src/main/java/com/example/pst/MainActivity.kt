package com.example.pst

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.example.pst.Adapters.File_Adapter
import com.example.pst.DataBaseHandler.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import net.vidageek.mirror.dsl.Mirror
import java.io.*
import java.nio.channels.FileChannel
import java.util.*

var address:Any = ""

class MainActivity : AppCompatActivity() {

    private lateinit var viewpager: ViewPager
    private lateinit var tabs: TabLayout
    private lateinit var mToggle: ActionBarDrawerToggle
    internal lateinit var db: DataBase
    private var MASTER:String? = null
    private var Tran:String? = null
    private var All:String? = null
    private var Backup:String? =  null
    private var progressBar1: ProgressBar? =null
    internal lateinit var dialog: AlertDialog
    internal lateinit var export: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DataBase(this)
//        Details.clear()
//        db.getFileDetail
//        db.checkSaveFiles()
//        AsyncLoadMain(this).execute()
        db.checkDB()
        db.getTotalItems()
        db.getBu()
        db.checkSaveFiles()

//        Frag1.adapter = File_Adapter(Frag1.stItem, this)
//        Frag1.adapter.refresh(db.getFileDetail)
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
                    val filepath="/sdcard/Download/database.db"
                    val file=File(filepath)
                    if(file.exists())
                    {
                        AsyncTaskRunner(this,File("/sdcard/Download/database.db"),File("/data/data/com.example.ui/databases/database.db")).execute()

                    }
                    else{
                        Toast.makeText(this,"Master file not found",Toast.LENGTH_SHORT).show()
                    }
//                    copy(File("/sdcard/Download/database.db.zip"),File("/data/data/com.example.ui/databases/database.db.zip"))

                }
                R.id.nav_export ->{
                    Toast.makeText(this,"OK",Toast.LENGTH_SHORT).show()
                    AsyncExport(this).execute()
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
        adapter.addFragment(Frag3(this), "UPLOAD FILES")

        viewpager!!.adapter = adapter
        tabs!!.setupWithViewPager(viewpager)

        val limit =  if (adapter.getCount() > 1) adapter.getCount() - 1 else 1
        viewpager.setOffscreenPageLimit(limit)

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

                    db.execSQL("DELETE FROM pdamasters")
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
        val listItems=arrayOf("Master Data", "Scanned Data","Backup Data","All Data")
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
                Backup = null
                println(MASTER)
                println(Tran)
                println(All)
            }

            if (i == 1) {
                Tran = "Transaction_table"
                MASTER = null
                All = null
                Backup = null
                println(MASTER)
                println(Tran)
                println(All)
            }

            if (i== 2){
                All = null
                Tran = null
                MASTER = null
                Backup = "Backup"
                println(MASTER)
                println(Tran)
                println(All)
            }

            if (i== 3){
                All = "Transaction_table"
                Tran = null
                MASTER = "Master"
                Backup = "Backup"
                println(MASTER)
                println(Tran)
                println(All)
            }

            if (i!=0 && i!=1 && i!=2 && i!=3){
                MASTER = null
                Tran = null
                All = null
                Backup  = null

                println(MASTER)
                println(Tran)
                println(All)
            }
        })
        builder.setPositiveButton("Clear",  DialogInterface.OnClickListener(){ _, _ ->

            val db=this.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
            val db1=this.openOrCreateDatabase("summery.db", Context.MODE_PRIVATE, null)

            if(MASTER==null){
                println("Master is null")
            }
            else {
                db.execSQL("DELETE FROM pdamasters")
                db.execSQL("DELETE FROM locations")
                db.execSQL("DELETE FROM variances")
                db.execSQL("DELETE FROM pdamaster_businesses")
                storeCode = ""
                storeName = ""
                stockTakeID = ""
                BU=""
            }


            if(Tran==null){
                println("Tran is null")
            }
            else {
                db1.execSQL("DELETE FROM summery")
                db1.execSQL("DELETE FROM date")
                db1.execSQL("VACUUM")
                deleteRecursive(File("/sdcard/Stock Export"))

            }

            if(All==null){
                println("ALL is null")
            }
            else {
                db.execSQL("DELETE FROM pdamasters")
                db.execSQL("DELETE FROM locations")
                db.execSQL("DELETE FROM variances")
                db.execSQL("DELETE FROM pdamaster_businesses")

                db1.execSQL("DELETE FROM summery")
                db1.execSQL("DELETE FROM date")
                db1.execSQL("VACUUM")
                storeCode = ""
                storeName = ""
                stockTakeID = ""
                BU = ""
                deleteRecursive(File("/sdcard/Stock Export"))
            }

            if(Backup == null){
                println("Backup is null")
            }
            else{
                deleteRecursive(File("/sdcard/Backup"))

            }

            db.close()
            db1.close()
            if(MASTER == null && Tran == null && All == null && Backup == null){
                Toast.makeText(this,"Select one option", Toast.LENGTH_SHORT).show()
            }

            MASTER = null
            Tran = null
            All = null
            Backup = null

            println(MASTER+Tran+All)

            dialog.dismiss()

            Details.clear()
            val DB = DataBase(this)
            Frag1.adapter = File_Adapter(Frag1.stItem, context)
            Frag1.adapter.refresh(DB.getFileDetail)
            val a=Intent(this, MainActivity::class.java)
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(a)
        })

        dialog= builder.create()
        dialog.show()
    }
    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            for (child in fileOrDirectory.listFiles()) {
                deleteRecursive(child)
            }
        }
        fileOrDirectory.delete()
    }

    fun exportDialog(){
        val builder= AlertDialog.Builder(this)
        val inflater=this.layoutInflater
        val view=inflater.inflate(R.layout.export_layout, null)
        builder.setView(view)
        dialog=builder.create()
        dialog.setMessage("DATA WILL BE EXPORTED TO Internal Storage/Stock Export")
        progressBar1 = view.findViewById(R.id.progress_bar)
        progressBar1!!.visibility = View.GONE
        dialog.show()

        export = view.findViewById(R.id.btn_export)
        export.setOnClickListener {
            progressBar1!!.visibility = View.VISIBLE
            db.getDocs()
            println(docsArray[1])
            println(docsArray.size)

        }
    }

    override fun onBackPressed() {
        val a=Intent(this, Login::class.java)
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(a)
        super.onBackPressed()
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

    private class AsyncExport(val context: Context?) : AsyncTask<String, String, String>() {
        lateinit var pgd: ProgressDialog
        lateinit var db: DataBase

        override fun doInBackground(vararg params: String?): String {
            db = DataBase(context!!)
            db.getDocs()
            for(i in 0 until docsArray.size){
                export(docsArray[i])
            }
            return "gg"
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Please Wait")
            pgd.setTitle("Exporting Files")
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            pgd.dismiss()
            Toast.makeText(context,"^_^ Export Completed", Toast.LENGTH_LONG).show()
            Details.clear()
            Frag1.adapter = File_Adapter(Frag1.stItem, context!!)
            Frag1.adapter.refresh(db.getFileDetail)
            val a=Intent(context, MainActivity::class.java)
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(a)
            super.onPostExecute(result)
        }

        private fun export(doc:String) {
            try {
                db= DataBase(context!!)
                val database=context.openOrCreateDatabase("summery.db", Context.MODE_PRIVATE, null)
                val selectQuery=
                    " SELECT Seq,StockTakeID,DocNum,Inspector,Seq,Location,SKU,Barcode,IBC,SBC,ProductName,QNT,SalePrice,DateTime FROM Summery WHERE DocNum='$doc'"
                val cursor=database.rawQuery(selectQuery, null)
                var rowcount: Int
                var colcount: Int

                val saveFile=File("/sdcard/Stock Export/$doc.csv")
                val fw=FileWriter(saveFile)

                var k = 1
                val bw=BufferedWriter(fw)
                rowcount=cursor.getCount()
                colcount=cursor.getColumnCount()
                bw.write("rowid,StockTakeID,DocNum,Inspector,SEQ,Location,SKU,Barcode,IBC,SBC,ProductName,QNT,SalePrice,DateTime")
                bw.newLine()

                if (rowcount>0) {

                    for (i in 0 until rowcount) {
                        cursor!!.moveToPosition(i)

                        for (j in 0 until colcount) {
                            if (j == 0) {
                                val formatted = java.lang.String.format("%04d", k)
                                bw.write("$formatted,")

                            }
                            if (j == 1) {

                                bw.write(cursor!!.getString(j) + ",")

                            }
                            if (j == 2) {

                                bw.write(cursor!!.getString(j) + ",")

                            }
                            if (j == 3) {

                                bw.write(cursor!!.getString(j) + ",")

                            }
                            if (j == 4) {
                                val formatted = java.lang.String.format("%01d", k)
                                bw.write("$formatted,")
//                            bw.write(cursor!!.getString(j) + ",")

                            }
                            if (j == 5) {

                                bw.write(cursor!!.getString(j) + ",")

                            }
                            if (j == 6) {

                                bw.write(cursor!!.getString(j).padStart(13,'0') + ",")

                            }
                            if (j == 7) {

                                bw.write(cursor!!.getString(j).padStart(13,'0') + ",")
                            }
                            if (j == 8) {

                                if(cursor.getString(j) == null || cursor.getString(j) == "")
                                {
                                    bw.write(",")
                                }
                                else {
                                    bw.write(cursor!!.getString(j).padStart(13,'0') + ",")
                                }

                            }
                            if (j == 9) {

                                if(cursor.getString(j) == null || cursor.getString(j) == "")
                                {
                                    bw.write(",")
                                }
                                else {
                                    bw.write(cursor!!.getString(j).padStart(13,'0') + ",")
                                }

                            }
                            if (j == 10) {

                                bw.write(cursor!!.getString(j) + ",")

                            }

                            if (j == 11) {

                                if(cursor.getString(j) == null || cursor.getString(j) == "")
                                {
                                    bw.write(",")
                                }
                                else {
                                    bw.write(cursor!!.getString(j) + ",")
                                }

                            }

                            if (j == 12) {
                                bw.write(cursor!!.getString(j) + ",")
                            }

                            if (j == 13) {
                                bw.write(cursor!!.getString(j))
                            }
                        }
                        bw.newLine()
                        k++
                    }
                    bw.flush()

                }
                updateCheck = "yes"
                db.update(doc)
                database.close()
                copyFile(File("/sdcard/Stock Export/$doc.csv"),File("/sdcard/Backup/$doc.csv"),File("/sdcard/Backup"))

            }
            catch (ex: Exception) {
                ex.printStackTrace()

            }

        }

        private fun copyFile(sourceFile:File, destFile:File, root:File){
            if(!root.exists()){
                root.mkdir()
            }

            if(!destFile.exists()){
                destFile.createNewFile()
            }
            var source: FileChannel? = null
            var destination: FileChannel? = null

            try {
                source = FileInputStream(sourceFile).channel
                destination = FileOutputStream(destFile).channel
                destination.transferFrom(source, 0, source.size())
            } finally {
                if (source != null) {
                    source.close()
                }
                if (destination != null) {
                    destination.close()
                }
            }
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
//            pgd.setButton(
//                DialogInterface.BUTTON_NEGATIVE,
//                "Cancel",
//                DialogInterface.OnClickListener { dialog, which ->
//                    running = false
//                    dialog.dismiss()
//                })
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            pgd.dismiss()
            setDate()
            Toast.makeText(context,"^_^ Restore Master Completed", Toast.LENGTH_LONG).show()
            val a=Intent(context, MainActivity::class.java)
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context!!.startActivity(a)
            super.onPostExecute(result)
        }

        override fun onCancelled() {
            running = false
        }
        fun setDate(){
            val c = Calendar.getInstance()
            val day = c[Calendar.DAY_OF_MONTH]
            val month = c[Calendar.MONTH]
            val year = c[Calendar.YEAR]
            val mth = month +1
            saveDate("$day/$mth/$year")
        }
        private fun saveDate(v: String) {
            var editor = context!!.getSharedPreferences("date", AppCompatActivity.MODE_PRIVATE).edit()
            editor.putString("valDate", v)
            editor.apply()
        }

        fun copy(src: File?, dst: File?) {
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
