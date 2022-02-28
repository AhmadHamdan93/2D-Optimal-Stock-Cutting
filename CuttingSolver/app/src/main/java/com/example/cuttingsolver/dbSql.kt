package com.example.cuttingsolver

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbSql(context: Context) : SQLiteOpenHelper(context, "cut.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table material (id INTEGER PRIMARY KEY AUTOINCREMENT , wid TEXT , high TEXT , qty TEXT , type TEXT )")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS material")
    }

    fun insertData(wid:String,high:String,qty:String,type:String):Boolean{

        val db : SQLiteDatabase=this.writableDatabase
        val contentValues= ContentValues()
        contentValues.put("wid",wid)
        contentValues.put("high",high)
        contentValues.put("qty",qty)
        contentValues.put("type",type)
        val result :Long=db.insert("material",null,contentValues)
        return result>-1
    }

    fun getData():ArrayList<Material>{
        val arrayList=ArrayList<Material>()

        val db:SQLiteDatabase=this.readableDatabase

        val res : Cursor =db.rawQuery("select * from material",null)

        res.moveToFirst()

        while (!res.isAfterLast){
            arrayList.add(Material(res.getString(0).toInt(),res.getString(1),res.getString(2),res.getString(3),res.getString(4)))
            res.moveToNext()
        }
        res.close()
        return arrayList
    }

    fun deleteData():Boolean{
        val db:SQLiteDatabase=this.writableDatabase
        val result:Int=db.delete("material",null, null)
//        db.execSQL("delete from material")
        return result>0
    }

}