package com.nightguard.parent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PinEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_entry)

        val etPin = findViewById<EditText>(R.id.etPin)
        val tvError = findViewById<TextView>(R.id.tvError)
        val btnUnlock = findViewById<Button>(R.id.btnUnlock)

        btnUnlock.setOnClickListener {
            val pin = etPin.text.toString()
            if (Prefs.verifyPin(this, pin)) {
                startActivity(Intent(this, SettingsActivity::class.java))
                finish()
            } else {
                tvError.visibility = View.VISIBLE
                etPin.text.clear()
            }
        }
    }
}
