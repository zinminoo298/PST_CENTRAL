package com.example.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.ui.DataBasrHandler.*
import com.example.ui.Modle.File_list
import kotlinx.android.synthetic.main.check_stock.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


var requestBack = 0

class Check_stock : AppCompatActivity() {

    internal lateinit var db: DataBase
    var date:String? = ""
    var formatted_date:String? = ""

    internal lateinit var txt_sku:TextView
    internal lateinit var edt_desc:EditText
    internal lateinit var btn_save:Button
    internal lateinit var btn_cancel:Button
    internal lateinit var mTopToolbar: Toolbar
    internal lateinit var imgComplete:ImageView
    internal lateinit var imgImcomplete:ImageView

    private var progressBar1: ProgressBar? =null
    internal lateinit var dialog: AlertDialog
    internal lateinit var export: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_stock)

        requestBack = 2
        imgComplete = findViewById(R.id.complete)
        imgImcomplete = findViewById(R.id.imcomplete)
        loadCount()

        if(updateCheck == "no"){
            imgComplete.visibility = GONE
            imgImcomplete.visibility = VISIBLE
        }
        else{
            imgImcomplete.visibility = GONE
            imgComplete.visibility = VISIBLE
        }

        mTopToolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(mTopToolbar)

        filename = doc_name
        txt_sku_qty.setText("")
        txt_lc_qty.setText("")
        txt_pdName.setText("")
        txt_cost.setText("")
        txt_pack.setText("")
        txt_status.setText("")
        txt_stock.setText("")
        txt_sku_name.setText("")
        total_am.setText("")

        val Lock:ImageView
        val Unlock:ImageView

        db = DataBase(this)
        db.getBu()
        storename.setText(storeName)
        storecode.setText(storeCode)
        bu.setText(BU)
        countid.setText("STOCK TAKE ID : " + stockTakeID)
        scan_qty.isFocusable = false


        txt_doc.setText(doc_name)
        txt_lc.setText(location)

        scan_bc.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                if (scan_bc.text.toString() == "") {
                    Toast.makeText(this, "Please Enter SKU", Toast.LENGTH_SHORT).show()
                    scan_bc.setText("")
                } else {
                    sku = scan_bc.text.toString()
                    qty = scan_qty.text.toString()
                    filename = txt_doc.text.toString()
                    db.checkItem1()

                    if (ckItem == 1) {
                        if (status!!.length == 1) {
                            db.checkSeq()
                            db.addItem1()
                            db.summeryValue()
                            txt_sku_qty.setText(sku_qty)
                            txt_lc_qty.setText(lc_qty)
                            txt_pdName.setText(pdName)
                            txt_cost.setText(cost.toString())
                            txt_pack.setText(packSz.toString())
                            txt_status.setText(status)
                            txt_stock.setText(stock.toString())
                            txt_sku_name.setText(scan_bc.text.toString())
                            total_am.setText(lc_value)
                            scan_bc.setText("")
                            scan_bc.requestFocus()

                            imgComplete.visibility = View.GONE
                            imgImcomplete.visibility = View.VISIBLE
                        } else {
                            if (status!!.length > 1) {
                                if (status!!.substring(1) == "Non-Sales") {
                                    Toast.makeText(this, "Barcode Non-Sales", Toast.LENGTH_LONG)
                                        .show()

//                                    var mediaPlayer = MediaPlayer.create(
//                                        applicationContext,
//                                        R.raw.buzz
//                                    )
//                                    mediaPlayer.start()
//                                    val handler = Handler()
//                                    handler.postDelayed({ mediaPlayer.stop() }, 1 * 1000.toLong())

//                                    val afd: AssetFileDescriptor = assets.openFd("buzz.wav")
//                                    val player = MediaPlayer()
//                                    player.setDataSource(
//                                        afd.getFileDescriptor(),
//                                        afd.getStartOffset(),
//                                        afd.getLength()
//                                    )
//                                    player.prepare()
//                                    player.start()
//                                    val handler = Handler()
//                                    handler.postDelayed({ player.stop() }, 1 * 1000.toLong())
                                    alertDialog1(status!!)
                                    AsyncTaskRunner(this).execute()

                                } else {
                                    db.checkSeq()
                                    db.addItem1()
                                    db.summeryValue()
                                    txt_sku_qty.setText(sku_qty)
                                    txt_lc_qty.setText(lc_qty)
                                    txt_pdName.setText(pdName)
                                    txt_cost.setText(cost.toString())
                                    txt_pack.setText(packSz.toString())
                                    txt_status.setText(status)
                                    txt_stock.setText(stock.toString())
                                    txt_sku_name.setText(scan_bc.text.toString())
                                    total_am.setText(lc_value)
                                    scan_bc.setText("")
                                    scan_bc.requestFocus()

                                    imgComplete.visibility = View.GONE
                                    imgImcomplete.visibility = View.VISIBLE
                                }
                            }
                        }
                    } else {
                        txt_sku_qty.setText("")
                        txt_lc_qty.setText("")
                        txt_pdName.setText("")
                        txt_cost.setText("")
                        txt_pack.setText("")
                        txt_status.setText("")
                        txt_stock.setText("")
                        txt_sku_name.setText(scan_bc.text.toString())
                        total_am.setText("")
                        scan_bc.setText("")
                        scan_bc.requestFocus()
                        Toast.makeText(this, "Barcode Not Found", Toast.LENGTH_SHORT).show()
                        val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
                    }
                }
            }

            false

        })

        scan_bc.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->

            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                if (scan_bc.text.toString() == "") {
                    Toast.makeText(this, "Please Enter SKU", Toast.LENGTH_SHORT).show()
                    scan_bc.setText("")
                } else {
                    sku = scan_bc.text.toString()
                    qty = scan_qty.text.toString()
                    filename = txt_doc.text.toString()
                    updateCheck = "no"
                    db.checkItem1()

                    if (ckItem == 1) {
                        if (status!!.length == 1) {
                            db.checkSeq()
                            db.addItem1()
                            db.summeryValue()
                            txt_sku_qty.setText(sku_qty)
                            txt_lc_qty.setText(lc_qty)
                            txt_pdName.setText(pdName)
                            txt_cost.setText(cost.toString())
                            txt_pack.setText(packSz.toString())
                            txt_status.setText(status)
                            txt_stock.setText(stock.toString())
                            txt_sku_name.setText(scan_bc.text.toString())
                            total_am.setText(lc_value)
                            scan_bc.setText("")
                            scan_bc.requestFocus()

                            imgComplete.visibility = View.GONE
                            imgImcomplete.visibility = View.VISIBLE
                        } else {
                            if (status!!.length > 1) {
                                if (status!!.substring(1) == "Non-Sales") {
                                    Toast.makeText(this, "Barcode Non-Sales", Toast.LENGTH_LONG)
                                        .show()

//                                    var mediaPlayer = MediaPlayer.create(
//                                        applicationContext,
//                                        R.raw.buzz
//                                    )
//                                    mediaPlayer.start()
//                                    val handler = Handler()
//                                    handler.postDelayed({ mediaPlayer.stop() }, 1 * 1000.toLong())

//                                    val afd: AssetFileDescriptor = assets.openFd("buzz.wav")
//                                    val player = MediaPlayer()
//                                    player.setDataSource(
//                                        afd.getFileDescriptor(),
//                                        afd.getStartOffset(),
//                                        afd.getLength()
//                                    )
//                                    player.prepare()
//                                    player.start()
//                                    val handler = Handler()
//                                    handler.postDelayed({ player.stop() }, 1 * 1000.toLong())
                                    alertDialog1(status!!)
                                    AsyncTaskRunner(this).execute()
                                } else {
                                    db.checkSeq()
                                    db.addItem1()
                                    db.summeryValue()
                                    txt_sku_qty.setText(sku_qty)
                                    txt_lc_qty.setText(lc_qty)
                                    txt_pdName.setText(pdName)
                                    txt_cost.setText(cost.toString())
                                    txt_pack.setText(packSz.toString())
                                    txt_status.setText(status)
                                    txt_stock.setText(stock.toString())
                                    txt_sku_name.setText(scan_bc.text.toString())
                                    total_am.setText(lc_value)
                                    scan_bc.setText("")
                                    scan_bc.requestFocus()

                                    imgComplete.visibility = View.GONE
                                    imgImcomplete.visibility = View.VISIBLE
                                }
                            }
                        }
                    } else {
                        txt_sku_qty.setText("")
                        txt_lc_qty.setText("")
                        txt_pdName.setText("")
                        txt_cost.setText("")
                        txt_pack.setText("")
                        txt_status.setText("")
                        txt_stock.setText("")
                        txt_sku_name.setText(scan_bc.text.toString())
                        total_am.setText("")
                        scan_bc.setText("")
                        scan_bc.requestFocus()
                        Toast.makeText(this, "Barcode Not Found", Toast.LENGTH_SHORT).show()
                        val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
                    }

//                    }

                }
            }

            false

        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun readFromFile() {
        var search: Int = 0
        val file = File("/sdcard/Download/result.csv")
        val fs: FileInputStream = FileInputStream(file)
        val br: BufferedReader = BufferedReader(InputStreamReader(fs))
        var lines = br.readLines()
        for (line in lines) {
            val array = line.split(",").toTypedArray()
            println(array[0])
            if (scan_bc.text.toString() == array[0]) {
                array[1] = scan_qty.text.toString()
                println(array[1])
                search = 1
                break
            } else {
                search = 0

            }

        }
    }

    fun getTime(){
        val time = SimpleDateFormat("hh:mma")
        val curFormater = SimpleDateFormat("dd/MM/yyyy hh:mma")
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mma")

        val c = Calendar.getInstance()
        val day = c[Calendar.DAY_OF_MONTH]
        val month = c[Calendar.MONTH]
        val year = c[Calendar.YEAR]
        val t = time.format(Date())
        date = day.toString() + "/" + month + "/" + year+" "+t
        val dateObj = curFormater.parse(date)
        formatted_date = sdf.format(dateObj)


//        val curFormater = SimpleDateFormat("d/m/yyyy/hhmm")
//        val dateObj = sdf.parse(date)
//        currentDate = sdf.format(dateObj)
//        println(currentDate)
    }


    private fun writeToFile(
        data: String, context: Context
    ) {
        try {
            val saveFile = File("/sdcard/Stock Export/$doc_name")
            val fw = FileWriter(saveFile.absoluteFile, true)
            val bw = BufferedWriter(fw)
            bw.write(data + "\r\n")
            bw.flush()

        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }

//    private fun getListFiles2(parentDir: File): List<File>? {
//        val inFiles: MutableList<File> = ArrayList()
//        val files: Queue<File> = LinkedList()
//        files.addAll(parentDir.listFiles())
//        while (!files.isEmpty()) {
//            val file = files.remove()
//            if (file.isDirectory) {
//                files.addAll(file.listFiles())
//            } else if (file.name.endsWith(".csv")) {
//                val lastModDate = Date(file.lastModified())
//                inFiles.add(file)
//                println(lastModDate.toString())
//            }
//        }
//        println(inFiles)
//        return inFiles
//    }

    val Summery: MutableList<File_list>
        get() {
            val file1 = File("/sdcard/Download/result.csv")
            val seItem = ArrayList<File_list>()
            val inFiles: MutableList<File> = ArrayList()
            val files: Queue<File> = LinkedList()
            files.addAll(File("/sdcard/Download/").listFiles())


            while (!files.isEmpty()) {
                val file = files.remove()
                if (file.isDirectory) {
                    files.addAll(file.listFiles())
                } else if (file.name.endsWith(".csv")) {


                    val fs: FileInputStream = FileInputStream(file1)
                    val br: BufferedReader = BufferedReader(InputStreamReader(fs))
                    var lines = br.readLine()
                    val array = lines.split(",").toTypedArray()

                    val item = File_list()
                    item.file_name = array[0]
                    item.file_time = array[1]
                    seItem.add(item)
                    println(seItem[0].file_name)
                }
            }
            return seItem
        }

    private fun exportDialog(type: Int, context: Context) {

        val builder= AlertDialog.Builder(this)
        val inflater=this.layoutInflater
        val view=inflater.inflate(R.layout.activity_export, null)
        builder.setView(view)
        dialog=builder.create()
        dialog.window?.attributes?.windowAnimations=type
        dialog.setMessage("THE DATA WILL BE EXPORTED TO sdcard/Stock Export")
        progressBar1 = view.findViewById(R.id.progress_bar)
        progressBar1!!.visibility = View.GONE
        dialog.show()

        export = view.findViewById(R.id.btn_export)
        export.setOnClickListener {
            progressBar1!!.visibility = View.VISIBLE

            Handler().postDelayed({

                val filepath = "/sdcard/Stock Export/$doc_name.csv"
                val file = File(filepath)
                if (file.exists()) {
                    export()
//                Toast.makeText(this, "FILE EXPORT SUCCESSFUL", Toast.LENGTH_LONG).show()

                } else {
                    generateNoteOnSD(this, "/$doc_name.csv")
                    if (file.exists()) {
                        export()
//                    Toast.makeText(this, "FILE EXPORT SUCCESSFUL", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(
                            this,
                            "EXPORT UNSUCCESSFUL. MAKE SURE TO GIVE STORAGE ACCESS",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }, 1000)


        }
    }

    fun generateNoteOnSD(context: Context, sFileName: String) {
        try {
            val root=File("/sdcard/Stock Export")
            if (!root.exists()) {
                root.mkdirs()
            }
            val gpxfile=File(root, sFileName)
            val writer=FileWriter(gpxfile)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun export() {

        progressBar1!!.visibility = View.GONE

        try {
            db= DataBase(this)
            val database=this.openOrCreateDatabase("summery.db", Context.MODE_PRIVATE, null)
            val selectQuery=
                " SELECT Seq,StockTakeID,DocNum,Inspector,Seq,Location,SKU,Barcode,IBC,SBC,ProductName,QNT,SalePrice,DateTime FROM Summery WHERE DocNum='$doc_name'"
            val cursor=database.rawQuery(selectQuery, null)
            var rowcount: Int
            var colcount: Int

            println(doc_name)
            val saveFile=File("/sdcard/Stock Export/$doc_name.csv")
            val fw=FileWriter(saveFile)

            var k = 1
            val bw=BufferedWriter(fw)
            rowcount=cursor.getCount()
            colcount=cursor.getColumnCount()
            bw.write("rowid,StockTakeID,DocNum,Inspector,SEQ,Location,SKU,BARCODE,IBC,SBC,ProductName,QNT,SalePrice,DateTime")
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

                            bw.write(cursor!!.getString(j) + ",")

                        }
                        if (j == 7) {

                            bw.write(cursor!!.getString(j) + ",")
                        }
                        if (j == 8) {

                            if(cursor.getString(j) == null || cursor.getString(j) == "")
                            {
                                bw.write(",")
                            }
                            else {
                                bw.write(cursor!!.getString(j) + ",")
                            }

                        }
                        if (j == 9) {

                            if(cursor.getString(j) == null || cursor.getString(j) == "")
                            {
                                bw.write(",")
                            }
                            else {
                                bw.write(cursor!!.getString(j) + ",")
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
            db.updateStatus()
            println(location + filename)
            Toast.makeText(this, "EXPORT SUCCESSFUL", Toast.LENGTH_LONG).show()
            dialog.dismiss()

            var filecountCheck = 0
            val query = "SELECT seq FROM date WHERE date='${com.example.ui.DataBasrHandler.date}'"
            val cursor1 = database.rawQuery(query,null)
            if(cursor1.moveToLast()){
                filecountCheck = cursor1.getInt(0)
                println("FILE COUNT $filecountCheck    $countAlert")
            }
            else{
                filecountCheck = 0
                println("FILE COUNT $filecountCheck  $countAlert")
            }
            database.close()
            imgImcomplete.visibility = GONE
            imgComplete.visibility = VISIBLE

            if(countAlert <= filecountCheck){
                fileCountAlert(filecountCheck)
            }
            else{
                if(requestBack == 1){
                    if(ck_activity == "0"){
                        val a=Intent(this, ViewStock::class.java)
                        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(a)
                    }
                    else{
                        val a=Intent(this, UserRecord::class.java)
                        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(a)
                    }
                }
                else{

                }
            }

//            confirmDel()

        }
        catch (ex: Exception) {
            ex.printStackTrace()

        }

    }

    private fun fileCountAlert(v:Int){
        lateinit var dialog: androidx.appcompat.app.AlertDialog

        // Initialize a new instance of alert dialog builder object
        val builder= androidx.appcompat.app.AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("File Count Alert! ($countAlert)")

        // Set a message for alert dialog
        builder.setMessage("Total counted file is $v")


        // On click listener for dialog buttons
        val dialogClickListener= DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    dialog.dismiss()
                }

            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("OK", dialogClickListener)

        // Initialize the AlertDialog using builder object
        dialog=builder.create()
        dialog.setCancelable(false)
        // Finally, display the alert dialog
        dialog.show()
    }

    private fun alertDialog1(status: kotlin.String){
        lateinit var dialog: androidx.appcompat.app.AlertDialog

        // Initialize a new instance of alert dialog builder object
        val builder= androidx.appcompat.app.AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("Do you want to continue?")

        // Set a message for alert dialog
        builder.setMessage("Status : $status")


        // On click listener for dialog buttons
        val dialogClickListener= DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    db.checkSeq()
                    db.addItem1()
                    db.summeryValue()
                    txt_sku_qty.setText(sku_qty)
                    txt_lc_qty.setText(lc_qty)
                    txt_pdName.setText(pdName)
                    txt_cost.setText(cost.toString())
                    txt_pack.setText(packSz.toString())
                    txt_status.setText(com.example.ui.DataBasrHandler.status)
                    txt_stock.setText(stock.toString())
                    txt_sku_name.setText(scan_bc.text.toString())
                    total_am.setText(lc_value)
                    scan_bc.setText("")
                    scan_bc.requestFocus()

                    imgComplete.visibility = View.GONE
                    imgImcomplete.visibility = View.VISIBLE
                    updateCheck = "no"
                }
                DialogInterface.BUTTON_NEUTRAL -> {
                    scan_qty.setText("1")
                    scan_bc.setText("")
                    scan_bc.requestFocus()
                    updateCheck = "yes"
                    dialog.dismiss()

                }
            }
        }

        // Set the alert dialog positive/yes button
        builder.setPositiveButton("YES", dialogClickListener)

        // Set the alert dialog neutral/cancel button
        builder.setNeutralButton("NO", dialogClickListener)

        // Initialize the AlertDialog using builder object
        dialog=builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }

    private fun loadCount(){
        var prefs = getSharedPreferences("count", Activity.MODE_PRIVATE)
        countAlert = prefs.getInt("valCount", 50)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemid = item.itemId

        if (itemid == R.id.action_edit) {

            db.getItem()
            if(itemDetail == " , , , , , , , , , , , ,"){
                Toast.makeText(this, "No Scanned Data", Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent(this, Edit_stock::class.java)
                startActivity(intent)
            }
            return true
        }

        if (itemid == R.id.action_save) {
            exportDialog(R.style.DialogSlide, this)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onBackPressed() {

        if(updateCheck == "no"){
            requestBack = 1
            exportDialog(R.style.DialogSlide, this)
        }

        else{
            requestBack =0
            if(ck_activity == "0"){
                val a=Intent(this, ViewStock::class.java)
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(a)
            }
            else{
                val a=Intent(this, UserRecord::class.java)
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(a)
            }
            super.onBackPressed()
        }
    }

    private class AsyncTaskRunner(val context: Context?) : AsyncTask<String, String, String>() {


        override fun doInBackground(vararg params: String?): String {

            return "gg"
        }

        override fun onPreExecute() {
            val afd: AssetFileDescriptor = context!!.assets.openFd("buzz.wav")
            val player = MediaPlayer()
            player.setDataSource(
                afd.getFileDescriptor(),
                afd.getStartOffset(),
                afd.getLength()
            )
            player.prepare()
            player.start()
            val handler = Handler()
            handler.postDelayed({ player.stop() }, 1 * 1000.toLong())
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

    }
}