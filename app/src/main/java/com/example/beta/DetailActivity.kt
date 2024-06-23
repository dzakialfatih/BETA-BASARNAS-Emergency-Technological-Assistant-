package com.example.beta

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.beta.response.RefugeeDetailResponse
import com.example.beta.retrofit.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {
    private lateinit var nameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var poskoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

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

        nameTextView = findViewById(R.id.nama_pengungsi)
        ageTextView = findViewById(R.id.usia_pengungsi)
        genderTextView = findViewById(R.id.genderRadioGroup)
        poskoTextView = findViewById(R.id.posko_pengungsi)

        val refugeeId = intent.getStringExtra("REFUGEE_ID") ?: return

        fetchRefugeeDetails(refugeeId)
    }

    private fun fetchRefugeeDetails(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getRefugeeById(id)
                withContext(Dispatchers.Main) {
                    displayRefugeeDetails(response)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayRefugeeDetails(details: RefugeeDetailResponse) {
        nameTextView.text = details.name
        ageTextView.text = details.age.toString()
        genderTextView.text = details.gender
        poskoTextView.text = details.posko
    }
}