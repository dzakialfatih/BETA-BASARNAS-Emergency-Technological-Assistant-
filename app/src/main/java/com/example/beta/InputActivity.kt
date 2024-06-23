package com.example.beta

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.beta.response.RefugeeRequest
import com.example.beta.retrofit.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

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

        val tambahkanButton: Button = findViewById(R.id.button_add)
        val progressBar: ProgressBar = findViewById(R.id.progressBar2)
        val genderRadioGroup: RadioGroup = findViewById(R.id.genderRadioGroup)
        val nameEditText: EditText = findViewById(R.id.nama_pengungsi)
        val ageEditText: EditText = findViewById(R.id.usia_pengungsi)
        val poskoEditText: EditText = findViewById(R.id.posko_pengungsi)

        tambahkanButton.setOnClickListener {
            val selectedRadioButtonId = genderRadioGroup.checkedRadioButtonId
            val name = nameEditText.text.toString().trim()
            val age = ageEditText.text.toString().trim()
            val posko = poskoEditText.text.toString().trim()

            when {
                name.isEmpty() -> {
                    Toast.makeText(this, "NAMA WAJIB DIISI", Toast.LENGTH_SHORT).show()
                }
                age.isEmpty() -> {
                    Toast.makeText(this, "USIA WAJIB DIISI", Toast.LENGTH_SHORT).show()
                }
                posko.isEmpty() -> {
                    Toast.makeText(this, "POSKO WAJIB DIISI", Toast.LENGTH_SHORT).show()
                }
                selectedRadioButtonId == -1 -> {
                    Toast.makeText(this, "Pilih jenis kelamin terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                    val gender = selectedRadioButton.text.toString()
                    progressBar.visibility = ProgressBar.VISIBLE

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            RetrofitInstance.api.inputRefugee(
                                RefugeeRequest(name, age.toInt(), gender, posko)
                            )
                            withContext(Dispatchers.Main) {
                                progressBar.visibility = ProgressBar.GONE
                                val intent = Intent(this@InputActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                progressBar.visibility = ProgressBar.GONE
                                Toast.makeText(this@InputActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}