package com.example.colorconquest2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

public class Page2 : AppCompatActivity() {
    lateinit var button2: Button
    lateinit var editText: EditText
    lateinit var editText2: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_page2);
        editText=findViewById(R.id.nameone)
        editText2=findViewById(R.id.nametwo)
        button2 = findViewById(R.id.start);
        button2.setOnClickListener {
            val str=editText.text.toString()
            val intent=Intent(applicationContext,MainActivity3::class.java)
            intent.putExtra("message_key",str)
            val str2=editText2.text.toString()
            intent.putExtra("message_key2",str2)
            startActivity(intent)

            }

        }
    }


