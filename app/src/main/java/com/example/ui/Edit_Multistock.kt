package com.example.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ui.DataBasrHandler.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_edit__multistock.*
import kotlinx.android.synthetic.main.activity_edit__multistock.id
import kotlinx.android.synthetic.main.activity_edit__multistock.totalam
import kotlinx.android.synthetic.main.activity_edit__multistock.txt_seq
import kotlinx.android.synthetic.main.activity_edit__multistock.txtbc
import kotlinx.android.synthetic.main.activity_edit__multistock.txtdoc
import kotlinx.android.synthetic.main.activity_edit__multistock.txtlc
import kotlinx.android.synthetic.main.activity_edit__multistock.txtlc_qty
import kotlinx.android.synthetic.main.activity_edit__multistock.txtsku_qty
import kotlinx.android.synthetic.main.check_stock.txt_cost
import kotlinx.android.synthetic.main.check_stock.txt_pack
import kotlinx.android.synthetic.main.check_stock.txt_pdName
import kotlinx.android.synthetic.main.check_stock.txt_status
import kotlinx.android.synthetic.main.check_stock.txt_stock
import java.io.*

class Edit_Multistock : AppCompatActivity() {

    internal lateinit var db:DataBase
    internal lateinit var txtQuantity: EditText
    internal lateinit var btnFirst : Button
    internal lateinit var btnLast : Button
    internal lateinit var btnBack : Button
    internal lateinit var btnNext : Button
    internal lateinit var imgComplete: ImageView
    internal lateinit var imgImcomplete: ImageView

    var noItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit__multistock)

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
        txt_seq.setText("${com.example.ui.DataBasrHandler.quantity} / ${com.example.ui.DataBasrHandler.quantity}")
        txtsku_qty.requestFocus()
        ck_qty = com.example.ui.DataBasrHandler.quantity
//        AsyncTaskRunner(this).execute()

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
                txtsku_qty.requestFocus()

                if(com.example.ui.DataBasrHandler.quantity == 0){
                    txt_seq.setText("0 / ${com.example.ui.DataBasrHandler.quantity}")
                    ck_qty= 0
                }
                else{
                    txt_seq.setText("1 / ${com.example.ui.DataBasrHandler.quantity}")
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
                txt_seq.setText("${com.example.ui.DataBasrHandler.quantity} / ${com.example.ui.DataBasrHandler.quantity}")
                txtsku_qty.requestFocus()

                ck_qty = com.example.ui.DataBasrHandler.quantity

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
                txtsku_qty.requestFocus()


                val back_qty = ck_qty-1
                if(com.example.ui.DataBasrHandler.quantity <= 1){
                    txt_seq.setText("${com.example.ui.DataBasrHandler.quantity} / ${com.example.ui.DataBasrHandler.quantity}")
                }
                else{
                    if(back_qty< 1){
                        txt_seq.setText("${ck_qty} / ${com.example.ui.DataBasrHandler.quantity}")
                    }
                    else{
                        txt_seq.setText("${back_qty} / ${com.example.ui.DataBasrHandler.quantity}")
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
                txtsku_qty.requestFocus()

                val next_qty = ck_qty+1
                if(com.example.ui.DataBasrHandler.quantity <= 1){
                    txt_seq.setText("${com.example.ui.DataBasrHandler.quantity} / ${com.example.ui.DataBasrHandler.quantity}")
                }
                else{
                    if(next_qty> com.example.ui.DataBasrHandler.quantity){
                        txt_seq.setText("${ck_qty} / ${com.example.ui.DataBasrHandler.quantity}")
                    }
                    else{
                        txt_seq.setText("${next_qty} / ${com.example.ui.DataBasrHandler.quantity}")
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
                    if (noItem == 1) {
                        Toast.makeText(this, "No data to delete", Toast.LENGTH_SHORT).show()
                    } else {
                        db.deleteItem()
                        updateCheck = "no"
                        db.updateStatus()

                        db.getNextItem()
                        if (nextCheck == 1) {
                            val itemArray = itemDetail.split(",").toTypedArray()

                            id.setText("STOCK TAKE ID : " + itemArray[1])
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

                            if (ck_qty != com.example.ui.DataBasrHandler.quantity) {
                                txt_seq.setText("$ck_qty / ${com.example.ui.DataBasrHandler.quantity}")
                            }
                            if (ck_qty == com.example.ui.DataBasrHandler.quantity) {
                                txt_seq.setText("${ck_qty} / ${com.example.ui.DataBasrHandler.quantity}")
                            }
                            if (com.example.ui.DataBasrHandler.quantity == 0) {
                                txt_seq.setText("0 / 0")
                            }
                        }

                        if (nextCheck == 0) {
                            db.getPrvItem()

                            if (nextCheck == 1) {
                                val itemArray = itemDetail.split(",").toTypedArray()

                                id.setText("STOCK TAKE ID : " + itemArray[1])
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
                                txt_seq.setText("${com.example.ui.DataBasrHandler.quantity} / ${com.example.ui.DataBasrHandler.quantity}")
                            }
                            if (nextCheck == 0) {
                                txt_seq.setText("0 / 0")
                                noItem = 1
                                println("No More Item")
                                itemDetail = " , , , , , , , , , , , ,"

                                val itemArray = itemDetail.split(",").toTypedArray()

                                id.setText("STOCK TAKE ID : " + itemArray[1])
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
                    }

                    imgComplete.visibility = GONE
                    imgImcomplete.visibility = VISIBLE
                }

                R.id.action_save -> {
                    if (com.example.ui.DataBasrHandler.quantity == 0) {
                        Toast.makeText(this, "No data to edit", Toast.LENGTH_SHORT).show()
                    } else {
                        updateQty = txtQuantity.text.toString()
                        if (txtQuantity.text.toString() == null) {
                            Toast.makeText(this, "Please Enter Quantity", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Quantity Update Complete", Toast.LENGTH_SHORT)
                                .show()
                            db.updateQty()
                            updateCheck = "no"
                            db.updateStatus()

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

                            imgComplete.visibility = GONE
                            imgImcomplete.visibility = VISIBLE
                        }
                    }

                }

                R.id.action_back -> {
//                    if(ck_activity == "0"){
                    val a = Intent(this, Check_stock_Multiscan::class.java)
                    a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(a)
//                    }
//                    else{
//                        val a= Intent(this, UserRecord::class.java)
//                        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        startActivity(a)
//                }
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
