package com.example.pas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore

class EditMenuActivity : AppCompatActivity() {
    private lateinit var etNama: EditText
    private lateinit var etHarga: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var etGambar: EditText
    private lateinit var ivPreviewGambar: ImageView
    private lateinit var btnPreview: Button
    private lateinit var btnUbah: Button
    private lateinit var btnHapus: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var menuId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_menu)

        etNama = findViewById(R.id.etNamaMenu)
        etHarga = findViewById(R.id.etHargaMenu)
        etDeskripsi = findViewById(R.id.etDeskripsiMenu)
        etGambar = findViewById(R.id.etGambarMenu)
        ivPreviewGambar = findViewById(R.id.ivPreviewGambar)
        btnPreview = findViewById(R.id.btnPreview)
        btnUbah = findViewById(R.id.btnUbah)
        btnHapus = findViewById(R.id.btnHapus)

        firestore = FirebaseFirestore.getInstance()
        menuId = intent.getStringExtra("menuId") ?: ""

        ambilData()
        setupListeners()
    }

    private fun setupListeners() {
        // Listener untuk tombol preview gambar
        btnPreview.setOnClickListener {
            val urlGambar = etGambar.text.toString().trim()
            if (urlGambar.isEmpty()) {
                Toast.makeText(this, "Masukkan URL gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
                etGambar.requestFocus()
            } else {
                loadImagePreview(urlGambar)
            }
        }

        btnUbah.setOnClickListener {
            updateMenu()
        }

        btnHapus.setOnClickListener {
            hapusMenu()
        }
    }

    private fun updateMenu() {
        val nama = etNama.text.toString().trim()
        val hargaText = etHarga.text.toString().trim()
        val deskripsi = etDeskripsi.text.toString().trim()
        val urlGambar = etGambar.text.toString().trim()

        // Validasi input
        if (nama.isEmpty()) {
            etNama.error = "Nama menu tidak boleh kosong"
            etNama.requestFocus()
            return
        }

        if (hargaText.isEmpty()) {
            etHarga.error = "Harga tidak boleh kosong"
            etHarga.requestFocus()
            return
        }

        val harga = try {
            hargaText.toDouble()
        } catch (e: NumberFormatException) {
            etHarga.error = "Format harga tidak valid"
            etHarga.requestFocus()
            return
        }

        if (harga <= 0) {
            etHarga.error = "Harga harus lebih dari 0"
            etHarga.requestFocus()
            return
        }

        // Update data ke Firestore
        val update = mapOf(
            "nama" to nama,
            "harga" to harga,
            "deskripsi" to deskripsi,
            "urlGambar" to urlGambar
        )

        firestore.collection("menu").document(menuId)
            .update(update)
            .addOnSuccessListener {
                Toast.makeText(this, "Menu berhasil diubah", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengubah menu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun hapusMenu() {
        firestore.collection("menu").document(menuId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Menu berhasil dihapus", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menghapus menu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun ambilData() {
        firestore.collection("menu").document(menuId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    etNama.setText(doc.getString("nama") ?: "")
                    etHarga.setText(doc.getDouble("harga")?.toString() ?: "")
                    etDeskripsi.setText(doc.getString("deskripsi") ?: "")
                    etGambar.setText(doc.getString("urlGambar") ?: "")

                    // Load preview gambar
                    loadImagePreview(doc.getString("urlGambar") ?: "")
                } else {
                    Toast.makeText(this, "Data menu tidak ditemukan", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadImagePreview(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            // Show loading state
            Toast.makeText(this, "Memuat gambar...", Toast.LENGTH_SHORT).show()

            Glide.with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder_motor)
                .error(R.drawable.error_image)
                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: com.bumptech.glide.load.engine.GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Toast.makeText(this@EditMenuActivity, "Gagal memuat gambar. Periksa URL gambar.", Toast.LENGTH_SHORT).show()
                        return false
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Toast.makeText(this@EditMenuActivity, "Gambar berhasil dimuat", Toast.LENGTH_SHORT).show()
                        return false
                    }
                })
                .centerCrop()
                .into(ivPreviewGambar)
        } else {
            ivPreviewGambar.setImageResource(R.drawable.default_motor)
        }
    }
}