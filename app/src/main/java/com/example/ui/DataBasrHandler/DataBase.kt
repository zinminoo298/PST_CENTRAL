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
var ck = 0
var totalItems = 0
var rowSeq = 0
var barcode:String = ""
var ckItem = 0
var checkdata = 0

/*Get BU*/
var stockTakeID = ""
var BU = ""
var storeName = ""
var storeCode = ""

/*count files*/
var fileCount = 0
var fileSaved = 0

var quantity:Int= 0
var updateCheck = ""
var updateQty = "0"

/*Next Check*/
var nextCheck = 0

/*Check fileSeq*/
var fileSeq = 0

/*Check RowID*/
var rowID = 1

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
var stockID:String=""
var ibc:String=""
var sbc:String=""
var sku_bc:String =""
var seq = 1
var totalQty = 0

/* View new Item Detail */
var newitem:String? = ""

/* add stock count data to db */
var sku:String? = ""
var qty :String = "0"
var price:String = ""
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

var itemDetail:String = ""


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

    fun checkDate(){
        val db = context.openOrCreateDatabase(DATABASE_2,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM date WHERE date='$date'"
        val cursor = db.rawQuery(query,null)

        if(cursor.moveToLast()){
            fileSeq = cursor.getInt(cursor.getColumnIndex("seq"))
            fileSeq++
        }
        else{
            fileSeq = 1
        }
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
        values.put("seq", fileSeq)
        values.put("updated", updateCheck)
        val id=db.insertWithOnConflict("date", null, values, SQLiteDatabase.CONFLICT_IGNORE)
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

    fun checkDB(){
        try{
            val db = context.openOrCreateDatabase("database.db",Context.MODE_PRIVATE,null)
            val query = "SELECT * FROM masters"
            val cursor = db.rawQuery(query,null)
            if(cursor.moveToFirst()){
                checkdata = 1
            }
            else{
                checkdata = 0
            }
        }catch(e:Exception){
                checkdata = 0
        }

    }

    fun checkDatabase(){
        val db = context.openOrCreateDatabase("summery.db",Context.MODE_PRIVATE,null)
        val query = "SELECT ROWID FROM summery"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToLast()){
            rowID = cursor.getInt(cursor.getColumnIndex("ROWID"))+1
        }
        else{
            rowID = 1
        }
        checkItem1()
        checkSeq()
        addItem1()
        summeryValue()
    }

    fun summeryValue(){
        val db1 = context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        val selectQuery1 =
            "SELECT SUM(QNT) as QNT FROM summery WHERE Barcode = '$sku' AND Location = '$location' AND DocNum='$filename'"
        val cursor1 = db1.rawQuery(selectQuery1, null)
        if (cursor1.moveToFirst()) {
            sku_qty = cursor1.getString(cursor1.getColumnIndex("QNT"))
        }
        else{
//            Toast.makeText(context,"Please Download Master",Toast.LENGTH_SHORT).show()
        }

        val selectQuery2 =
            "SELECT SUM(QNT) AS qty FROM summery WHERE Location='$location' AND DocNum='$filename'"
        val cursor2 = db1.rawQuery(selectQuery2, null)
        if (cursor2.moveToFirst()) {
            lc_qty = cursor2.getString(cursor2.getColumnIndex("qty"))
        }
        else{
//            Toast.makeText(context,"Please Download Master",Toast.LENGTH_SHORT).show()
        }

        println("SEQ"+seq)
        val selectQuery3 = "SELECT SUM(SalePrice * QNT) as val FROM summery WHERE  DocNum='$filename'"
        val cursor3 = db1.rawQuery(selectQuery3, null)
        if (cursor3.moveToFirst()) {
            lc_value = cursor3.getString(cursor3.getColumnIndex("val"))
        }

        else{
//            Toast.makeText(context,"Please Download Master",Toast.LENGTH_SHORT).show()
        }
    }

    fun showDetail() {
        val db = context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
        val query = "SELECT * FROM masters WHERE BarcodeSBC='$sku'"
        val cursor = db.rawQuery(query, null)
        val withoutstatus :String
        if (cursor.moveToFirst()) {
            pdName = cursor.getString(cursor.getColumnIndex("ProductName"))
            stock = cursor.getString(cursor.getColumnIndex("Stock"))
            packSz = cursor.getString(cursor.getColumnIndex("PackSize"))
            withoutstatus = cursor.getString(cursor.getColumnIndex("Status"))
            cost = cursor.getString(cursor.getColumnIndex("Cost"))
            stockID = cursor.getString(cursor.getColumnIndex("CountName"))
            sku_bc = cursor.getString(cursor.getColumnIndex("SKU"))
            status = withoutstatus.replace("\\s".toRegex(), "")
            sbc = sku!!
            ibc = "null"
            if (cost == null) {
                cost = "0"
            }
            ck = 1
        }
        else{
            ck = 0
        }
        println("ck $ck")
        db.close()
    }

    fun showTotalQty(){
        val db = context.openOrCreateDatabase("summery.db", Context.MODE_PRIVATE, null)
        val query = "SELECT SUM(QNT) as qty FROM summery WHERE Barcode='$sku'"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
           totalQty = cursor.getInt(0)
        }
        else{
            totalQty = 0
        }
        println("ck $ck")
        db.close()
    }

    fun checkItem1(){
        val db=context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
        val query = "SELECT * FROM masters WHERE BarcodeSBC='$sku'"
        val cursor = db.rawQuery(query,null)
        val withoutstatus :String
        if(cursor.moveToFirst()){
            pdName = cursor.getString(cursor.getColumnIndex("ProductName"))
            stock = cursor.getString(cursor.getColumnIndex("Stock"))
            packSz = cursor.getString(cursor.getColumnIndex("PackSize"))
            withoutstatus = cursor.getString(cursor.getColumnIndex("Status"))
            cost = cursor.getString(cursor.getColumnIndex("Cost"))
            stockID = cursor.getString(cursor.getColumnIndex("CountName"))
            sku_bc = cursor.getString(cursor.getColumnIndex("SKU"))
            sbc = sku!!
            ibc = cursor.getString(cursor.getColumnIndex("BarcodeIBC"))
            status = withoutstatus.replace("\\s".toRegex(), "")
            if(cost == null){
                cost = "0"
            }
            ckItem = 1
//            checkSeq()
//            addItem1()
//            summeryValue()
        }
        else{
            val query = "SELECT * FROM masters WHERE BarcodeIBC='$sku'"
            val cursor = db.rawQuery(query,null)
            if(cursor.moveToFirst()) {
                pdName = cursor.getString(cursor.getColumnIndex("ProductName"))
                stock = cursor.getString(cursor.getColumnIndex("Stock"))
                packSz = cursor.getString(cursor.getColumnIndex("PackSize"))
                status = cursor.getString(cursor.getColumnIndex("Status"))
                cost = cursor.getString(cursor.getColumnIndex("Cost"))
                stockID = cursor.getString(cursor.getColumnIndex("CountName"))
                sku_bc = cursor.getString(cursor.getColumnIndex("SKU"))
                ibc = sku!!
                sbc= cursor.getString(cursor.getColumnIndex("BarcodeSBC"))
                if (cost == null) {
                    cost = "0"
                }
                ckItem = 1
//                checkSeq()
//                addItem1()
//                summeryValue()

            }
            else{
                ckItem = 0
            }
        }
        db.close()
    }

    fun checkSeq(){
        val db = context.openOrCreateDatabase(DATABASE_2,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM summery WHERE DocNum='$filename'"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToLast()){
            seq = cursor.getInt(15)+1
        }
        else{
            seq = 1
        }
    }

    fun addItem1(){
        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)

        val values=ContentValues()
//        values.put("ROWID", rowID)
        values.put("Barcode", sku)
        values.put("QNT", qty)
        values.put("Location", location)
        values.put("DocNum", filename)
        values.put("Inspector", usr)
        values.put("ProductName", pdName)
        values.put("SalePrice", cost)
        values.put("DateTime",date+" "+time)
        values.put("StockTakeID", stockID)
        values.put("Stock", stock)
        values.put("Pack", packSz)
        values.put("Status", status)
        values.put("Seq",seq)
        values.put("SKU", sku_bc)
        values.put("IBC",ibc)
        values.put("SBC", sbc)
//        val id = db.insertWithOnConflict("summery", "null", values,SQLiteDatabase.CONFLICT_IGNORE)
//        if (id == -1L) {
//            val selectQuery=
//                "SELECT QNT FROM summery WHERE Barcode=? AND summery.Location='$location' AND summery.DocNum='$filename'"
//            val cursor=db.rawQuery(selectQuery, arrayOf(sku.toString()))
//            (cursor.moveToFirst())
//            var quantity=cursor.getInt(cursor.getColumnIndex("QNT"))
//            val valu=ContentValues()
//            valu.put("QNT", quantity + qty)
//
//            db.update("summery",valu,"Barcode=? AND Location=? AND DocNum=?", arrayOf(sku.toString(),
//                location, filename))
//        }
//        else{
        db.insertWithOnConflict("summery", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        val query = "SELECT count FROM date WHERE name='$filename' AND location='$location'"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        var quantity = cursor.getInt(cursor.getColumnIndex("count"))
        val valu = ContentValues()
        valu.put("count",quantity+1)
        valu.put("updated", updateCheck)
        db.update("date",valu,"location=? AND name=?", arrayOf(location, filename))
        println("ADD COMPLETED")
//        }
        db.close()
    }

    fun addItem() {

        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)

        val query = "SELECT * FROM summery WHERE barcode='$sku'"
        val cur = db.rawQuery(query,null)

        val values=ContentValues()
        values.put("barcode", sku)
        values.put("qty", qty)
        values.put("location", location)
        values.put("filename", filename)
        values.put("inspector", usr)
        values.put("description", desc)
        val id=db.insertWithOnConflict("summery", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        if (id == -1L) {
            val selectQuery=
                "SELECT qty FROM summery WHERE barcode=? AND summery.location='$location' AND summery.filename='$filename'"
            val cursor=db.rawQuery(selectQuery, arrayOf(sku.toString()))
            cursor.moveToFirst()
            var quantity=cursor.getInt(cursor.getColumnIndex("qty"))
            val valu=ContentValues()
            valu.put("qty", quantity + Integer.parseInt(qty))

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
                item.file_status = cursor.getString(8)
                val query1 = "SELECT SUM(QNT) FROM summery Where DocNum = '${cursor.getString(0)}'"
                val cursor1 = db.rawQuery(query1,null)
                if(cursor1.moveToFirst()){
                    if(cursor1.getString(0) == null){
                        item.file_qty = "0"
                    }
                    else{
                        item.file_qty = cursor1.getString(0)
                    }
                    println("QTY : "+item.file_qty)
                }
                Details.add(item)
            }while(cursor.moveToNext())
        }
        db.close()
        return Details
    }

    val getDetail:MutableList<Detail> get(){

        Detail.clear()
        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        val query = "SELECT * FROM summery WHERE Location='$location' AND DocNum = '$filename'"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            do{
                val item = Detail()
                item.sku = cursor.getString(4)
                item.qty = cursor.getString(7)
                item.insp = cursor.getString(2 )
                item.seq = cursor.getInt(15)
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
        val query = "SELECT * FROM  users WHERE username='$usr' AND plain_password='$psw'"
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

    fun getItem(){
        println("GETITEM")
        val db = context.openOrCreateDatabase(DATABASE_2,Context.MODE_PRIVATE,null)
        val query = "SELECT count FROM date WHERE name='$filename' AND location='$location'"
        val cursor = db.rawQuery(query,null)
        try{
            cursor.moveToFirst()
            quantity = cursor.getInt(cursor.getColumnIndex("count"))
            println("QTY"+quantity)
            if(quantity == 0){
                itemDetail = " , , , , , , , , , , , ,"
            }else{
                var id = ""
                var stockid = ""
                var doc = ""
                var loc = ""
                var bc = ""
                var name = ""
                var prc = ""
                var qty = ""
                var stock = ""
                var pack = ""
                var status = ""
                var lcqty = ""
                var lcvalue = ""
                val db = context.openOrCreateDatabase(DATABASE_2,Context.MODE_PRIVATE,null)
                val query = "SELECT ROWID,* FROM summery WHERE DocNum='$filename'"
                val cursor = db.rawQuery(query,null)
                if(cursor.moveToLast()){
                    id = cursor.getString(16)
                    stockid = cursor.getString(1)
                    doc = cursor.getString(2)
                    loc = cursor.getString(4)
                    bc = cursor.getString(5)
                    name = cursor.getString(6)
                    prc = cursor.getString(7)
                    qty = cursor.getString(8)
                    stock = cursor.getString(13)
                    pack = cursor.getString(14)
                    status = cursor.getString(15)

                }

                val selectQuery2 =
                    "SELECT SUM(QNT) AS qty FROM summery WHERE Location='$loc' AND DocNum='$doc'"
                val cursor2 = db.rawQuery(selectQuery2, null)
                if (cursor2.moveToFirst()) {
                    lcqty = cursor2.getString(cursor2.getColumnIndex("qty"))
                }

                val selectQuery3 = "SELECT SUM(SalePrice * QNT) as val FROM summery WHERE DocNum='$filename'"
                val cursor3 = db.rawQuery(selectQuery3, null)
                if (cursor3.moveToFirst()) {
                    lcvalue = cursor3.getString(cursor3.getColumnIndex("val"))
                }


                itemDetail = "$id,$stockid,$doc,$loc,$bc,$name,$prc,$qty,$stock,$pack,$status,$lcqty,$lcvalue"
            }
        }
        catch(e:Exception){
            println(e)
//            Toast.makeText(context,"NO DATA",Toast.LENGTH_SHORT).show()
        }
    }

    fun getFirstItem(){

        val db = context.openOrCreateDatabase(DATABASE_2,Context.MODE_PRIVATE,null)
        val query = "SELECT count FROM date WHERE name='$filename' AND location='$location'"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        quantity = cursor.getInt(cursor.getColumnIndex("count"))
        if(quantity == 0){
            itemDetail = " , , , , , , , , , , , ,"
        }else {
            var id = ""
            var stockid = ""
            var doc = ""
            var loc = ""
            var bc = ""
            var name = ""
            var prc = ""
            var qty = ""
            var stock = ""
            var pack = ""
            var status = ""
            var lcqty = ""
            var lcvalue = ""
            val db = context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
            val query = "SELECT ROWID,* FROM summery WHERE DocNum='$filename'"
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                id = cursor.getString(16)
                stockid = cursor.getString(1)
                doc = cursor.getString(2)
                loc = cursor.getString(4)
                bc = cursor.getString(5)
                name = cursor.getString(6)
                prc = cursor.getString(7)
                qty = cursor.getString(8)
                stock = cursor.getString(13)
                pack = cursor.getString(14)
                status = cursor.getString(15)

                val selectQuery2 =
                    "SELECT SUM(QNT) AS qty FROM summery WHERE Location='$loc' AND DocNum='$doc'"
                val cursor2 = db.rawQuery(selectQuery2, null)
                if (cursor2.moveToFirst()) {
                    lcqty = cursor2.getString(cursor2.getColumnIndex("qty"))
                }

                val selectQuery3 = "SELECT SUM(SalePrice * QNT) as val FROM summery WHERE DocNum='$filename'"
                val cursor3 = db.rawQuery(selectQuery3, null)
                if (cursor3.moveToFirst()) {
                    lcvalue = cursor3.getString(cursor3.getColumnIndex("val"))
                }


                itemDetail =
                    "$id,$stockid,$doc,$loc,$bc,$name,$prc,$qty,$stock,$pack,$status,$lcqty,$lcvalue"
            }


        }

    }

    fun getNextItem(){

        val db = context.openOrCreateDatabase(DATABASE_2,Context.MODE_PRIVATE,null)
        val query = "SELECT count FROM date WHERE name='$filename' AND location='$location'"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        quantity = cursor.getInt(cursor.getColumnIndex("count"))
        if(quantity == 0){
            itemDetail = " , , , , , , , , , , , ,"
        }else {
            val typedArray = itemDetail.split(",").toTypedArray()
            var sequence = typedArray[0].toInt()
            println(sequence)
            var id: Int
            var stockid = ""
            var doc = ""
            var loc = ""
            var bc = ""
            var name = ""
            var prc = ""
            var qty = ""
            var stock = ""
            var pack = ""
            var status = ""
            var lcqty = ""
            var lcvalue = ""
            val db = context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
            val query =
                "SELECT ROWID,* FROM summery WHERE DocNum='$filename' AND Seq >'$sequence' LIMIT 1"
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                println("HAS NEXT")
                id = cursor.getInt(16)
                stockid = cursor.getString(1)
                doc = cursor.getString(2)
                loc = cursor.getString(4)
                bc = cursor.getString(5)
                name = cursor.getString(6)
                prc = cursor.getString(7)
                qty = cursor.getString(8)
                stock = cursor.getString(13)
                pack = cursor.getString(14)
                status = cursor.getString(15)

                val selectQuery2 =
                    "SELECT SUM(QNT) AS qty FROM summery WHERE Location='$loc' AND DocNum='$doc'"
                val cursor2 = db.rawQuery(selectQuery2, null)
                if (cursor2.moveToFirst()) {
                    lcqty = cursor2.getString(cursor2.getColumnIndex("qty"))
                }

                val selectQuery3 = "SELECT SUM(SalePrice * QNT) as val FROM summery WHERE DocNum='$filename' "
                val cursor3 = db.rawQuery(selectQuery3, null)
                if (cursor3.moveToFirst()) {
                    lcvalue = cursor3.getString(cursor3.getColumnIndex("val"))
                }
                nextCheck = 1

                itemDetail =
                    "$id,$stockid,$doc,$loc,$bc,$name,$prc,$qty,$stock,$pack,$status,$lcqty,$lcvalue"

            } else {
                println("NO HAVE")
                nextCheck = 0
//            Toast.makeText(context, "Last Barcode", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun getPrvItem(){

        val db = context.openOrCreateDatabase(DATABASE_2,Context.MODE_PRIVATE,null)
        val query = "SELECT count FROM date WHERE name='$filename' AND location='$location'"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        quantity = cursor.getInt(cursor.getColumnIndex("count"))
        if(quantity == 0){
            itemDetail = " , , , , , , , , , , , ,"
        }else {
            val typedArray = itemDetail.split(",").toTypedArray()
            var sequence = typedArray[0].toInt()
            println(sequence)
            var id: Int
            var stockid = ""
            var doc = ""
            var loc = ""
            var bc = ""
            var name = ""
            var prc = ""
            var qty = ""
            var stock = ""
            var pack = ""
            var status = ""
            var lcqty = ""
            var lcvalue = ""
            val db = context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
            val query = "SELECT ROWID,* FROM summery WHERE DocNum='$filename' AND Seq<'$sequence'"
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToLast()) {
                println("HAS NEXT")
                id = cursor.getInt(16)
                stockid = cursor.getString(1)
                doc = cursor.getString(2)
                loc = cursor.getString(4)
                bc = cursor.getString(5)
                name = cursor.getString(6)
                prc = cursor.getString(7)
                qty = cursor.getString(8)
                stock = cursor.getString(13)
                pack = cursor.getString(14)
                status = cursor.getString(15)

                val selectQuery2 =
                    "SELECT SUM(QNT) AS qty FROM summery WHERE Location='$loc' AND DocNum='$doc'"
                val cursor2 = db.rawQuery(selectQuery2, null)
                if (cursor2.moveToFirst()) {
                    lcqty = cursor2.getString(cursor2.getColumnIndex("qty"))
                }

                val selectQuery3 = "SELECT SUM(SalePrice * QNT) as val FROM summery WHERE DocNum='$filename' "
                val cursor3 = db.rawQuery(selectQuery3, null)
                if (cursor3.moveToFirst()) {
                    lcvalue = cursor3.getString(cursor3.getColumnIndex("val"))
                }
                nextCheck = 1

                itemDetail =
                    "$id,$stockid,$doc,$loc,$bc,$name,$prc,$qty,$stock,$pack,$status,$lcqty,$lcvalue"

            } else {
                println("NO HAVE")
                nextCheck = 0
//            Toast.makeText(context, "First Barcode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteItem() {
        val typedArray = itemDetail.split(",").toTypedArray()
        var sequence = typedArray[0].toInt()
        var barcode = typedArray[4]
        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        db.delete("summery", "DocNum=? AND Barcode=? AND Seq=?" , arrayOf(filename,barcode, sequence.toString()))

        val query = "SELECT count FROM date WHERE name='$filename' AND location='$location'"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        quantity = cursor.getInt(cursor.getColumnIndex("count"))
        val valu = ContentValues()
        valu.put("count",quantity-1)
        db.update("date",valu,"location=? AND name=?", arrayOf(location, filename))
        db.close()
//         getNextItem()
    }

    fun updateStatus(){
        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        val valu = ContentValues()
        valu.put("updated", updateCheck)
        db.update("date",valu,"location=? AND name=?", arrayOf(location, filename))
        println("Update COMPLETED")
        db.close()
    }

    fun getTotalItems(){
        try{
            val db=context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
            val query = "SELECT count(id) as id FROM masters"
            val cursor = db.rawQuery(query,null)
            if(cursor.moveToFirst()){
                totalItems = cursor.getInt(0)
            }
            else{
                totalItems = 0
            }
            db.close()
        }
        catch(e:Exception){
            Toast.makeText(context,"No master data found",Toast.LENGTH_SHORT).show()
        }

    }

    fun checkSaveFiles(){
        try{
            val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
            val query = "SELECT * FROM date"
            val cursor = db.rawQuery(query,null)
            if(cursor.moveToFirst()){
                fileCount = cursor.count
            }else{
                fileCount = 0
            }

            val query1 = "SELECT * FROM date WHERE updated = 'yes'"
            val cursor1 = db.rawQuery(query1,null)
            if(cursor.moveToFirst()){
                fileSaved = cursor1.count
            }else{
                fileSaved = 0
            }
            db.close()
        }
        catch(e:Exception){
            println("Error")
        }
    }
    fun updateQty(){
        val typedArray = itemDetail.split(",").toTypedArray()
        var sequence = typedArray[0].toInt()
        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        val valu = ContentValues()
        valu.put("QNT", updateQty)
        val itemArray = itemDetail.split(",").toTypedArray()
        db.update("summery",valu,"Location=? AND DocNum=? AND Barcode=? AND Seq=?", arrayOf(location, filename,itemArray[4],sequence.toString()))

        val query = "SELECT count FROM date WHERE name='$filename' AND location='$location'"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        quantity = cursor.getInt(cursor.getColumnIndex("count"))
        if(quantity == 0){
            itemDetail = " , , , , , , , , , , , ,"
        }else {
            var id = ""
            var stockid = ""
            var doc = ""
            var loc = ""
            var bc = ""
            var name = ""
            var prc = ""
            var qty = ""
            var stock = ""
            var pack = ""
            var status = ""
            var lcqty = ""
            var lcvalue = ""
            val typedArray = itemDetail.split(",").toTypedArray()
            var sequence = typedArray[0].toInt()
            var barcode = typedArray[4]
            val db = context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
            val query = "SELECT ROWID,* FROM summery WHERE DocNum='$filename' AND Barcode='$barcode' AND Seq='$sequence'"
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                id = cursor.getString(16)
                stockid = cursor.getString(1)
                doc = cursor.getString(2)
                loc = cursor.getString(4)
                bc = cursor.getString(5)
                name = cursor.getString(6)
                prc = cursor.getString(7)
                qty = cursor.getString(8)
                stock = cursor.getString(13)
                pack = cursor.getString(14)
                status = cursor.getString(15)

                val selectQuery2 =
                    "SELECT SUM(QNT) AS qty FROM summery WHERE Location='$loc' AND DocNum='$doc'"
                val cursor2 = db.rawQuery(selectQuery2, null)
                if (cursor2.moveToFirst()) {
                    lcqty = cursor2.getString(cursor2.getColumnIndex("qty"))
                }

                val selectQuery3 = "SELECT SUM(SalePrice * QNT) as val FROM summery WHERE DocNum='$filename'"
                val cursor3 = db.rawQuery(selectQuery3, null)
                if (cursor3.moveToFirst()) {
                    lcvalue = cursor3.getString(cursor3.getColumnIndex("val"))
                }


                itemDetail =
                    "$id,$stockid,$doc,$loc,$bc,$name,$prc,$qty,$stock,$pack,$status,$lcqty,$lcvalue"
            }

        }
    }

    fun getBu(){
        try{
            val db=context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
            val query = "SELECT * FROM masters"
            val cursor = db.rawQuery(query,null)

            if(cursor.moveToFirst()){
                stockTakeID = cursor.getString(1)
                storeCode   = cursor.getString(2)
                storeName   = cursor.getString( 3)
                if(stockTakeID == "Null"){ stockTakeID = ""}
                if(storeCode == "Null"){ storeCode = ""}
                if(storeName == "Null"){ storeName = ""}
            }

            val query1 = "SELECT * FROM master_businesses"
            val cursor1 = db.rawQuery(query1,null)
            if(cursor1.moveToFirst()){
                BU = cursor1.getString(2)
            }

            db.close()
        }
        catch(e:Exception){

        }

    }

    fun deleteRow(seq:Int){
        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        db.delete("summery", "DocNum=? AND Barcode=? AND Seq=?" , arrayOf(filename, barcode, seq.toString()))

        val query = "SELECT count FROM date WHERE name='$filename' AND location='$location'"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()
        quantity = cursor.getInt(cursor.getColumnIndex("count"))
        val valu = ContentValues()
        valu.put("count",quantity-1)
        db.update("date",valu,"location=? AND name=?", arrayOf(location, filename))

        updateCheck = "no"
        updateStatus()
        db.close()
    }

    fun updateRow(qty:String,seq:Int){

        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        println(filename+barcode+ seq)
        val valu = ContentValues()
        valu.put("QNT", qty)
        db.update("summery",valu, "DocNum=? AND Barcode=? AND Seq=?" , arrayOf(filename, barcode, seq.toString()))
        updateCheck = "no"
        updateStatus()
        println("UPDATED")
        db.close()

    }

    fun deleteFileRow(file:String){
        val db=context.openOrCreateDatabase(DATABASE_2, Context.MODE_PRIVATE, null)
        db.delete("date","name=?", arrayOf(file))
        db.close()

    }

}