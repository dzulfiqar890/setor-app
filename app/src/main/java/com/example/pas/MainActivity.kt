package com.example.pas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pas.adapter.MenuAdapter
import com.example.pas.model.Menu
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var rvMenu: RecyclerView
    private lateinit var btnAddMenu: Button
    private lateinit var btnPembayaran: Button
    private lateinit var menuAdapter: MenuAdapter
    private val menuList = mutableListOf<Menu>()
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        firestore = FirebaseFirestore.getInstance()
        rvMenu = findViewById(R.id.rvMenu)
        btnAddMenu = findViewById(R.id.btnAddMenu)
        btnPembayaran = findViewById(R.id.btnPembayaran)
        menuAdapter = MenuAdapter(menuList) { menu ->
            val intent = Intent(this, EditMenuActivity::class.java)
            intent.putExtra("menuId", menu.id)
            startActivity(intent)
        }
        btnPembayaran.setOnClickListener {
            val intent = Intent(this, pemesanan::class.java)
            startActivity(intent)
        }
        rvMenu.layoutManager = LinearLayoutManager(this)
        rvMenu.adapter = menuAdapter
        btnAddMenu.setOnClickListener {
            val intent = Intent(this, InputMenuActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_MENU)
        }
        loadMenuData()
    }
    private fun loadMenuData() {
        firestore.collection("menu").get().addOnSuccessListener { documents ->
            menuList.clear()
            for (document in documents) {
                val menu = document.toObject(Menu::class.java)
                menu.id = document.id
                menuList.add(menu)
            }
            menuAdapter.notifyDataSetChanged()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_MENU && resultCode == RESULT_OK) {
            loadMenuData()
        }
    }
    companion object {
        const val REQUEST_CODE_ADD_MENU = 1
    }
    override fun onResume() {
        super.onResume()
        loadMenuData()
    }
}