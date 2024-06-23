package com.example.beta

import android.annotation.SuppressLint
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

class EditActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var poskoEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

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
        ageEditText = findViewById(R.id.usia_pengungsi)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        poskoEditText = findViewById(R.id.posko_pengungsi)
        saveButton = findViewById(R.id.save_button)
        deleteButton = findViewById(R.id.delete_button)
        progressBar = findViewById(R.id.progressBar)

        val refugeeId = intent.getStringExtra("REFUGEE_ID") ?: return

        fetchRefugeeDetails(refugeeId)

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val age = ageEditText.text.toString().trim()
            val posko = poskoEditText.text.toString().trim()
            val selectedRadioButtonId = genderRadioGroup.checkedRadioButtonId
            val gender = findViewById<RadioButton>(selectedRadioButtonId)?.text.toString()

            if (name.isEmpty() || age.isEmpty() || posko.isEmpty() || selectedRadioButtonId == -1) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                updateRefugee(refugeeId, name, age.toInt(), gender, posko)
            }
        }

        deleteButton.setOnClickListener {
            deleteRefugee(refugeeId)
        }
    }

    private fun fetchRefugeeDetails(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getRefugeeById(id)
                withContext(Dispatchers.Main) {
                    nameEditText.setText(response.name)
                    ageEditText.setText(response.age.toString())
                    poskoEditText.setText(response.posko)
                    // Set gender radio button
                    when (response.gender) {
                        "Male" -> genderRadioGroup.check(R.id.radioMale)
                        "Female" -> genderRadioGroup.check(R.id.radioFemale)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateRefugee(id: String, name: String, age: Int, gender: String, posko: String) {
        progressBar.visibility = ProgressBar.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitInstance.api.updateRefugee(id, RefugeeRequest(name, age, gender, posko))
                withContext(Dispatchers.Main) {
                    progressBar.visibility = ProgressBar.GONE
                    val intent = Intent(this@EditActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@EditActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteRefugee(id: String) {
        progressBar.visibility = ProgressBar.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitInstance.api.deleteRefugee(id)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = ProgressBar.GONE
                    val intent = Intent(this@EditActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@EditActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}