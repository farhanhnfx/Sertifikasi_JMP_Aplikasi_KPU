package com.example.sertifikasijmp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sertifikasijmp.Informasi
import com.example.sertifikasijmp.R
import com.example.sertifikasijmp.databinding.ItemInformasiBinding

class InformasiAdapter (
    private val context: Context,
    private val informasiList: List<Informasi>,
    private val listener: (Informasi) -> Unit
) : RecyclerView.Adapter<InformasiAdapter.InformasiViewHolder>() {

    inner class InformasiViewHolder(val binding: ItemInformasiBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.invoke(informasiList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformasiViewHolder {
        val binding = ItemInformasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InformasiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InformasiViewHolder, position: Int) {
        val informasi = informasiList[position]
        with(holder) {
            binding.txtTitle.text = informasi.title
            binding.txtTgl.text = informasi.tgl
            Glide.with(context)
                .load(informasi.imgId)
                .into(binding.imgMain)
        }
    }

    override fun getItemCount(): Int = informasiList.size
}