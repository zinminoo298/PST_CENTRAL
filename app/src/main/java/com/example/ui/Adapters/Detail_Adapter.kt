package com.example.ui.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.ui.DataBasrHandler.Detail
import com.example.ui.DataBasrHandler.insp
import com.example.ui.R
import kotlinx.android.synthetic.main.rowview_2.view.*

class Detail_Adapter(
//    internal var stitem: MutableList<File_list>,
    val context: Context

) : BaseAdapter() {

    internal val inflater: LayoutInflater

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
        rowview.txt_sku.text = Detail[position].sku
        rowview.txt_qty.text = Detail[position].qty
        rowview.txt_seq.text = seq_Array[position]


        insp = Detail[position].insp




//        println(seItem[position].file_name.toString()+"GG")

        rowview.setOnClickListener {

        }

        return rowview
    }

    override fun getItem(position: Int): Any {
        return Detail[position]
    }

    override fun getItemId(position: Int): Long {
        return Detail[position].qty.toLong()
    }

    override fun getCount(): Int {
        return Detail.size

    }


}