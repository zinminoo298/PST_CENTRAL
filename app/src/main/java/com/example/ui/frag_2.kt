package com.example.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
var ipa:String? = ""

class Frag2(context: Context): Fragment(){

    internal lateinit var serverIp:TextView
    internal lateinit var btnDownload:Button
    internal lateinit var db:DataBase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = DataBase(context!!)
        val view:View = inflater.inflate(R.layout.fragment_2,container,false)
        loadIp()
        db.getBu()
        serverIp = view.findViewById(R.id.ip)
        btnDownload = view.findViewById(R.id.btn_download)

        serverIp.setText(ipa)
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
                AsyncTaskRunner(context).execute()
            }

//            db.getBu()
//            view.storename.setText(storeName)
//            view.storecode.setText(storeCode)
//            view.stocktakeid.setText(stockTakeID)
        }
        return view

    }


    private class AsyncTaskRunner(val context: Context?) : AsyncTask<String, String, String>() {
        internal lateinit var pgd: ProgressDialog

        override fun doInBackground(vararg params: String?): String {


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
            return "gg"
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


        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Downloading")
            pgd.setTitle("Downloading Master")

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
//            Toast.makeText(context,"^_^ Download Completed", Toast.LENGTH_LONG).show()
            super.onPostExecute(result)
        }

    }


        private fun loadIp() {
        var prefs = context?.getSharedPreferences("ip", Activity.MODE_PRIVATE)
        ipa = prefs?.getString("valIp", "")
    }

}