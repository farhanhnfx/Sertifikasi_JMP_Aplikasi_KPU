package com.example.sertifikasijmp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sertifikasijmp.adapter.DataPemilihAdapter
import com.example.sertifikasijmp.databinding.ActivityListPemilihBinding

class ListPemilihActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityListPemilihBinding.inflate(layoutInflater)
    }
    private val dbHelper by lazy {
        DBHelper(this)
    }

    companion object {
        val EXTRA_PEMILIH_ID = "DETAIL_PEMILIH_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pemilihList = dbHelper.getAllPemilih()

        with(binding) {
            toolbar.txtTitle.text = "List Pemilih"
            toolbar.icBack.setOnClickListener { finish() }

            rv.layoutManager = LinearLayoutManager(this@ListPemilihActivity)
            rv.adapter = DataPemilihAdapter(pemilihList) {
                pemilih ->
                val intentToFormEntryActivity = Intent(this@ListPemilihActivity, FormEntryActivity::class.java)
                intentToFormEntryActivity.putExtra(EXTRA_PEMILIH_ID, pemilih.id)
                startActivity(intentToFormEntryActivity)
            }
        }
    }
}