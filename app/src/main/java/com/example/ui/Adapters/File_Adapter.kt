package com.example.ui.Adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.ui.*
import com.example.ui.DataBasrHandler.Details
import com.example.ui.DataBasrHandler.filename
import com.example.ui.DataBasrHandler.location
import com.example.ui.Modle.File_list
import kotlinx.android.synthetic.main.rowview_1.view.*

class File_Adapter(
//    internal var stitem: MutableList<File_list>,
     val context: Context

) : BaseAdapter() {

    internal val inflater: LayoutInflater

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowview:View

        rowview = inflater.inflate(R.layout.rowview_1,null)
        rowview.txt_filename.text = Details[position].file_name
        rowview.txt_filelocation.text = Details[position].file_lc
        rowview.txt_filedate.text = Details[position].file_date
        rowview.txt_filetime.text = Details[position].file_time
        rowview.txt_filesummery.text = Details[position].file_summery

//        println(seItem[position].file_name.toString()+"GG")

        rowview.setOnClickListener {

            println("PRESS OK")
            location = Details[position].file_lc
            filename = Details[position].file_name
            doc_name = Details[position].file_name
            val i= Intent(context, ViewStock::class.java)
            context.startActivity(i)
        }

        return rowview
    }

    override fun getItem(position: Int): Any {
        return Details[position]
    }

    override fun getItemId(position: Int): Long {
        return Details[position].file_summery.toLong()
    }

    override fun getCount(): Int {
        return Details.size

    }


}