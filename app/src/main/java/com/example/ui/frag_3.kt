package com.example.ui

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

var fileList = ArrayList<String>()

class Frag3: Fragment(){


    internal lateinit var serverIp: TextView
    internal lateinit var btnUpload: Button
    internal lateinit var txtSavedFiles:TextView
    fun Fragment(){

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View = inflater.inflate(R.layout.fragment_3,container,false)
        serverIp = view.findViewById(R.id.ip)
        btnUpload = view.findViewById(R.id.btn_upload)
        txtSavedFiles = view.findViewById(R.id.saved_files)
        loadIp()
        serverIp.setText(ip)
        fileCount()

//        println(fileList.size)
//        println(fileList[0])

        btnUpload.setOnClickListener {
            val file = File("/sdcard/Download/test_test.csv")
            AsyncTaskRunner(file,context).execute()
        }

        return view
    }

    fun fileCount() {
        val files: Queue<File> = LinkedList()
        files.addAll(File("/sdcard/Stock Export/").listFiles())


        while (!files.isEmpty()) {
            val file = files.remove()
            if (file.isDirectory) {
                files.addAll(file.listFiles())
            } else if (file.name.endsWith(".csv")) {


                val fs: FileInputStream = FileInputStream(file)
                val br: BufferedReader = BufferedReader(InputStreamReader(fs))
                var lines = br.readLine()
                if (lines == null) {
                    println("SCAN FAIL")
                    println(file.name+"NO")

                }
                else {
                    fileList.add(file.name)
                    println(file.name+"YES")

                }
            }
        }
    }

    private class AsyncTaskRunner(val fileuri: File,val context: Context?) : AsyncTask<String, String, String>() {
        internal lateinit var pgd: ProgressDialog

        override fun doInBackground(vararg params: String?): String {

            for(i in 0..fileList.size-1){
                var builder = Retrofit.Builder().baseUrl("http://$ip:3000")
                var retrofit = builder.build()
                var fileDownloadClient = retrofit.create(FileDownloadClient::class.java)
                val uri = Uri.fromFile(File("/sdcard/Stock Export/${fileList[i]}"))
                println("File Name"+uri)
                val originalFile = File(uri.toString())
                println("OrFileName"+originalFile)

                var filepart = RequestBody.create(
                    MediaType.parse("csv/*"),
                    File("/sdcard/Stock Export/${fileList[i]}"))

                var file = MultipartBody.Part.createFormData("file",originalFile.name,filepart )

                val descriptionString = "product"
                val description = RequestBody.create(
                    MultipartBody.FORM, descriptionString
                )


                var call: Call<ResponseBody?>? = fileDownloadClient.upload(file,description)
                call?.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>?,
                        response: Response<ResponseBody?>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(context,"Upload Started!", Toast.LENGTH_SHORT).show()
//                    val writtenToDisk: Boolean = writeResponseBodyToDisk(response.body())
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


            return "gg"
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Uploading")
            pgd.setTitle("Uploading Master")

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
//            Toast.makeText(context,"^_^ Upload Completed", Toast.LENGTH_LONG).show()
            super.onPostExecute(result)
        }

    }

    private fun loadIp() {
        var prefs = context?.getSharedPreferences("ip", Activity.MODE_PRIVATE)
        ip = prefs?.getString("valIp", "")
    }
}