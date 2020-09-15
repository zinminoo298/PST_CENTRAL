package com.example.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.ui.DataBasrHandler.*
import com.example.ui.Modle.File_list
import kotlinx.android.synthetic.main.check_stock_multiscan.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Check_stock_Multiscan : AppCompatActivity() {


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
        setContentView(R.layout.check_stock_multiscan)

        imgComplete = findViewById(R.id.complete)
        imgImcomplete = findViewById(R.id.imcomplete)

        if(updateCheck == "no"){
            imgComplete.visibility = View.GONE
            imgImcomplete.visibility = View.VISIBLE
        }
        else{
            imgImcomplete.visibility = View.GONE
            imgComplete.visibility = View.VISIBLE
        }

        mTopToolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(mTopToolbar)

        scan_qty.setText("")
        txt_sku_qty.setText("")
        txt_lc_qty.setText("")
        txt_pdName.setText("")
        txt_cost.setText("")
        txt_pack.setText("")
        txt_status.setText("")
        txt_stock.setText("")
        txt_sku_name.setText("")
        total_am.setText("")
        scan_qty.setText("1")

        val Lock:ImageView
        val Unlock:ImageView

        db = DataBase(this)
        db.getBu()
        storename.setText(storeName)
        storecode.setText(storeCode)
        bu.setText(BU)
        countid.setText("COUNT ID : "+stockTakeID)
//
//        Lock = findViewById(R.id.lock)
//        Unlock = findViewById(R.id.unlock)

//        Lock.setOnClickListener {
//            scan_qty.isFocusable  = true
//            scan_qty.isFocusableInTouchMode = true
//            scan_qty.requestFocus()
//            Lock.visibility = GONE
//            Unlock.visibility = View.VISIBLE
//        }
//
//        Unlock.setOnClickListener {
//            scan_qty.isFocusable = false
//            scan_qty.isFocusableInTouchMode = false
//            Lock.visibility = View.VISIBLE
//            Unlock.visibility = GONE
//        }

        /*Bottom Nav Bar actions*/
//        val bottomnavigationview: BottomNavigationView = findViewById(R.id.bottom_navigation)
//        bottomnavigationview.setOnNavigationItemSelectedListener { menuItem ->
//
//            when (menuItem.itemId) {
//                R.id.action_clear -> {
//                    exportDialog(R.style.DialogSlide,this)
//
//                }
//
//                R.id.action_edit -> {
//                    val intent = Intent(this,Edit_stock::class.java)
//                    startActivity(intent)
//                }
//
//                R.id.action_back -> {
//                    if(ck_activity == "0"){
//                        val a=Intent(this, ViewStock::class.java)
//                        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        startActivity(a)
//                    }
//                    else{
//                        val a=Intent(this, UserRecord::class.java)
//                        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        startActivity(a)
//                    }                }
//
//            }
//            true
//
//        }

        txt_doc.setText(doc_name)
        txt_lc.setText(location)

        scan_bc.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                if(scan_bc.text.toString() == ""){
                    Toast.makeText(this,"Please Enter SKU",Toast.LENGTH_SHORT).show()
                }

                else{
                    sku = scan_bc.text.toString()
                    db.showDetail()
                    if(ck == 1){
                        txt_sku_qty.setText(sku_qty)
                        txt_lc_qty.setText("")
                        txt_pdName.setText(pdName)
                        txt_cost.setText(cost.toString())
                        txt_pack.setText(packSz.toString())
                        txt_status.setText(status)
                        txt_stock.setText(stock.toString())
                        txt_sku_name.setText(scan_bc.text.toString())
                        total_am.setText("")

//                        scan_bc.setText("")
                        scan_qty.setText("1")
                        scan_qty.requestFocus()
                        scan_qty.setSelectAllOnFocus(true)
                    }

                    else{
                        Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show()
                        scan_bc.setText("")
                        scan_bc.requestFocus()
                    }
                }
            }

            false

        })

        scan_bc.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->

            if (event.keyCode == KeyEvent.KEYCODE_SPACE && event.action == KeyEvent.ACTION_UP || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                if(scan_bc.text.toString() == ""){
                    Toast.makeText(this,"Please Enter SKU",Toast.LENGTH_SHORT).show()
                }

                else{
                    sku = scan_bc.text.toString()
                    db.showDetail()
                    if(ck == 1){
                        txt_sku_qty.setText(sku_qty)
                        txt_lc_qty.setText("")
                        txt_pdName.setText(pdName)
                        txt_cost.setText(cost.toString())
                        txt_pack.setText(packSz.toString())
                        txt_status.setText(status)
                        txt_stock.setText(stock.toString())
                        txt_sku_name.setText(scan_bc.text.toString())
                        total_am.setText("")

//                        scan_bc.setText("")
                        scan_qty.setText("1")
                        scan_qty.requestFocus()
                        scan_qty.setSelectAllOnFocus(true)
                    }

                    else{
                        Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show()
                        scan_bc.setText("")
                        scan_bc.requestFocus()
                    }
                }
            }

            false

        })


        scan_qty.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->

            if (event.keyCode == KeyEvent.KEYCODE_SPACE && event.action == KeyEvent.ACTION_UP || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                if(scan_bc.text.toString() == ""){
                    Toast.makeText(this,"Please Enter BARCODE",Toast.LENGTH_SHORT).show()
                    scan_bc.setText("")
                }

                else{
                    sku = scan_bc.text.toString()
                    qty =scan_qty.text.toString()
                    filename = txt_doc.text.toString()
                    updateCheck = "no"
                    db.checkItem1()

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
                    scan_qty.setText("1")
                    scan_bc.requestFocus()
                    imgComplete.visibility = View.GONE
                    imgImcomplete.visibility = View.VISIBLE
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

                val filepath="/sdcard/Stock Export/$doc_name.csv"
                val file=File(filepath)
                if(file.exists())
                {
                    export()
//                Toast.makeText(this, "FILE EXPORT SUCCESSFUL", Toast.LENGTH_LONG).show()

                }
                else{
                    generateNoteOnSD(this,"/$doc_name.csv")
                    if(file.exists())
                    {
                        export()
//                    Toast.makeText(this, "FILE EXPORT SUCCESSFUL", Toast.LENGTH_LONG).show()
                    }

                    else{
                        Toast.makeText(this, "EXPORT UNSUCCESSFUL. MAKE SURE TO GIVE STORAGE ACCESS", Toast.LENGTH_LONG).show()
                    }

                }
            },1000)


        }
    }

    fun generateNoteOnSD(context: Context, sFileName: String) {
        try {
            val root=File( "/sdcard/Stock Export")
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

                            bw.write(cursor!!.getString(j)+",")

                        }
                        if (j == 2) {

                            bw.write(cursor!!.getString(j)+",")

                        }
                        if (j == 3) {

                            bw.write(cursor!!.getString(j)+",")

                        }
                        if (j == 4) {

                            bw.write(cursor!!.getString(j)+",")

                        }
                        if (j == 5) {

                            bw.write(cursor!!.getString(j)+",")

                        }
                        if (j == 6) {

                            bw.write(cursor!!.getString(j)+",")

                        }
                        if (j == 7) {

                            bw.write(cursor!!.getString(j)+",")
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

                            bw.write(cursor!!.getString(j)+",")

                        }

                        if (j == 11) {

                            if(cursor.getString(j) == null || cursor.getString(j) == "")
                            {
                                bw.write(",")
                            }
                            else {
                                bw.write(cursor!!.getString(j)+",")
                            }

                        }

                        if (j == 12) {
                            bw.write(cursor!!.getString(j)+",")
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
            Toast.makeText(this,"EXPORT SUCCESSFUL",Toast.LENGTH_LONG).show()
            dialog.dismiss()
            database.close()
            imgImcomplete.visibility = View.GONE
            imgComplete.visibility = View.VISIBLE
//            confirmDel()

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
        catch (ex: Exception) {
            ex.printStackTrace()

        }

    }

    private fun alertDialog(qty: kotlin.String){
        lateinit var dialog: androidx.appcompat.app.AlertDialog

        // Initialize a new instance of alert dialog builder object
        val builder= androidx.appcompat.app.AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("Confirm QTY?")

        // Set a message for alert dialog
        builder.setMessage("Quantity : $qty")


        // On click listener for dialog buttons
        val dialogClickListener= DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    db.addDate()
                    val intent = Intent(this,Check_stock_Multiscan::class.java)
                    startActivity(intent)               }
                DialogInterface.BUTTON_NEUTRAL -> {
                    dialog.dismiss()
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



    private fun dialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.new_item,null)
        builder.setView(view)
        val dialog: AlertDialog = builder.create()
//        dialog.window?.attributes?.windowAnimations = R.style.DialogSlide
//        dialog.setMessage("Please Enter Password!!")
        dialog.show()

        txt_sku =view.findViewById<TextView>(R.id.txt_bc)
        edt_desc = view.findViewById<EditText>(R.id.edt_desc)
        btn_save = view.findViewById<Button>(R.id.btn_save)
        btn_cancel = view.findViewById<Button>(R.id.btn_cancel)
        txt_sku.setText(sku)

        btn_save.setOnClickListener{
            desc = edt_desc.text.toString()
            db.addItem()

            db.viewData()
            txt_sku_qty.setText(sku_qty)
            txt_lc_qty.setText(lc_qty)
            txt_pdName.setText(newitem)
            txt_cost.setText("")
            txt_pack.setText("")
            txt_status.setText("")
            txt_stock.setText("")
            txt_sku_name.setText(sku)
            total_am.setText(lc_value)

            getTime()
            val line = "$doc_name,$insp,$location,${sku},${edt_desc.text},0,${scan_qty.text.toString()},$formatted_date"
            val sb = StringBuilder()
            var rest = 188 - line.length
            sb.append(line)
            for(i in 1..rest){
                sb.append(" ")
            }
            println(sb.length)
//            writeToFile(line,this)
            Toast.makeText(this, "Scan Completed", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }

        btn_cancel.setOnClickListener{
            dialog.dismiss()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_edit) {
            db.getItem()
            if(itemDetail == " , , , , , , , , , , , ,"){
                Toast.makeText(this,"No Scanned Data",Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent(this,Edit_Multistock::class.java)
                startActivity(intent)
            }
            return true
        }

        if (id == R.id.action_save) {
            exportDialog(R.style.DialogSlide,this)
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
            exportDialog(R.style.DialogSlide,this)
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
}