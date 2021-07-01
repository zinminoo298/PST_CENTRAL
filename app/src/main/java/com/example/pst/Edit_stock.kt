package com.example.pst

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pst.DataBaseHandler.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_edit_stock.*
import kotlinx.android.synthetic.main.activity_edit_stock.txt_cost
import kotlinx.android.synthetic.main.activity_edit_stock.txt_pack
import kotlinx.android.synthetic.main.activity_edit_stock.txt_pdName
import kotlinx.android.synthetic.main.activity_edit_stock.txt_status
import kotlinx.android.synthetic.main.activity_edit_stock.txt_stock
import java.io.*
import kotlin.collections.ArrayList

var msg = ""
var count = 0
val result: ArrayList<String> = ArrayList()
var barcode = ""
var quantity= ""
var ck_qty:Int = 0

class Edit_stock : AppCompatActivity() {

    internal lateinit var db:DataBase
    internal lateinit var txtQuantity: TextView
    internal lateinit var btnFirst : Button
    internal lateinit var btnLast : Button
    internal lateinit var btnBack : Button
    internal lateinit var btnNext : Button
    internal lateinit var imgComplete: ImageView
    internal lateinit var imgImcomplete: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_stock)

        txtQuantity = findViewById(R.id.txtsku_qty)
        btnFirst = findViewById(R.id.btn_first)
        btnLast = findViewById(R.id.btn_last)
        btnBack = findViewById(R.id.btn_back)
        btnNext = findViewById(R.id.btn_next)

        imgComplete = findViewById(R.id.complete)
        imgImcomplete = findViewById(R.id.imcomplete)

        if(updateCheck == "no"){
            imgComplete.visibility = GONE
            imgImcomplete.visibility = VISIBLE
        }
        else{
            imgImcomplete.visibility = GONE
            imgComplete.visibility = VISIBLE
        }

        db = DataBase(this)

        db.getBu()
        storename.setText(storeName)
        storecode.setText(storeCode)
        bu.setText(BU)
        id.setText("STOCK TAKE ID : "+stockTakeID)

        db.getItem()
        val itemArray = itemDetail.split(",").toTypedArray()

        id.setText("STOCK TAKE ID : "+itemArray[1])
        txtdoc.setText(itemArray[2])
        txtlc.setText(itemArray[3])
        txtbc.setText(itemArray[4])
        txt_pdName.setText(itemArray[5])
        txt_cost.setText(itemArray[6])
        txtsku_qty.setText(itemArray[7])
        txt_stock.setText(itemArray[8])
        txt_pack.setText(itemArray[9])
        txt_status.setText(itemArray[10])
        txtlc_qty.setText(itemArray[11])
        totalam.setText(itemArray[12])
        txt_seq.setText("${com.example.pst.DataBaseHandler.quantity} / ${com.example.pst.DataBaseHandler.quantity}")
        ck_qty = com.example.pst.DataBaseHandler.quantity
//        AsyncTaskRunner(this,txtQuantity).execute()

        btnFirst.setOnClickListener {
            try {
                db.getFirstItem()
                val itemArray = itemDetail.split(",").toTypedArray()

                id.setText("STOCK TAKE ID : "+itemArray[1])
                txtdoc.setText(itemArray[2])
                txtlc.setText(itemArray[3])
                txtbc.setText(itemArray[4])
                txt_pdName.setText(itemArray[5])
                txt_cost.setText(itemArray[6])
                txtsku_qty.setText(itemArray[7])
                txt_stock.setText(itemArray[8])
                txt_pack.setText(itemArray[9])
                txt_status.setText(itemArray[10])
                txtlc_qty.setText(itemArray[11])
                totalam.setText(itemArray[12])

                if(com.example.pst.DataBaseHandler.quantity == 0){
                    txt_seq.setText("0 / ${com.example.pst.DataBaseHandler.quantity}")
                    ck_qty= 0
                }
                else{
                    txt_seq.setText("1 / ${com.example.pst.DataBaseHandler.quantity}")
                    ck_qty= 1
                }

            } catch (ex: IOException) {
                println("Error Occurs")
            }
        }

        btnLast.setOnClickListener {
            try {
                db.getItem()
                val itemArray = itemDetail.split(",").toTypedArray()

                id.setText("STOCK TAKE ID : "+itemArray[1])
                txtdoc.setText(itemArray[2])
                txtlc.setText(itemArray[3])
                txtbc.setText(itemArray[4])
                txt_pdName.setText(itemArray[5])
                txt_cost.setText(itemArray[6])
                txtsku_qty.setText(itemArray[7])
                txt_stock.setText(itemArray[8])
                txt_pack.setText(itemArray[9])
                txt_status.setText(itemArray[10])
                txtlc_qty.setText(itemArray[11])
                totalam.setText(itemArray[12])
                txt_seq.setText("${com.example.pst.DataBaseHandler.quantity} / ${com.example.pst.DataBaseHandler.quantity}")
                ck_qty = com.example.pst.DataBaseHandler.quantity

            } catch (ex: IOException) {
                println("Error Occurs")
            }
        }

        btnBack.setOnClickListener {
            try {
                db.getPrvItem()
                val itemArray = itemDetail.split(",").toTypedArray()

                id.setText("STOCK TAKE ID : "+itemArray[1])
                txtdoc.setText(itemArray[2])
                txtlc.setText(itemArray[3])
                txtbc.setText(itemArray[4])
                txt_pdName.setText(itemArray[5])
                txt_cost.setText(itemArray[6])
                txtsku_qty.setText(itemArray[7])
                txt_stock.setText(itemArray[8])
                txt_pack.setText(itemArray[9])
                txt_status.setText(itemArray[10])
                txtlc_qty.setText(itemArray[11])
                totalam.setText(itemArray[12])

                val back_qty = ck_qty-1
                if(com.example.pst.DataBaseHandler.quantity <= 1){
                    txt_seq.setText("${com.example.pst.DataBaseHandler.quantity} / ${com.example.pst.DataBaseHandler.quantity}")
                }
                else{
                    if(back_qty< 1){
                        txt_seq.setText("${ck_qty} / ${com.example.pst.DataBaseHandler.quantity}")
                    }
                    else{
                        txt_seq.setText("${back_qty} / ${com.example.pst.DataBaseHandler.quantity}")
                        ck_qty = back_qty
                    }
                }

            } catch (ex: IOException) {
                println("Error Occurs")
            }
        }

        btnNext.setOnClickListener {
            try {
                db.getNextItem()
                val itemArray = itemDetail.split(",").toTypedArray()

                id.setText("STOCK TAKE ID : "+itemArray[1])
                txtdoc.setText(itemArray[2])
                txtlc.setText(itemArray[3])
                txtbc.setText(itemArray[4])
                txt_pdName.setText(itemArray[5])
                txt_cost.setText(itemArray[6])
                txtsku_qty.setText(itemArray[7])
                txt_stock.setText(itemArray[8])
                txt_pack.setText(itemArray[9])
                txt_status.setText(itemArray[10])
                txtlc_qty.setText(itemArray[11])
                totalam.setText(itemArray[12])

                val next_qty = ck_qty+1
                if(com.example.pst.DataBaseHandler.quantity <= 1){
                    txt_seq.setText("${com.example.pst.DataBaseHandler.quantity} / ${com.example.pst.DataBaseHandler.quantity}")
                }
                else{
                    if(next_qty> com.example.pst.DataBaseHandler.quantity){
                        txt_seq.setText("${ck_qty} / ${com.example.pst.DataBaseHandler.quantity}")
                    }
                    else{
                        txt_seq.setText("${next_qty} / ${com.example.pst.DataBaseHandler.quantity}")
                        ck_qty = next_qty
                    }
                }

            } catch (ex: IOException) {
                println("Error Occurs")
            }
        }

/*Bottom Nav Bar actions*/
        val bottomnavigationview: BottomNavigationView = findViewById(R.id.bottom_edit_navigation)
        bottomnavigationview.setOnNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.action_delete -> {
                    db.deleteItem()
                    updateCheck = "no"
                    db.updateStatus()

                    db.getNextItem()
                    if(nextCheck == 1){
                        val itemArray = itemDetail.split(",").toTypedArray()

                        id.setText("STOCK TAKE ID : "+itemArray[1])
                        txtdoc.setText(itemArray[2])
                        txtlc.setText(itemArray[3])
                        txtbc.setText(itemArray[4])
                        txt_pdName.setText(itemArray[5])
                        txt_cost.setText(itemArray[6])
                        txtsku_qty.setText(itemArray[7])
                        txt_stock.setText(itemArray[8])
                        txt_pack.setText(itemArray[9])
                        txt_status.setText(itemArray[10])
                        txtlc_qty.setText(itemArray[11])
                        totalam.setText(itemArray[12])

                        if(ck_qty != com.example.pst.DataBaseHandler.quantity){
                            txt_seq.setText("$ck_qty / ${com.example.pst.DataBaseHandler.quantity}")
                        }
                        if(ck_qty == com.example.pst.DataBaseHandler.quantity){
                            txt_seq.setText("${ck_qty} / ${com.example.pst.DataBaseHandler.quantity}")
                        }
                        if(com.example.pst.DataBaseHandler.quantity == 0){
                            txt_seq.setText("0 / 0")
                        }
                    }

                    if(nextCheck == 0){
                        db.getPrvItem()

                        if(nextCheck == 1){ val itemArray = itemDetail.split(",").toTypedArray()

                            id.setText("STOCK TAKE ID : "+itemArray[1])
                            txtdoc.setText(itemArray[2])
                            txtlc.setText(itemArray[3])
                            txtbc.setText(itemArray[4])
                            txt_pdName.setText(itemArray[5])
                            txt_cost.setText(itemArray[6])
                            txtsku_qty.setText(itemArray[7])
                            txt_stock.setText(itemArray[8])
                            txt_pack.setText(itemArray[9])
                            txt_status.setText(itemArray[10])
                            txtlc_qty.setText(itemArray[11])
                            totalam.setText(itemArray[12])

                            txt_seq.setText("${com.example.pst.DataBaseHandler.quantity} / ${com.example.pst.DataBaseHandler.quantity}")
                        }
                        if(nextCheck == 0){
                            txt_seq.setText("0 / 0")
                            println("No More Item")
                            itemDetail = " , , , , , , , , , , , ,"
                            val itemArray = itemDetail.split(",").toTypedArray()

                            id.setText("STOCK TAKE ID : "+itemArray[1])
                            txtdoc.setText(itemArray[2])
                            txtlc.setText(itemArray[3])
                            txtbc.setText(itemArray[4])
                            txt_pdName.setText(itemArray[5])
                            txt_cost.setText(itemArray[6])
                            txtsku_qty.setText(itemArray[7])
                            txt_stock.setText(itemArray[8])
                            txt_pack.setText(itemArray[9])
                            txt_status.setText(itemArray[10])
                            txtlc_qty.setText(itemArray[11])
                            totalam.setText(itemArray[12])
                        }
                    }


                    imgComplete.visibility = GONE
                    imgImcomplete.visibility = VISIBLE
                }

                R.id.action_back -> {
//                    if(ck_activity == "0"){
                        val a= Intent(this, Check_stock::class.java)
                        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(a)
//                    }
//                    else{
//                        val a= Intent(this, UserRecord::class.java)
//                        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        startActivity(a)
//                    }
                }

            }
            true

        }

    }

    private class AsyncTaskRunner(val context: Context?,val txtQuantity:TextView) : AsyncTask<String, String, String>() {
        internal lateinit var pgd: ProgressDialog

        override fun doInBackground(vararg params: String?): String {

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
//            edtBarcode.setText(barcode)
            txtQuantity.setText(quantity)
//            Toast.makeText(context,"^_^ Upload Completed", Toast.LENGTH_LONG).show()
            super.onPostExecute(result)
        }

    }


}
