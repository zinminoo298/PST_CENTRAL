package com.example.pst.Adapters

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.pst.DataBaseHandler.*
import com.example.pst.Modle.Detail
import com.example.pst.R
import kotlinx.android.synthetic.main.rowview_2.view.*

class Detail_Adapter(
    internal var seItem: MutableList<Detail>,
    val context: Context

) : BaseAdapter() {

    internal var inflater: LayoutInflater
    internal lateinit var db: DataBase

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowview:View
        var seq_Array = ArrayList< String>()
        for (i in 1 until Detail.size+1) {
            var formater =java.lang.String.format("%04d", i)
            seq_Array.add(formater)
        }

        rowview = inflater.inflate(R.layout.rowview_2,null)
        rowview.txt_sku.text = seItem[position].sku
        rowview.txt_qty.text = seItem[position].qty
        rowview.txt_seq.text = seq_Array[position]


        insp = Detail[position].insp

        rowview.setOnLongClickListener {
            lateinit var dialog: AlertDialog
            db=DataBase(context)
            barcode =  seItem[position].sku

            // Initialize a new instance of alert dialog builder object
            val builder= AlertDialog.Builder(context)

            // Set a title for alert dialog
            builder.setTitle("Delete Barcode : $barcode")

            // Set a message for alert dialog
            builder.setMessage("Are you sure??")


            // On click listener for dialog buttons
            val dialogClickListener= DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {

                        db.deleteRow(seItem[position].seq)
                        dialog.dismiss()
                        refresh(db.getDetail)
                    }
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

            return@setOnLongClickListener true
        }

        rowview.setOnClickListener{
            val firstChar = filename.first().toString()
            if(firstChar == "S"){

            }
            else{
                db = DataBase(context)
                barcode = Detail[position].sku
                val builder=android.app.AlertDialog.Builder(context)
                inflater=context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                /*show dialog update data*/
                val view=inflater.inflate(R.layout.edit_box, null)
                builder.setView(view)
                val dialog: android.app.AlertDialog=builder.create()

                val br = view.findViewById<TextView>(R.id.row_bc)
                val qty = view.findViewById<EditText>(R.id.row_qty)
                val btn = view.findViewById<Button>(R.id.btn_update)

                br.setText(seItem[position].sku.toString())
                qty.setText(seItem[position].qty.toString())
                dialog.show()

                btn.setOnClickListener {
                    if(qty.text.toString() == "null"){
                        Toast.makeText(context,"Please Enter Qty",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        db.updateRow(qty.text.toString(),seItem[position].seq)
                        dialog.dismiss()
                        refresh(db.getDetail)
                    }

                }
            }

        }

        return rowview
    }

    /*Refresh data after deleting data*/
    fun refresh(newList: MutableList<Detail>) {
        seItem.clear()
        seItem.addAll(newList)
        notifyDataSetChanged()
    }
    override fun getItem(position: Int): Any {
        return Detail[position]
    }

    override fun getItemId(position: Int): Long {
        return  Detail[position].sku.toLong()
    }

    override fun getCount(): Int {
        return Detail.size

    }


}