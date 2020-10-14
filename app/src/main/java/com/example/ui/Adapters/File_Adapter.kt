package com.example.ui.Adapters

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ui.*
import com.example.ui.DataBasrHandler.*
import com.example.ui.Modle.Detail
import com.example.ui.Modle.FileDetail
import com.example.ui.Modle.File_list
import kotlinx.android.synthetic.main.rowview_1.view.*
import java.io.File
import java.io.IOException

class File_Adapter(
    internal var stitem: MutableList<FileDetail>,
    val context: Context

) : BaseAdapter() {

    internal val inflater: LayoutInflater
    internal lateinit var db: DataBase


    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowview:View


        rowview = inflater.inflate(R.layout.rowview_1,null)
        rowview.txt_filename.text = stitem[position].file_name
        rowview.txt_filelocation.text = stitem[position].file_lc
        rowview.txt_filedate.text = stitem[position].file_date
        rowview.txt_filetime.text = stitem[position].file_time
        rowview.txt_filesummery.text = "${stitem[position].file_summery} / ${stitem[position].file_qty}"
        if(stitem[position].file_status == "no"){
            rowview.complete.visibility = GONE
            rowview.imcomplete.visibility = VISIBLE
        }
        else{
            rowview.complete.visibility = VISIBLE
            rowview.imcomplete.visibility = GONE
        }

//        println(seItem[position].file_name.toString()+"GG")

        rowview.setOnClickListener {

            updateCheck = stitem[position].file_status
            location = stitem[position].file_lc
            filename = stitem[position].file_name
            doc_name = stitem[position].file_name
            val i= Intent(context, ViewStock::class.java)
            context.startActivity(i)
        }

        rowview.setOnLongClickListener {
            lateinit var dialog: AlertDialog
            db= DataBase(context)
//            com.example.ui.DataBasrHandler.barcode =  seItem[position].sku

            // Initialize a new instance of alert dialog builder object
            val builder= AlertDialog.Builder(context)

            // Set a title for alert dialog
            builder.setTitle("Delete Location : ${stitem[position].file_lc}")

            // Set a message for alert dialog
            builder.setMessage("Are you sure??")


            // On click listener for dialog buttons
            val dialogClickListener= DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {

                        try {
                            val root= File("/sdcard/Stock Export/${stitem[position].file_name}.csv")
                            if (root.exists()) {
                                println("DELETED")
                                root.delete()
                                Toast.makeText(context,"File Deleted",Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        db.deleteFileRow(stitem[position].file_name)
//                        db.deleteRow(seItem[position].seq)
                        Details.clear( )
                        dialog.dismiss()
                        refresh(db.getFileDetail)
                        db.checkSaveFiles()
//                        txt1.setText("01")
                        txt1.setText(fileCount.toString())
                        txt2.setText(fileSaved.toString())
                    }
                    DialogInterface.BUTTON_NEUTRAL -> {
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

            return@setOnLongClickListener true
        }

        return rowview
    }

    /*Refresh data after deleting data*/
    fun refresh(newList: MutableList<FileDetail>) {
        stitem.clear()
        stitem.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any {
        return stitem[position]
    }

    override fun getItemId(position: Int): Long {
        return stitem[position].file_summery.toLong()
    }

    override fun getCount(): Int {
        return stitem.size

    }


}