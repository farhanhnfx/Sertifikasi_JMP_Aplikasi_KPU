package com.example.sertifikasijmp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sertifikasijmp.adapter.InformasiAdapter
import com.example.sertifikasijmp.databinding.ActivityInformasiBinding

class InformasiActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityInformasiBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val listInformasi = listOf(
            Informasi("Informasi Hari-H Pemilihan Suara", "Baru saja", R.drawable.kpu_info),
            Informasi("Pengumuman Penting", "31 Desember 2024", R.drawable.kpu_info2)
        )

        with(binding) {
            toolbar.apply {
                txtTitle.text = "Pusat Informasi"
                icBack.setOnClickListener { finish() }
            }
            rv.layoutManager = LinearLayoutManager(this@InformasiActivity)
            rv.adapter = InformasiAdapter(this@InformasiActivity, listInformasi) {

            }
        }
    }
}