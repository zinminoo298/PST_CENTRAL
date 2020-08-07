package com.example.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.ui.Adapters.Detail_Adapter
import com.example.ui.Adapters.File_Adapter
import com.example.ui.DataBasrHandler.DataBase
import com.example.ui.DataBasrHandler.location
import kotlinx.android.synthetic.main.activity_view_stock.*

class ViewStock : AppCompatActivity() {

    internal lateinit var db:DataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stock)

        ck_activity = "0"
        db = DataBase(this)
        db.getDetail

        val listview = findViewById<ListView>(R.id.lv1)
        val adapter= Detail_Adapter( this)
        listview.adapter=adapter
        txt_locaion.setText(location)

        addmore.setOnClickListener {
            val intent = Intent(this,Check_stock::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val a=Intent(this, MainActivity::class.java)
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(a)
        super.onBackPressed()
    }
}
