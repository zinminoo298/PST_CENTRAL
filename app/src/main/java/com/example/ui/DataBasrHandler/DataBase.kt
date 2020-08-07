package com.example.ui.DataBasrHandler

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.Toast
import com.example.ui.Modle.Detail
import com.example.ui.Modle.FileDetail
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import kotlin.math.cos

/*View stock Data from db*/
var pdName:String? = ""
var stock:String? = ""
var packSz:String? = ""
var status:String? = ""
var cost:String? = ""
var check:Int = 0
var sku_qty :String = ""
var lc_qty :String = ""
var lc_value:String =""

/* View new Item Detail */
var newitem:String? = ""

/* add stock count data to db */
var sku:String? = ""
var qty :Int = 0
var location :String = ""
var filename:String = ""
var insp:String=""
var desc:String= ""

/* add file details to db */
var date:String = ""
var name:String = ""
var time:String = ""

/* check item in db */
var ck_item:Int = 0

/*check user_login*/
var ck_user = 0
var usr = ""
var psw = ""

val Details =  ArrayList<FileDetail>()
val Detail = ArrayList<Detail>()
val Location = ArrayList<String>()


class DataBase(val context: Context){

    companion object{
        private val REAL_DATABASE="database.db"
        private val DATABASE_2 = "summery.db"

    }


    /*Open database and start copying database from assets folder*/
    fun openDatabase(): SQLiteDatabase {
        val dbFile=context.getDatabasePath(DATABASE_2)
        if (!dbFile.exists()) {
            try {
                val checkDB=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)

                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
                Toast.makeText(context,"OKOKOK",Toast.LENGTH_SHORT).show()
            }

        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    /*Copy database from assets folder to package*/
    @SuppressLint("WrongConstant")
    private fun copyDatabase(dbFile: File) {
        val `is`=context.assets.open(DATABASE_2)
        val os= FileOutputStream(dbFile)

        val buffer=ByteArray(1024)
        while(`is`.read(buffer)>0) {
            os.write(buffer)
            Log.d("#DB", "writing>>")
        }

        os.flush()
        os.close()
        `is`.close()
        Log.d("#DB", "completed..")
    }

    fun addDate(){
        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)

//        val query = "SELECT * FROM summery WHERE barcode='$sku'"
//        val cur = db.rawQuery(query,null)

        val values=ContentValues()
        values.put("name", name)
        values.put("date", date)
        values.put("time", time)
        values.put("location", location)
        values.put("count",0)
        val id=db.insertWithOnConflict("date", null, values, SQLiteDatabase.CONFLICT_IGNORE)
//        if (id == -1L) {
//            val selectQuery=
//                "SELECT qty FROM summery WHERE barcode=? AND summery.location='$location'"
//            val cursor=db.rawQuery(selectQuery, arrayOf(sku.toString()))
//            cursor.moveToFirst()
//            var quantity=cursor.getInt(cursor.getColumnIndex("qty"))
//            val valu=ContentValues()
//            valu.put("qty", quantity + qty)
//
//            db.update("summery",valu,"barcode=? AND location=?", arrayOf(sku.toString(),
//                location))
//        }
        db.close()
    }

    fun checkItem(){

        val db0=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        val query0 = "SELECT * FROM summery WHERE barcode='$sku'"
        val cur0 = db0.rawQuery(query0,null)

        if(cur0.moveToFirst()){
            ck_item = 1
        }
        else{
            try{
                val db=context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
                val query = "SELECT * FROM masters WHERE BarcodeIBC='$sku'"
                val cur = db.rawQuery(query,null)

                if(cur.moveToFirst()){
                    ck_item = 1
                }
                else{
                    ck_item = 0
                }
            }
            catch(e:Exception){
                Toast.makeText(context,"Database Not Found",Toast.LENGTH_SHORT).show()
            }

        }

    }

//    fun addNewItem(){
//
//    }

    fun addItem() {

        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)

        val query = "SELECT * FROM summery WHERE barcode='$sku'"
        val cur = db.rawQuery(query,null)

        val values=ContentValues()
        values.put("barcode", sku)
        values.put("qty", qty)
        values.put("location", location)
        values.put("filename", filename)
        values.put("inspector", insp)
        values.put("description", desc)
        val id=db.insertWithOnConflict("summery", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        if (id == -1L) {
            val selectQuery=
                "SELECT qty FROM summery WHERE barcode=? AND summery.location='$location' AND summery.filename='$filename'"
            val cursor=db.rawQuery(selectQuery, arrayOf(sku.toString()))
            cursor.moveToFirst()
            var quantity=cursor.getInt(cursor.getColumnIndex("qty"))
            val valu=ContentValues()
            valu.put("qty", quantity + qty)

            db.update("summery",valu,"barcode=? AND location=? AND filename=?", arrayOf(sku.toString(),
                location, filename))
        }
        else{
            db.insertWithOnConflict("summery", null, values, SQLiteDatabase.CONFLICT_IGNORE)
            val query = "SELECT count FROM date WHERE name='$filename' AND location='$location'"
            val cursor = db.rawQuery(query,null)
            cursor.moveToFirst()
            var quantity = cursor.getInt(cursor.getColumnIndex("count"))
            val valu = ContentValues()
            valu.put("count",quantity+1)
            db.update("date",valu,"location=? AND name=?", arrayOf(location, filename))
            println("ADD COMPLETED")
        }
        db.close()
    }

//    fun viewNewData(){
//        val selectQuery4 = "SELECT * FROM summery WHERE barcode = '$sku'"
//        val cursor4 = db1.rawQuery(selectQuery4, null)
//        if(cursor4.moveToFirst()){
//            newitem = cursor4.getString(cursor4.getColumnIndex("description"))
//        }
//        else{
//            println("NO DATA")
//        }
//    }


    fun viewData() {

        try{
            val db = context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
            val selectQuery =
                "SELECT * FROM masters WHERE BarcodeIBC = '$sku'"
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {

                do {
                    pdName = cursor.getString(cursor.getColumnIndex("ProductName"))
                    stock = cursor.getString(cursor.getColumnIndex("Stock"))
                    packSz = cursor.getString(cursor.getColumnIndex("PackSize"))
                    status = cursor.getString(cursor.getColumnIndex("Status"))
                    cost = cursor.getString(cursor.getColumnIndex("Cost"))
                    Log.e(ContentValues.TAG, DatabaseUtils.dumpCurrentRowToString(cursor))
                    check = 1
                } while (cursor.moveToNext())
            }
            else{
                check = 0
//            Toast.makeText(context,"Please Download Master",Toast.LENGTH_SHORT).show()
            }
        }
        catch(e:Exception){
           Toast.makeText(context,"Please Download Master",Toast.LENGTH_SHORT).show()
        }

        val db0=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        val query = "SELECT * FROM summery WHERE barcode='$sku'AND filename='$filename' AND location='$location'"
        val cur = db0.rawQuery(query,null)
        val values=ContentValues()
        values.put("cost", cost)
        db0.update("summery",values,"barcode=? AND location=? AND filename=?", arrayOf(sku.toString(),
            location, filename))

        val db1 = context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        val selectQuery1 =
            "SELECT * FROM summery WHERE barcode = '$sku' AND location = '$location' AND filename='$filename'"
        val cursor1 = db1.rawQuery(selectQuery1, null)
        if (cursor1.moveToFirst()) {
            sku_qty = cursor1.getString(cursor1.getColumnIndex("qty"))
        }
        else{
//            Toast.makeText(context,"Please Download Master",Toast.LENGTH_SHORT).show()
        }

        val selectQuery2 =
            "SELECT SUM(qty) AS qty FROM summery WHERE location='$location' AND filename='$filename'"
        val cursor2 = db1.rawQuery(selectQuery2, null)
        if (cursor2.moveToFirst()) {
            lc_qty = cursor2.getString(cursor2.getColumnIndex("qty"))
        }
        else{
//            Toast.makeText(context,"Please Download Master",Toast.LENGTH_SHORT).show()
        }

        val selectQuery3 = "SELECT SUM(cost * qty) as val FROM summery"
        val cursor3 = db1.rawQuery(selectQuery3, null)
        if (cursor3.moveToFirst()) {
            lc_value = cursor3.getString(cursor3.getColumnIndex("val"))
        }
        else{
//            Toast.makeText(context,"Please Download Master",Toast.LENGTH_SHORT).show()
        }

        val selectQuery4 = "SELECT * FROM summery WHERE barcode = '$sku'"
        val cursor4 = db1.rawQuery(selectQuery4, null)
        if(cursor4.moveToFirst()){
            newitem = cursor4.getString(cursor4.getColumnIndex("description"))
        }
        else{
            println("NO DATA")
        }
    }

    val getFileDetail:MutableList<FileDetail> get(){

        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        val query = "SELECT * FROM date"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            do{
                val item = FileDetail()
                item.file_name = cursor.getString(0)
                item.file_lc = cursor.getString(1)
                item.file_date = cursor.getString(2)
                item.file_time = cursor.getString(3)
                item.file_summery = cursor.getString(4)
                Details.add(item)
            }while(cursor.moveToNext())
        }

        return Details
    }

    val getDetail:MutableList<Detail> get(){

        Detail.clear()
        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        val query = "SELECT * FROM summery WHERE location='$location' AND filename = '$filename'"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            do{
                val item = Detail()
                item.sku = cursor.getString(2)
                item.qty = cursor.getString(3)
                item.insp = cursor.getString(4)
                Detail.add(item)
                println("1st array"+ Detail[0].sku)
            }while(cursor.moveToNext())
        }

        return Detail
    }

    fun getLocation(){
        Location.clear()
        val db=context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
        val query = "SELECT * FROM  locations"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            do{
                val location = cursor.getString(1)
                Location.add(location)
            }while (cursor.moveToNext())
        }
    }

    fun checkUser() {
        try {
            val db = context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
            val query = "SELECT * FROM  users"
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    ck_user = 1
                    println("YES")
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            ck_user = 0
            println("NOPE  " + e)
        }
    }

    fun getUser(){
        val db = context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
        val query = "SELECT * FROM  users WHERE email='$usr' AND plain_password='$psw'"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                ck_user = 1
                println("YES")
            } while (cursor.moveToNext())
        }
        else{
            ck_user = 0
        }
    }


}