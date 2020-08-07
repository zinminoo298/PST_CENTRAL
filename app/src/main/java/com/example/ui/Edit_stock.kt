package com.example.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.opencsv.CSVReaderBuilder
import kotlinx.android.synthetic.main.activity_edit_stock.*
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

var msg = ""
var count = 0
val result: ArrayList<String> = ArrayList()
var barcode = ""
var quantity= ""
internal lateinit var edtBarcode: TextView
internal lateinit var edtQuantity: EditText
internal lateinit var btnFirst : Button
internal lateinit var btnLast : Button
internal lateinit var btnBack : Button
internal lateinit var btnNext : Button


class Edit_stock : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_stock)

        edtBarcode = findViewById(R.id.scan_bc)
        edtQuantity = findViewById(R.id.scan_qty)
        btnFirst = findViewById(R.id.btn_first)
        btnLast = findViewById(R.id.btn_last)
        btnBack = findViewById(R.id.btn_back)
        btnNext = findViewById(R.id.btn_next)

        result.clear()
        AsyncTaskRunner(this).execute()

        btnFirst.setOnClickListener {
            try {
                RandomAccessFile("/sdcard/Download/test1.csv", "rw").use { f ->

                    f.seek(190)
                    msg = f.readLine()
                    var array = msg.split(",").toTypedArray()
                    barcode = array[3]
                    quantity = array[6]
                    edtBarcode.setText(barcode)
                    edtQuantity.setText(quantity)
                    println(count)}

            } catch (ex: IOException) {
                println("Error Occurs")
            }
        }

        btnLast.setOnClickListener {
            try {
                RandomAccessFile("/sdcard/Download/test1.csv", "rw").use { f ->

                    println("count"+(188* (count-1)))
                    f.seek(((188* (count-1))).toLong())
//                    f.seek(564)
                    msg = f.readLine()
                    println("MSG is "+msg)
                    var array = msg.split(",").toTypedArray()
                    barcode = array[3]
                    quantity = array[6]
                    edtBarcode.setText(barcode)
                    edtQuantity.setText(quantity)
                    println(count)}

            } catch (ex: IOException) {
                println("Error Occurs")
            }
        }

        btnBack.setOnClickListener {

        }

        btnNext.setOnClickListener {

        }



    }

    private class AsyncTaskRunner(val context: Context?) : AsyncTask<String, String, String>() {
        internal lateinit var pgd: ProgressDialog

        override fun doInBackground(vararg params: String?): String {
//            openCV()
            randomAccess()
            return "gg"
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Uploading")
            pgd.setTitle("Uploading Master")

            pgd.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                "Cancel",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            pgd.dismiss()
            edtBarcode.setText(barcode)
            edtQuantity.setText(quantity)
//            Toast.makeText(context,"^_^ Upload Completed", Toast.LENGTH_LONG).show()
            super.onPostExecute(result)
        }

        private fun callData() {
            val file = "/sdcard/Download/test1.csv"
            val fs: FileInputStream = FileInputStream(file)
            val br: BufferedReader = BufferedReader(InputStreamReader(fs))
            var lines = br.readLine()
            if (lines == null) {
                println("Lines null")
            } else {

                Scanner(FileReader(file)).use { s ->
                    while (s.hasNext()) {
                        result.add(s.nextLine())
                    }
                }
                println(result.size)

            }
        }

        private fun openCV(){
            // Create an object of file reader
            // class with CSV file as a parameter.
            // Create an object of file reader
            // class with CSV file as a parameter.
            val filereader = FileReader("/sdcard/Download/ok.csv")

            // create csvReader object and skip first Line

            // create csvReader object and skip first Line
            val csvReader = CSVReaderBuilder(filereader)
                .withSkipLines(1)
                .build()
            val allData = csvReader.readAll().get(1).toString().split(",").toTypedArray()
            val lines:String = allData.toString()
            val gg = lines.split(",").toTypedArray()

            println(allData)
        }

        private fun randomAccess(){

            try {
                RandomAccessFile("/sdcard/Download/test1.csv", "rw").use { f ->

                    while(f.readLine() != null){
                        count++
                    }
                    println(count)
                    f.seek(420)
                    msg = f.readLine()
                    println( msg.length)
                    println("MSG is "+msg)
                     var array = msg.split(",").toTypedArray()
                    barcode = array[3]
                    quantity = array[6]
                    println(count)}

            } catch (ex: IOException) {
                println("Error Occurs")
            }
        }
    }


}
