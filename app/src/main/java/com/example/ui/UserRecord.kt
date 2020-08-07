package com.example.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ui.DataBasrHandler.*
import kotlinx.android.synthetic.main.activity_user_record.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.String
import java.text.SimpleDateFormat
import java.util.*


var date_seq = 1
var load_dateseq = 1
var formatted = ""
var loadDate = ""
var checkseq = ""
var Date =""
var currentDate:kotlin.String = ""
var currentDate1:kotlin.String = ""
var doc_name:kotlin.String = ""
var spinner_lc:kotlin.String = ""

var ck_activity = ""

class UserRecord : AppCompatActivity() {

    internal lateinit var date_count:EditText
    internal lateinit var db:DataBase
    private var mac =""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_record)

        ck_activity = "1"
        loadseq()
        loaddate()
        loadMac()

        db = DataBase(this)
//        db.getLocation()
        date_count = findViewById(R.id.count_date)
//        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:a")
//        val date_format1 = SimpleDateFormat("dd-MM-yyyy")

        val edtLocation  = findViewById<EditText>(R.id.edt_location)

//        if (spinner != null) {
//            val adapter = ArrayAdapter(this,
//                android.R.layout.simple_spinner_item, Location)
//            spinner.adapter = adapter
//
//            spinner.onItemSelectedListener = object :
//                AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(parent: AdapterView<*>,
//                                            view: View, position: Int, id: Long) {
//                    Toast.makeText(this@UserRecord, "Selected item is "+ Location[position], Toast.LENGTH_SHORT).show()
//                    spinner_lc = Location[position]
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>) {
//                    // write code to perform some action
//                }
//            }
//        }

        val sdf = SimpleDateFormat("hh:mma")
        val date_format = SimpleDateFormat("yyyymmdd")
        val dbdate_format = SimpleDateFormat("dd MMM")
        val curFormater = SimpleDateFormat("dd/MM/yyyy/hh:mma")
        val curFormater1 = SimpleDateFormat("dd/mm/yyyy")
        val curFormater2 = SimpleDateFormat("dd/MM/yyyy")
        val curFormater3 = SimpleDateFormat("dd/MM")


        val format = SimpleDateFormat("dd-MMM-yyyy")



        val c = Calendar.getInstance()
        val day = c[Calendar.DAY_OF_MONTH]
        val month = c[Calendar.MONTH]
        val year = c[Calendar.YEAR]
        val t = sdf.format(Date())
        val mth = month + 1
        val date1 = "" + day + "/" + mth + "/" + year+"/"+t
        val date2 = "" + day + "/" + mth + "/" + year
        val date3 = "" + day + "/" + mth
        val dateObj = curFormater.parse(date1)
        val dateObj1 = curFormater1.parse(date2)
        val dateObj2 = curFormater2.parse(date2)
        val dateObj3 = curFormater3.parse(date3)



        currentDate = date_format.format(dateObj1)
        currentDate1 = format.format(dateObj2)
        val db_date = dbdate_format.format(dateObj3)
        val currentTime = sdf.format(dateObj)
        Date = currentDate
        date_count.setText(currentDate1)
        println("Current Time"+currentTime)


//        if(currentDate == loadDate){
//            formatted = String.format("%03d", date_seq)
//            doc_no.setText(currentDate+formatted)
//            date_count.setText(currentDate1)
//
//        }
//        else{
//            load_dateseq = 1
//            formatted = String.format("%03d", load_dateseq)
//            doc_no.setText(currentDate+formatted)
//            date_count.setText(currentDate1)
//        }

        btn_next.setOnClickListener{

            if(store.text.toString()=="" || inspector.text.toString()=="")
            {
                Toast.makeText(this,"Please Enter Store and Inspecor Name",Toast.LENGTH_SHORT).show()
            }

            else{
                insp = inspector.text.toString()
                location = edtLocation.text.toString()
                createFile()

                name = doc_name
                date = db_date
                time = currentTime
                db.addDate()

                if(scanCheck == 0){
                    val intent = Intent(this,Check_stock::class.java)
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this,Check_stock_Multiscan::class.java)
                    startActivity(intent)
                }

            }
        }


//        date_count.setOnClickListener {
//            datePicker()
//        }
    }

    fun datePicker(){

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            val mth = monthOfYear + 1
            val date = "" + dayOfMonth + "/" + mth + "/" + year
            val format = SimpleDateFormat("dd-MMM-yyyy")
            val sdf = SimpleDateFormat("hh:mma")

            val date_format = SimpleDateFormat("yyyymmdd")
            val curFormater = SimpleDateFormat("dd/MM/yyyy")
            val dateObj = curFormater.parse(date)

            currentDate = date_format.format(dateObj)
            currentDate1 = format.format(dateObj)
            Date = currentDate
            date_count.setText(currentDate1)
//            if(currentDate == loadDate){
//                formatted = String.format("%03d", date_seq)
//                doc_no.setText(currentDate+formatted)
//                date_count.setText(currentDate1)
//
//            }
//            else{
//                load_dateseq = 1
//                formatted = String.format("%03d", load_dateseq)
//                doc_no.setText(currentDate+formatted)
//                date_count.setText(currentDate1)
//
//            }

//            date_count.setText(currentDate1)
//            doc_no.setText(currentDate+ formatted)

        }, year, month, day)

        dpd.show()
    }

    fun createFile(){
        val root=File( "/storage/emulated/0/Stock Export/")
        if (!root.exists()) {
            root.mkdirs()
        }

        val file = File("/sdcard/Stock Export")
        val list: Array<File> = file.listFiles()
        var count = 0
        for (f in list) {
            val name: kotlin.String = f.getName()
            if (name.endsWith(".csv")) count++
        }
        println("170 $count")
        var file_count = count
        var fileseq=1
        formatted = String.format("%03d", fileseq)
        val filepath="/storage/emulated/0/Stock Export/$mac$Date$formatted.csv"
        var filename=File(filepath)
        var checkfile_seq = 0

        for(i in 0..file_count){


            if(filename.exists()){
                fileseq++
                formatted = String.format("%03d", fileseq)
                filename =File( "/storage/emulated/0/Stock Export/$mac$Date$formatted.csv")
                println("Exists")
            }

            else{
                generateNoteOnSD(this,"/$mac$Date$formatted.csv/")
                doc_name = "$mac$Date$formatted.csv"
                val saveFile = File("/sdcard/Stock Export/$doc_name")
                val fw = FileWriter(saveFile.absoluteFile, true)
                val bw = BufferedWriter(fw)
                val line = "DocNum,Inspector,Location,Barcode,ProductName,SalePrice,QNT,DateTime"
                val sb = StringBuilder()
                var rest = 188 - line.length
                sb.append(line)
                for(i in 1..rest){
                    sb.append(" ")
                }
                bw.write(sb.toString() + "\r\n")
                bw.flush()
                checkfile_seq = 0
                println("NEW")
                setseq(fileseq+1)
                break
            }


        }
    }

    fun generateNoteOnSD(context: Context, sFileName: kotlin.String) {
        try {
            val root=File( "/storage/emulated/0/Stock Export/")
            if (!root.exists()) {
                root.mkdirs()
            }
            val gpxfile=File(root, sFileName)
            val writer= FileWriter(gpxfile)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun setseq( v: kotlin.Int) {
        var editor=getSharedPreferences("seq", MODE_PRIVATE).edit()
        editor.putInt("seq", v)
        editor.apply()
    }

    private fun setdate(v: kotlin.String){
        var editor = getSharedPreferences("date",MODE_PRIVATE).edit()
        editor.putString("date", v)
        editor.apply()
    }

    private fun loadseq(){
        var prefs = getSharedPreferences("seq", Context.MODE_PRIVATE)
        var seq = prefs.getInt("seq",1)
        date_seq = seq

    }

    private fun loaddate(){
        var prefs = getSharedPreferences("date", Context.MODE_PRIVATE)
        var date = prefs.getString("date","")
        loadDate = date.toString()
    }

    private fun loadMac() {
        var prefs = getSharedPreferences("mac", Activity.MODE_PRIVATE)
        mac = prefs.getString("valMac", "").toString()
    }

    override fun onBackPressed() {
            val a=Intent(this, MainActivity::class.java)
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(a)
        super.onBackPressed()
    }

}
