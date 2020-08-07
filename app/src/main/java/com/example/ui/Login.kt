package com.example.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ui.DataBasrHandler.DataBase
import com.example.ui.DataBasrHandler.ck_user
import com.example.ui.DataBasrHandler.psw
import com.example.ui.DataBasrHandler.usr
import com.example.ui.Modle.File_list
import kotlinx.android.synthetic.main.activity_login.*
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.and


val seItem=ArrayList<File_list>()
class Login : AppCompatActivity() {
    internal lateinit var db:DataBase
    var STORAGE_PERMISSION_CODE = 1

    var username = "admin"
    var password = "12345678"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DataBase(this)
        db.openDatabase()
//        Details.clear()
//        db.getFileDetail
//        fileCount()

        /* Enable Bluetooth */
//        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        if (!mBluetoothAdapter.isEnabled) {
//            mBluetoothAdapter.enable()
//        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Storage  Permission is Granted",
//                Toast.LENGTH_SHORT).show();
//            Summery
        } else {
            requestStoragePermission();
        }

        getMACAddress("wlan0")

        Login.setOnClickListener{

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
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this,"Wrong password or username",Toast.LENGTH_SHORT).show()
                    }
                }
               else{
                    if(edt_user.text.toString() == username && edt_password.text.toString() == password){
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this,"Admin username or password is wrong",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    val Summery: MutableList<File_list>
        get() {
            val file1 = File("/sdcard/Download/result.csv")
//            val seItem=ArrayList<File_list>()
            val inFiles: MutableList<File> = ArrayList()
            val files: Queue<File> = LinkedList()
            files.addAll(File("/sdcard/Download/").listFiles())


            while (!files.isEmpty()) {
                val file = files.remove()
                if (file.isDirectory) {
                    files.addAll(file.listFiles())
                } else if (file.name.endsWith(".csv")) {


                    val fs: FileInputStream = FileInputStream(file)
                    val br: BufferedReader = BufferedReader(InputStreamReader(fs))
                    var lines = br.readLine()
                    if(lines == null){
                        println("SCAN FAIL")
//                        val filename = file.name.split("/").toTypedArray()
                        val item=File_list()
                        println(file.name)
                        item.file_name = file.name
                        seItem.add(item)
                        println(seItem[0].file_name)

                    }

                    else{
                        val array = lines.split(",").toTypedArray()

                        val time_array = array[7].split(" ").toTypedArray()
                        val item=File_list()
                        println(file.name)
                        item.file_name =file.name
                        item.file_location = array[2]
                        item.file_date = time_array[0]
                         item.file_time = time_array[1]
                        seItem.add(item)
                        println(seItem[0].file_name)
                    }

//
                }
            }
            return seItem
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


}
