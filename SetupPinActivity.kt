package com.nightguard.parent

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SetupPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_pin)

        val etPin = findViewById<EditText>(R.id.etPin)
        val etConfirm = findViewById<EditText>(R.id.etPinConfirm)
        val tvError = findViewById<TextView>(R.id.tvError)
        val btnSave = findViewById<Button>(R.id.btnSavePin)

        btnSave.setOnClickListener {
            val pin = etPin.text.toString()
            val confirm = etConfirm.text.toString()

            when {
                pin.length != 4 -> tvError.text = "PIN 4 digit ka hona chahiye"
                pin != confirm -> tvError.text = "PIN match nahi ho raha"
                else -> {
                    Prefs.setPin(this, pin)
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                }
            }
        }
    }
}
