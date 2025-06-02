package com.example.pas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class pemesanan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pemesanan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pemesananMotor)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val pindahback: ImageView = findViewById(R.id.back)

        pindahback.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val pindah = findViewById<Button>(R.id.buttonselesai)
        pindah.setOnClickListener {
            val intent = Intent(this, selesai::class.java)
            startActivity(intent)
        }
    }
}