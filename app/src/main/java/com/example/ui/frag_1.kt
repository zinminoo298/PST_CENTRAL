package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ui.Adapters.File_Adapter
import com.example.ui.DataBasrHandler.DataBase
import com.example.ui.DataBasrHandler.fileCount
import com.example.ui.DataBasrHandler.fileSaved
import com.example.ui.Modle.FileDetail
import com.example.ui.Modle.File_list
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_1.*
import kotlinx.android.synthetic.main.fragment_1.view.*
import org.w3c.dom.Text

var scanCheck = 0
internal lateinit var txt1:TextView
internal lateinit var txt2:TextView

class Frag1: Fragment(){

    private lateinit var fab_main: FloatingActionButton
    private lateinit var fab1_single: FloatingActionButton
    private lateinit var fab2_multi:FloatingActionButton
    private lateinit var fab_open: Animation
    private lateinit var fab_close: Animation
    private lateinit var fab_clock: Animation
    private lateinit var fab_anticlock: Animation
    private lateinit var textview_mail: TextView
    private lateinit var textview_share: TextView
    var isOpen = false

    private var mcontext: Context? = null
    internal var stItem: MutableList<FileDetail> = ArrayList<FileDetail>()
    internal lateinit var db:DataBase

    override fun onAttach(activity: Activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity)
        mcontext = activity
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View = inflater.inflate(R.layout.fragment_1,container,false)
        val listview = view.findViewById<ListView>(R.id.lv)
        val adapter=File_Adapter(stItem, mcontext!!)
        val db = DataBase(mcontext!!)
//        db.getFileDetail
        listview.adapter=adapter
        adapter.refresh(db.getFileDetail)
        txt1 = view.findViewById(R.id.total_lc)
        txt2 = view.findViewById(R.id.totalsaved)

        view.total_lc.setText(fileCount.toString())
        view.totalsaved.setText(fileSaved.toString())

        fab_main = view.findViewById(R.id.fab);
        fab1_single = view.findViewById(R.id.fab1);
        fab2_multi = view.findViewById(R.id.fab2);
        fab_close = AnimationUtils.loadAnimation(mcontext!!.applicationContext,R.anim.fab_close)
        fab_open = AnimationUtils.loadAnimation(mcontext!!.applicationContext, R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(mcontext!!.applicationContext, R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(mcontext!!.applicationContext, R.anim.fab_rotate_anticlock);
        textview_mail = view.findViewById(R.id.textview_mail);
        textview_share = view.findViewById(R.id.textview_share);

        fab_main.setOnClickListener {

            if (isOpen) {

                textview_mail.setVisibility(View.INVISIBLE);
                textview_share.setVisibility(View.INVISIBLE);
                fab2_multi.startAnimation(fab_close);
                fab1_single.startAnimation(fab_close);
                fab_main.startAnimation(fab_anticlock);
                fab2_multi.setClickable(false);
                fab1_single.setClickable(false);
                isOpen = false;
            } else {
                textview_mail.setVisibility(View.VISIBLE);
                textview_share.setVisibility(View.VISIBLE);
                fab2_multi.startAnimation(fab_open);
                fab1_single.startAnimation(fab_open);
                fab_main.startAnimation(fab_clock);
                fab2_multi.setClickable(true);
                fab1_single.setClickable(true);
                isOpen = true;
            }
        }

        fab2_multi.setOnClickListener {
            scanCheck = 1
            val intent = Intent(mcontext,UserRecord::class.java)
            startActivity(intent)
//            Toast.makeText(context,"Under Development",Toast.LENGTH_SHORT).show()
        }

        fab1_single.setOnClickListener {
            scanCheck = 0
            val intent = Intent(mcontext,UserRecord::class.java)
            startActivity(intent)
        }

        return view
    }



}

