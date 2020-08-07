package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.net.NetworkInterface
import java.util.*


class Setting : AppCompatActivity() {

    lateinit var masterIp: EditText
    lateinit var wifiMac: EditText
    lateinit var btnSave: Button
    private var mac =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        loadIp()
        loadMac()
        getMac(this)
        getMacAddr()

        masterIp = findViewById(R.id.master_ip)
        wifiMac = findViewById(R.id.wifi_mac)
        btnSave = findViewById(R.id.btn_save)

        masterIp.setText(ip)
        wifiMac.setText(mac)

        btnSave.setOnClickListener {
            setIp(masterIp.text.toString())
            setMac(wifiMac.text.toString())
            loadIp()

            val a=Intent(this, MainActivity::class.java)
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(a)
        }

    }
    fun getMac( context: Context): String {
        val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        println(info.macAddress.toUpperCase())
        return info.macAddress.toUpperCase()
    }

    fun getWifiMac(){
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wInfo = wifiManager.connectionInfo
        val macAddress = wInfo.macAddress
    }

    fun getMacAddr(): String {
        try {
            val all = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (nif in all) {
                if (!nif.getName().equals("wlan0", ignoreCase=true)) continue

                val macBytes = nif.getHardwareAddress() ?: return ""

                val res1 = StringBuilder()
                for (b in macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b))
                }

                if (res1.length > 0) {
                    res1.deleteCharAt(res1.length - 1)
                }
                println(res1.toString())
                return res1.toString()
            }
        } catch (ex: Exception) {
        }

        return "02:00:00:00:00:00"
    }

    private fun setIp(v: String) {
        var editor = getSharedPreferences("ip", MODE_PRIVATE).edit()
        editor.putString("valIp", v)
        editor.apply()
    }

    private fun loadIp() {
        var prefs = getSharedPreferences("ip", Activity.MODE_PRIVATE)
        ip = prefs.getString("valIp", "").toString()
    }

    private fun setMac(v: String) {
        var editor = getSharedPreferences("mac", MODE_PRIVATE).edit()
        editor.putString("valMac", v)
        editor.apply()
    }

    private fun loadMac() {
        var prefs = getSharedPreferences("mac", Activity.MODE_PRIVATE)
         mac = prefs.getString("valMac", "").toString()
    }
    override fun onBackPressed() {
//        startActivity(Intent(applicationContext, MainActivity::class.java))
        val a=Intent(this, MainActivity::class.java)
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(a)
        super.onBackPressed()
    }
}
