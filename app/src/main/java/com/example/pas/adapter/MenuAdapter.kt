package com.example.pas.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pas.R
import com.example.pas.model.Menu
import java.text.NumberFormat
import java.util.*

class MenuAdapter(private val menuList: List<Menu>,
                  private val onItemClick: (Menu) -> Unit) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]

        // Set text data
        holder.tvNamaMenu.text = menu.nama
        holder.tvHarga.text = formatRupiah(menu.harga)
        holder.tvDeskripsi.text = menu.deskripsi

        // Load gambar menggunakan Glide
        loadImageWithGlide(holder.ivGambarMenu, menu.urlGambar)

        // Set click listener
        holder.itemView.setOnClickListener { onItemClick(menu) }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    private fun loadImageWithGlide(imageView: ImageView, imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder_motor) // Placeholder saat loading
                .error(R.drawable.error_image) // Gambar saat error
                .fallback(R.drawable.default_motor) // Gambar default jika URL kosong
                .centerCrop()
                .into(imageView)
        } else {
            // Jika tidak ada URL gambar, tampilkan gambar default
            imageView.setImageResource(R.drawable.default_motor)
        }
    }

    private fun formatRupiah(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(amount).replace("IDR", "Rp")
    }

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivGambarMenu: ImageView = itemView.findViewById(R.id.ivGambarMenu)
        val tvNamaMenu: TextView = itemView.findViewById(R.id.tvNamaMenu)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvDeskripsi: TextView = itemView.findViewById(R.id.tvDeskripsi)
    }
}