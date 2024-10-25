package com.example.sertifikasijmp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.math.ln

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory? = null) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "kpu_db"
        private const val DATABASE_VERSION = 3

        const val TABLE_NAME = "pemilih_table"
        const val ID_COL = "id"
        const val NIK_COL = "nik"
        const val NAMA_COL = "nama"
        const val PHONE_COL = "no_hp"
        const val JK_COL = "jenis_kelamin"
        const val TANGGAL_COL = "tanggal"
        const val ALAMAT_COL = "alamat"
        const val LAT_COL = "latitude"
        const val LNG_COL = "longitude"
        const val FOTO_COL = "img_path"

        private const val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$NIK_COL TEXT NOT NULL, " +
                "$NAMA_COL TEXT NOT NULL, " +
                "$PHONE_COL TEXT, " +
                "$JK_COL TEXT, " +
                "$TANGGAL_COL TEXT, " +
                "$ALAMAT_COL TEXT, " +
                "$LAT_COL REAL, " +
                "$LNG_COL REAL, " +
                "$FOTO_COL TEXT" +
                ")")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert a new record into the database
    fun insertPemilih(nik: String, nama: String, phone: String?, jk: String?, tanggal: String?, alamat: String?, lat: Double?, lng: Double?, foto: String?): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(NIK_COL, nik)
            put(NAMA_COL, nama)
            put(PHONE_COL, phone)
            put(JK_COL, jk)
            put(TANGGAL_COL, tanggal)
            put(ALAMAT_COL, alamat)
            put(LAT_COL, lat)
            put(LNG_COL, lng)
            put(FOTO_COL, foto)
        }
        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result
    }

//    fun getAllPemilih(): Cursor {
//        val db = this.readableDatabase
//        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
//    }

    // Method to get all pemilih records from the database
    fun getAllPemilih(): List<Pemilih> {
        val pemilihList = mutableListOf<Pemilih>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COL))
                val nik = cursor.getString(cursor.getColumnIndexOrThrow(NIK_COL))
                val nama = cursor.getString(cursor.getColumnIndexOrThrow(NAMA_COL))
                val phone = cursor.getString(cursor.getColumnIndexOrThrow(PHONE_COL))
                val jk = cursor.getString(cursor.getColumnIndexOrThrow(JK_COL))
                val tanggal = cursor.getString(cursor.getColumnIndexOrThrow(TANGGAL_COL))
                val alamat = cursor.getString(cursor.getColumnIndexOrThrow(ALAMAT_COL))
                val lat = cursor.getDouble(cursor.getColumnIndexOrThrow(LAT_COL))
                val lng = cursor.getDouble(cursor.getColumnIndexOrThrow(LNG_COL))
                val foto = cursor.getString(cursor.getColumnIndexOrThrow(FOTO_COL))

                // Create a Pemilih object and add it to the list
                val pemilih = Pemilih(id, nik, nama, phone, jk, tanggal, alamat, lat, lng, foto)
                pemilihList.add(pemilih)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return pemilihList
    }

    fun getPemilih(id: Int): Pemilih? {
        val db = this.readableDatabase

        // Define the columns you want to retrieve
        val columns = arrayOf(ID_COL, NIK_COL, NAMA_COL, PHONE_COL, JK_COL, TANGGAL_COL, ALAMAT_COL, LAT_COL, LNG_COL, FOTO_COL)

        // Define the selection (WHERE clause)
        val selection = "$ID_COL = ?"
        val selectionArgs = arrayOf(id.toString())

        // Query the database
        val cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null)

        // If a result is found
        return if (cursor.moveToFirst()) {
            // Extract data from cursor
            val pemilih = Pemilih(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COL)),
                nik = cursor.getString(cursor.getColumnIndexOrThrow(NIK_COL)),
                nama = cursor.getString(cursor.getColumnIndexOrThrow(NAMA_COL)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(PHONE_COL)),
                jenisKelamin = cursor.getString(cursor.getColumnIndexOrThrow(JK_COL)),
                tanggal = cursor.getString(cursor.getColumnIndexOrThrow(TANGGAL_COL)),
                alamat = cursor.getString(cursor.getColumnIndexOrThrow(ALAMAT_COL)),
                latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LAT_COL)),
                longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LNG_COL)),
                imgPath = cursor.getString(cursor.getColumnIndexOrThrow(FOTO_COL))
            )
            cursor.close() // Close the cursor
            pemilih
        } else {
            cursor.close() // Close the cursor if no result found
            null
        }
    }

    fun updatePemilih(id: Int, nik: String, nama: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(NIK_COL, nik)
            put(NAMA_COL, nama)
        }
        return db.update(TABLE_NAME, contentValues, "$ID_COL = ?", arrayOf(id.toString()))
    }

    fun deletePemilih(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$ID_COL = ?", arrayOf(id.toString()))
    }

    fun isNikOrIdExists(nik: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(ID_COL), // Specify the columns you want to retrieve
            "$NIK_COL = ?",
            arrayOf(nik),
            null,
            null,
            null
        )

        val exists = cursor.count > 0
        cursor.close() // Close the cursor after use
        return exists
    }



}
