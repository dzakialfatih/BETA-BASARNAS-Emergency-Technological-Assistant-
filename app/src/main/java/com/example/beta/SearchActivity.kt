package com.example.beta

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.example.beta.adapter.RefugeeAdapter
import com.example.beta.retrofit.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var poskoEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var refugeeListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                it.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            }
            window.statusBarColor = android.graphics.Color.TRANSPARENT
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            window.statusBarColor = android.graphics.Color.TRANSPARENT
        }

        nameEditText = findViewById(R.id.nama_pengungsi)
        poskoEditText = findViewById(R.id.posko_pengungsi)
        searchButton = findViewById(R.id.search_button)
        refugeeListView = findViewById(R.id.rv_user)

        searchButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val posko = poskoEditText.text.toString().trim()
            if (name.isEmpty() && posko.isEmpty()) {
                Toast.makeText(this, "Please enter a name or posko to search", Toast.LENGTH_SHORT).show()
            } else {
                searchRefugees(name, posko)
            }
        }
    }

    private fun searchRefugees(name: String, posko: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.searchRefugee(name, posko)
                withContext(Dispatchers.Main) {
                    // Update UI with search results
                    // Example code to update a ListView with the results
                    val adapter = RefugeeAdapter(this@SearchActivity, response)
                    refugeeListView.adapter = adapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SearchActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}