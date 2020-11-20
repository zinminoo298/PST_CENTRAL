
package com.example.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
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
import com.example.ui.DataBasrHandler.DataBase
import com.example.ui.DataBasrHandler.DataBase.Companion.stocktakeID
import com.example.ui.DataBasrHandler.fileSaved
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.*
import java.io.File
import java.io.IOException

var fileList = ArrayList<String>()
var jsonStock = ""

class Frag3(context: Context?): Fragment(){


    internal lateinit var serverIp: TextView
    internal lateinit var btnUpload: Button
    internal lateinit var txtSavedFiles:TextView
    internal lateinit var db:DataBase
    lateinit var pgd: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View = inflater.inflate(R.layout.fragment_3, container, false)
        db = DataBase(context!!)
        pgd = ProgressDialog(context)
        serverIp = view.findViewById(R.id.ip)
        btnUpload = view.findViewById(R.id.btn_upload)
        txtSavedFiles = view.findViewById(R.id.saved_files)
        loadIp()
        serverIp.setText(ipa)
        txtSavedFiles.setText(fileSaved.toString())

        btnUpload.setOnClickListener {
            checkID()
//            if(jsonStock == "true"){
//                AsyncTaskRunner(context,pgd).execute()
//            }
//            else{
//
//            }
//            db.checkStockTakeID()
//            AsyncTaskRunner(context, pgd).execute()
        }

        return view
    }


    private fun checkID(){

        db.checkStockTakeID()

        if(DataBase.stocktakeID == "false"){
            Toast.makeText(context, "StockTakeID does not match", Toast.LENGTH_SHORT).show()
        }
        else{
            var builder = Retrofit.Builder().baseUrl("http://$ipa:3000")
            var retrofit = builder.build()
            var fileDownloadClient = retrofit.create(FileDownloadClient::class.java)
            var call:Call<ResponseBody> = fileDownloadClient.checkID("master", "$stocktakeID")

            call?.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        val jsonObject = JSONObject(response.body()!!.string())
//                        println(jsonObject)
//                        Toast.makeText(context, "$jsonObject", Toast.LENGTH_SHORT).show()
                        if (jsonObject.toString() == "{\"exist\":true}") {
                            jsonStock = "true"
                            AsyncTaskRunner(context,pgd).execute()
//                            Toast.makeText(context, "StockTakeID match", Toast.LENGTH_SHORT).show()

                        } else {
                            jsonStock = "false"
                            Toast.makeText(
                                context,
                                "StockTakeID does not match",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
//
                    } else {
                        Toast.makeText(context, "Server error!", Toast.LENGTH_SHORT)
                            .show()
                        println("ERROR")
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable?
                ) {
                    if (t is IOException) {
                        Toast.makeText(
                            context,
                            "this is an actual network failure :( inform the user and possibly retry",
                            Toast.LENGTH_SHORT
                        ).show();
                        // logging probably not necessary
                    } else {
                        Toast.makeText(
                            context,
                            "conversion issue! big problems :(",
                            Toast.LENGTH_SHORT
                        ).show();
                        // todo log to some central bug tracking service
                    }
                }
            })
        }
    }

    private class AsyncTaskRunner(val context: Context?, val pgd: ProgressDialog) : AsyncTask<String, String, String>() {
        lateinit var db:DataBase

        override fun doInBackground(vararg params: String?): String {
            db = DataBase(context!!)
            db.totalfileCheck()
            for(i in 0..fileList.size-1){
                var builder = Retrofit.Builder().baseUrl("http://$ipa:3000")
                var retrofit = builder.build()
                var fileDownloadClient = retrofit.create(FileDownloadClient::class.java)
                val uri = Uri.fromFile(File("/sdcard/Stock Export/${fileList[i]}.csv"))
//                println("File Name" + uri)
                val originalFile = File(uri.toString())
//                println("OrFileName" + originalFile)

                var filepart = RequestBody.create(
                    MediaType.parse("csv/*"),
                    File("/sdcard/Stock Export/${fileList[i]}.csv")
                )

                var file = MultipartBody.Part.createFormData("file", originalFile.name, filepart)

                val descriptionString = "product"
                val description = RequestBody.create(
                    MultipartBody.FORM, descriptionString
                )

                var call: Call<ResponseBody?>? = fileDownloadClient.upload(file, description)

                call?.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>?,
                        response: Response<ResponseBody?>
                    ) {
                        if (response.isSuccessful) {
                            val jsonObject = JSONObject(response.body()!!.string())
                            println(jsonObject)
//                            Toast.makeText(context, "Upload Success!", Toast.LENGTH_SHORT).show()
                            pgd.dismiss()
                        } else {
//                            Toast.makeText(context, "Server return error!", Toast.LENGTH_SHORT)
//                                .show()
//                            println("ERROR")
                        }
                    }

                    override fun onFailure(
                        call: Call<ResponseBody?>?,
                        t: Throwable?
                    ) {
                        if (t is IOException) {

                            Toast.makeText(
                                context,
                                "this is an actual network failure :( inform the user and possibly retry",
                                Toast.LENGTH_SHORT
                            ).show()
                            pgd.dismiss()
                            // logging probably not necessary
                        } else {
                            Toast.makeText(
                                context,
                                "conversion issue! big problems :(",
                                Toast.LENGTH_SHORT
                            ).show();
                            // todo log to some central bug tracking service
                        }
                    }
                })
            }
//            pgd.dismiss()
            fileList.clear()

            return "gg"
        }

        override fun onPreExecute() {
            pgd.setMessage("Uploading")
            pgd.setTitle("Uploading Master")

//            pgd.setButton(
//                DialogInterface.BUTTON_NEGATIVE,
//                "Cancel",
//                DialogInterface.OnClickListener { dialog, which ->
//                    dialog.dismiss()
//                })
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
//            Toast.makeText(context,"^_^ Upload Completed", Toast.LENGTH_LONG).show()
            super.onPostExecute(result)
        }

    }
    private fun loadIp() {
        var prefs = context?.getSharedPreferences("ip", Activity.MODE_PRIVATE)
        ipa = prefs?.getString("valIp", "")
    }
}