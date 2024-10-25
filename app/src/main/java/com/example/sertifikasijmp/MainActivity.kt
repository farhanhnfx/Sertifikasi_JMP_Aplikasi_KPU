package com.example.sertifikasijmp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sertifikasijmp.adapter.CustomButton
import com.example.sertifikasijmp.adapter.ListBerandaAdapter
import com.example.sertifikasijmp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val buttons = listOf(
        CustomButton("Informasi", R.drawable.sharp_info_24) { informButton() },
        CustomButton("Form Entri", R.drawable.baseline_post_add_24) { formEntryButton() },
        CustomButton("Lihat Data", R.drawable.baseline_library_books_24) { dataButton() },
        CustomButton("Keluar", R.drawable.baseline_exit_to_app_24) { exitButton() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            val adapter = ListBerandaAdapter(this@MainActivity, buttons)
            lvMain.adapter = adapter
        }
    }

    private fun informButton() {
        val intentToInformasiActivity = Intent(this, InformasiActivity::class.java)
        startActivity(intentToInformasiActivity)
    }

    private fun formEntryButton() {
        val intentToFormEntryActivity = Intent(this, FormEntryActivity::class.java)
        startActivity(intentToFormEntryActivity)
    }

    private fun dataButton() {
        val intentToListPemilihActivity = Intent(this, ListPemilihActivity::class.java)
        startActivity(intentToListPemilihActivity)
    }

    private fun exitButton() {
        finish()
    }
}