package com.example.sertifikasijmp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.sertifikasijmp.databinding.ListBerandaBinding

class ListBerandaAdapter(
    private val context: Context,
    private val buttons: List<CustomButton>
) : BaseAdapter() {

    override fun getCount(): Int {
        return buttons.size
    }

    override fun getItem(position: Int): Any {
        return buttons[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ListBerandaBinding

        // If the view is being reused, use it; otherwise, inflate a new view
        if (convertView == null) {
            binding = ListBerandaBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            binding = ListBerandaBinding.bind(convertView)
        }

        val button = buttons[position]
        with(binding) {
            icon.setImageResource(button.icon)
            txtName.text = button.text
            root.setOnClickListener {
                button.onClick.invoke()
            }
        }

        return binding.root
    }
}


class CustomButton(val text: String, val icon: Int, val onClick: () -> Unit)
