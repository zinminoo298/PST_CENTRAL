package com.example.pst

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pst.Adapters.File_Adapter
import com.example.pst.DataBaseHandler.*
import com.example.pst.Modle.File_list
import kotlinx.android.synthetic.main.activity_login.*
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.ArrayList

var wifiMacAdds = ""
var user = ""

val seItem=ArrayList<File_list>()
class Login : AppCompatActivity() {
    internal lateinit var db:DataBase
    var STORAGE_PERMISSION_CODE = 1

    var username = "admin"
    var password = "12345678"
    lateinit var pgd: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DataBase(this)
        db.openDatabase()
        edt_user.requestFocus()
        pgd = ProgressDialog(this)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestStoragePermission();
        }

        getMACAddress("wlan0")

        edt_user.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->

            if (event.keyCode == KeyEvent.KEYCODE_SPACE && event.action == KeyEvent.ACTION_UP || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                if(edt_user.text.toString()==""){
                    Toast.makeText(this,"Please enter username",Toast.LENGTH_SHORT).show()
                }
                else{
                    edt_password.requestFocus()
                }
            }

            false

        })

        edt_password.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->

            if (event.keyCode == KeyEvent.KEYCODE_SPACE && event.action == KeyEvent.ACTION_UP || event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                if(edt_user.text.toString()=="" || edt_password.text.toString() ==""){
                    Toast.makeText(this,"Please enter username and password",Toast.LENGTH_SHORT).show()
                }
                else{
                    loginProcess()
                }
            }

            false

        })

        Login.setOnClickListener{

            loginProcess()
        }


    }

    fun loginProcess(){
        db.checkUser()

        if(edt_user.text.toString() == "" || edt_password.text.toString() == ""){
            Toast.makeText(this,"Please enter username and password",Toast.LENGTH_SHORT).show()
        }
        else{

            if(ck_user == 1){
                usr = edt_user.text.toString()
                psw = edt_password.text.toString()
                db.getUser()
                if(ck_user ==1){
                    AsyncTaskRunner(this,pgd,db).execute()

                }
                else{
                    Toast.makeText(this,"Wrong password or username",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                if(edt_user.text.toString() == username && edt_password.text.toString() == password){
                    usr = edt_user.text.toString()
//                    Frag1.adapter = File_Adapter(Frag1.stItem, this)
//                    Frag1.adapter.refresh(db.getFileDetail)
//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
                    AsyncTaskRunner(this,pgd,db).execute()
                }
                else{
                    Toast.makeText(this,"Admin username or password is wrong",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun getMACAddress(interfaceName: String?): String? {
        try {
            val interfaces: ArrayList<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (interfaceName != null) {
                    if (!intf.name.equals(interfaceName, ignoreCase = true)) continue
                }
                val mac = intf.hardwareAddress ?: return ""
                val buf = java.lang.StringBuilder()
                for (idx in mac.indices) buf.append(String.format("%02X:", mac[idx]))
                if (buf.length > 0) buf.deleteCharAt(buf.length - 1)
                println("WIFI MAC : $buf")
                wifiMacAdds = buf.toString().replace(":","")
                return buf.toString()
            }
        } catch (ex: java.lang.Exception) {
            println(ex)
        }
        return ""
    }

    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
            )
        }

        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class AsyncTaskRunner(val context: Context, val pgd: ProgressDialog,val db:DataBase) : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            Details.clear()
            Frag1.adapter = File_Adapter(Frag1.stItem, context)
            Frag1.adapter.refresh(db.getFileDetail)
            return "null"
        }

        override fun onPreExecute() {
            pgd.setMessage("Loading")
            pgd.setTitle("Login")

            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
//            pgd.dismiss()
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            super.onPostExecute(result)
        }

    }

}
