package com.example.pas

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pas.model.Menu
import com.google.firebase.firestore.FirebaseFirestore

class InputMenuActivity : Activity() {
    private lateinit var etNamaMenu: EditText
    private lateinit var etHargaMenu: EditText
    private lateinit var etDeskripsiMenu: EditText
    private lateinit var etUrlGambar: EditText
    private lateinit var ivPreviewGambar: ImageView
    private lateinit var btnPreviewGambar: Button
    private lateinit var btnSimpanMenu: Button
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_menu)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etNamaMenu = findViewById(R.id.etNamaMenu)
        etHargaMenu = findViewById(R.id.etHargaMenu)
        etDeskripsiMenu = findViewById(R.id.etDeskripsiMenu)
        etUrlGambar = findViewById(R.id.etUrlGambar)
        ivPreviewGambar = findViewById(R.id.ivPreviewGambar)
        btnPreviewGambar = findViewById(R.id.btnPreviewGambar)
        btnSimpanMenu = findViewById(R.id.btnSimpanMenu)
    }

    private fun setupListeners() {
        btnPreviewGambar.setOnClickListener {
            previewGambar()
        }

        btnSimpanMenu.setOnClickListener {
            saveMenu()
        }
    }

    private fun previewGambar() {
        val urlGambar = etUrlGambar.text.toString().trim()

        if (urlGambar.isEmpty()) {
            Toast.makeText(this, "Masukkan URL gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidUrl(urlGambar)) {
            Toast.makeText(this, "Format URL tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Tampilkan gambar menggunakan Glide
        Glide.with(this)
            .load(urlGambar)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_delete)
            .into(ivPreviewGambar)

        ivPreviewGambar.visibility = ImageView.VISIBLE
        Toast.makeText(this, "Gambar berhasil dimuat", Toast.LENGTH_SHORT).show()
    }

    private fun isValidUrl(url: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(url).matches() &&
                (url.contains(".jpg") || url.contains(".jpeg") ||
                        url.contains(".png") || url.contains(".gif") ||
                        url.contains(".webp"))
    }

    private fun saveMenu() {
        val nama = etNamaMenu.text.toString().trim()
        val hargaText = etHargaMenu.text.toString().trim()
        val deskripsi = etDeskripsiMenu.text.toString().trim()
        val urlGambar = etUrlGambar.text.toString().trim()

        // Validasi input
        if (nama.isEmpty()) {
            etNamaMenu.error = "Nama motor tidak boleh kosong"
            etNamaMenu.requestFocus()
            return
        }

        if (hargaText.isEmpty()) {
            etHargaMenu.error = "Harga tidak boleh kosong"
            etHargaMenu.requestFocus()
            return
        }

        val harga = hargaText.toDoubleOrNull()
        if (harga == null || harga <= 0) {
            etHargaMenu.error = "Masukkan harga yang valid"
            etHargaMenu.requestFocus()
            return
        }

        if (deskripsi.isEmpty()) {
            etDeskripsiMenu.error = "Deskripsi tidak boleh kosong"
            etDeskripsiMenu.requestFocus()
            return
        }

        if (urlGambar.isNotEmpty() && !isValidUrl(urlGambar)) {
            etUrlGambar.error = "Format URL gambar tidak valid"
            etUrlGambar.requestFocus()
            return
        }

        val menu = Menu(
            id = "",
            nama = nama,
            harga = harga,
            deskripsi = deskripsi,
            urlGambar = urlGambar
        )

        // Disable button saat menyimpan
        btnSimpanMenu.isEnabled = false
        btnSimpanMenu.text = "Menyimpan..."

        firestore.collection("menu")
            .add(menu)
            .addOnSuccessListener {
                Toast.makeText(this, "Menu berhasil disimpan", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
                btnSimpanMenu.isEnabled = true
                btnSimpanMenu.text = "Simpan Pesanan"
            }
    }
}