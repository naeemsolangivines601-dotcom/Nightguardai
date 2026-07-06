package com.nightguard.parent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val next = if (!Prefs.isPinSet(this)) {
            Intent(this, SetupPinActivity::class.java)
        } else {
            Intent(this, PinEntryActivity::class.java)
        }
        startActivity(next)
        finish()
    }
}
