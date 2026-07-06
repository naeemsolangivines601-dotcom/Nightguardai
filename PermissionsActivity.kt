package com.nightguard.parent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PermissionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        val btnGrant = findViewById<Button>(R.id.btnGrantWriteSettings)
        val tvStatus = findViewById<TextView>(R.id.tvWriteSettingsStatus)
        val btnDone = findViewById<Button>(R.id.btnDone)

        updateStatus(tvStatus)

        btnGrant.setOnClickListener {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        btnDone.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus(findViewById(R.id.tvWriteSettingsStatus))
    }

    private fun updateStatus(tv: TextView) {
        tv.text = if (Settings.System.canWrite(this)) {
            "✅ Permission allow ho chuki hai"
        } else {
            "❌ Abhi permission allow nahi hai"
        }
    }
}
