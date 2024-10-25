package com.example.sertifikasijmp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sertifikasijmp.Pemilih
import com.example.sertifikasijmp.databinding.ItemDataPemilihBinding

class DataPemilihAdapter(
    private val pemilihList: List<Pemilih>,
    private val listener: (Pemilih) -> Unit
) : RecyclerView.Adapter<DataPemilihAdapter.PemilihViewHolder>() {

    inner class PemilihViewHolder(val binding: ItemDataPemilihBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.invoke(pemilihList[position])//.onItemClick(pemilihList[position].id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PemilihViewHolder {
        val binding = ItemDataPemilihBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PemilihViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PemilihViewHolder, position: Int) {
        val pemilih = pemilihList[position]
        holder.binding.txtNik.text = pemilih.nik
        holder.binding.txtNama.text = pemilih.nama
        holder.binding.txtAlamat.text =  truncateAddress(pemilih.alamat)
    }

    override fun getItemCount(): Int = pemilihList.size

    private fun truncateAddress(address: String): String {
        return if (address.length > 50) {
            "${address.substring(0, 47)}..."
        } else {
            address
        }
    }
}
