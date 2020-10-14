package com.example.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ui.Adapters.File_Adapter
import com.example.ui.Modle.FileDetail
import com.example.ui.Modle.File_list
import kotlinx.android.synthetic.main.activity_list.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class List : AppCompatActivity() {

    internal var seItem: MutableList<FileDetail> = ArrayList<FileDetail>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
//        Summery
//        Summery()
    }

    private fun Summery() {
        val adapter=File_Adapter( seItem,this)
//        println (adapter.count)
        ls.adapter=adapter
//        list_summery.getItemAtPosition(1)
    }

    val Summery: MutableList<File_list>
        get() {
            val seItem=ArrayList<File_list>()
            val inFiles: MutableList<File> = ArrayList()
            val files: Queue<File> = LinkedList()
            files.addAll(File("/sdcard/Download/").listFiles())

            while (!files.isEmpty()) {
                val file = files.remove()
                if (file.isDirectory) {
                    files.addAll(file.listFiles())
                } else if (file.name.endsWith(".csv")) {
                    val item= File_list()
                    item.file_name = Date(file.lastModified()).toString()
                    item.file_time = file.toString()
                    seItem.add(item)
                    inFiles.add(file)
                    println(file.toString()+ Date(file.lastModified()).toString())

                }
            }
            return seItem
        }
}
