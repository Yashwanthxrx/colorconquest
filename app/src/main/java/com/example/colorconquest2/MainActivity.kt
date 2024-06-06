package com.example.colorconquest2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var helpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.play)
        helpButton = findViewById(R.id.help)

        button.setOnClickListener {
            val intent = Intent(this, Page2::class.java)
            startActivity(intent)
        }

        helpButton.setOnClickListener {
            showRulesDialog()
        }
    }

    private fun showRulesDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.rules_dialog, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        dialog.show()
    }
}