package com.example.pst

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.pst.Adapters.Detail_Adapter
import com.example.pst.Adapters.File_Adapter
import com.example.pst.DataBaseHandler.*
import com.example.pst.Modle.Detail
import kotlinx.android.synthetic.main.activity_view_stock.*

class ViewStock : AppCompatActivity() {

    internal lateinit var db:DataBase
    internal var seItem: MutableList<Detail> = ArrayList<Detail>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stock)

        ck_activity = "0"
        db = DataBase(this)
        db.getDetail

        val listview = findViewById<ListView>(R.id.lv1)
        val adapter= Detail_Adapter( seItem,this)
        listview.adapter=adapter
        adapter.refresh(db.getDetail)
        txt_locaion.setText(location)

        addmore.setOnClickListener {
            val firstChar = filename.first().toString()
            println(firstChar)
            if(firstChar == "S"){
                val intent = Intent(this,Check_stock::class.java)
                startActivity(intent)
            }
           else{
                val intent = Intent(this,Check_stock_Multiscan::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        Detail.clear()
        Details.clear()
        Frag1.adapter = File_Adapter(Frag1.stItem, this)
        Frag1.adapter.refresh(db.getFileDetail)
        val a=Intent(this, MainActivity::class.java)
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(a)
        super.onBackPressed()
    }
}
