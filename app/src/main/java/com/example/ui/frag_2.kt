package com.example.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.api_test.FileDownloadClient
import com.example.ui.DataBasrHandler.*
import kotlinx.android.synthetic.main.fragment_2.*
import kotlinx.android.synthetic.main.fragment_2.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

var ipa:String? = ""

class Frag2(context: Context): Fragment(){

    internal lateinit var serverIp:TextView
    internal lateinit var btnDownload:Button
    internal lateinit var db:DataBase
    internal lateinit var view:View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = DataBase(context!!)
        view = inflater.inflate(R.layout.fragment_2,container,false)
        loadIp()
        loadDate()
        db.getBu()
        serverIp = view.findViewById(R.id.ip)
        btnDownload = view.findViewById(R.id.btn_download)

        serverIp.setText(ipa)
        view.businessUnit.setText(BU)
        view.storename.setText(storeName)
        view.storecode.setText(storeCode)
        view.stocktakeid.setText(stockTakeID)
        view.items.setText(totalItems.toString())
        btnDownload.setOnClickListener {

            println("IP"+serverIp.text.toString())
            if(serverIp.text.toString() == "" || serverIp.text.toString() == null){
                Toast.makeText(context,"Please enter correct IpAddress",Toast.LENGTH_SHORT).show()
            }
            else{
                Download()
            }
        }
        return view

    }

    fun setDate(){
        val c = Calendar.getInstance()
        val day = c[Calendar.DAY_OF_MONTH]
        val month = c[Calendar.MONTH]
        val year = c[Calendar.YEAR]
        val mth = month +1
        view.date.setText(""+day+"/"+mth+"/"+year)
        saveDate(""+day+"/"+mth+"/"+year)
    }

    fun Download(){
        lateinit var pgd: ProgressDialog
        var builder = Retrofit.Builder().baseUrl("http://$ipa:3000")
        var retrofit = builder.build()

        var fileDownloadClient = retrofit.create(FileDownloadClient::class.java)
        var call = fileDownloadClient.downloadfile()

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>?,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
//                        Toast.makeText(context,"Download Started!", Toast.LENGTH_SHORT).show()
                    object : AsyncTask<Void?, Void?, Void?>() {
                        override fun doInBackground(vararg params: Void?): Void? {
                            writeResponseBodyToDisk(response.body())

                            return null
                        }

                        override fun onPreExecute() {
                            pgd = ProgressDialog(context)
                            pgd.setMessage("Downloading")
                            pgd.setTitle("Downloading Master")
                            pgd.show()
                            pgd.setCancelable(false)

                            super.onPreExecute()
                        }

                        override fun onPostExecute(result: Void?) {
                            pgd.dismiss()
                            db.getTotalItems()
                            db.getBu()
                            view.businessUnit.setText(BU)
                            view.storename.setText(storeName)
                            view.storecode.setText(storeCode)
                            view.stocktakeid.setText(stockTakeID)
                            view.items.setText(totalItems.toString())
                            setDate()
                            loadDate()
                            if(checkdata == 1) {
                                Frag1.updateButton!!.onUpdate(true)
                            }
                            else{
                                Frag1.updateButton!!.onUpdate(false)

                            }

                            super.onPostExecute(result)
                        }

                    }.execute()

                    println("YEAH !!")
                } else {
                    Toast.makeText(context,"Server return error!", Toast.LENGTH_SHORT).show()
                    println("ERROR")
                }
            }

            override fun onFailure(
                call: Call<ResponseBody?>?,
                t: Throwable?
            ) {
                if (t is IOException) {
                    Toast.makeText(context, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary
                }
                else {
                    Toast.makeText(context, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                }
            }
        })
    }
    private fun writeResponseBodyToDisk(body: ResponseBody?): Boolean {
        return try {
            // todo change the file location/name according to your needs
            val futureStudioIconFile = File("/data/data/com.example.ui/databases/database.db")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body?.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body?.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read = inputStream?.read(fileReader)
                    println("DOING")
                    if (read == -1) {
                        break
                    }
                    outputStream?.write(fileReader, 0, read!!)
                    fileSizeDownloaded += read!!.toLong()
                }
                outputStream?.flush()
                println("COMPLETE")
                checkdata = 1
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }

    private fun loadIp() {
        var prefs = context?.getSharedPreferences("ip", Activity.MODE_PRIVATE)
        ipa = prefs?.getString("valIp", "")
    }
    private fun saveDate(v: String) {
        var editor = context!!.getSharedPreferences("date", AppCompatActivity.MODE_PRIVATE).edit()
        editor.putString("valDate", v)
        editor.apply()
    }

    private fun loadDate() {
        var prefs = context!!.getSharedPreferences("date", Activity.MODE_PRIVATE)
        view.date.setText(prefs.getString("valDate", "").toString())
    }

}