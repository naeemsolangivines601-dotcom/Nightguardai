package com.nightguard.parent

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SettingsActivity : AppCompatActivity() {

    private var startHour = 19
    private var startMinute = 0
    private var endHour = 23
    private var endMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val switchEnabled = findViewById<Switch>(R.id.switchEnabled)
        val btnStartTime = findViewById<Button>(R.id.btnStartTime)
        val btnEndTime = findViewById<Button>(R.id.btnEndTime)
        val seekBrightness = findViewById<SeekBar>(R.id.seekBrightness)
        val seekVolume = findViewById<SeekBar>(R.id.seekVolume)
        val tvBrightnessLabel = findViewById<TextView>(R.id.tvBrightnessLabel)
        val tvVolumeLabel = findViewById<TextView>(R.id.tvVolumeLabel)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnPermissions = findViewById<Button>(R.id.btnPermissions)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)

        // Load saved values
        startHour = Prefs.getStartHour(this)
        startMinute = Prefs.getStartMinute(this)
        endHour = Prefs.getEndHour(this)
        endMinute = Prefs.getEndMinute(this)
        switchEnabled.isChecked = Prefs.isScheduleEnabled(this)
        seekBrightness.progress = Prefs.getBrightnessPct(this)
        seekVolume.progress = Prefs.getVolumePct(this)

        btnStartTime.text = formatTime(startHour, startMinute)
        btnEndTime.text = formatTime(endHour, endMinute)
        tvBrightnessLabel.text = "Brightness: ${seekBrightness.progress}%"
        tvVolumeLabel.text = "Volume: ${seekVolume.progress}%"

        btnStartTime.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                startHour = h; startMinute = m
                btnStartTime.text = formatTime(h, m)
            }, startHour, startMinute, false).show()
        }

        btnEndTime.setOnClickListener {
            TimePickerDialog(this, { _, h, m ->
                endHour = h; endMinute = m
                btnEndTime.text = formatTime(h, m)
            }, endHour, endMinute, false).show()
        }

        seekBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                tvBrightnessLabel.text = "Brightness: $progress%"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        seekVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                tvVolumeLabel.text = "Volume: $progress%"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        btnPermissions.setOnClickListener {
            startActivity(Intent(this, PermissionsActivity::class.java))
        }

        btnSave.setOnClickListener {
            if (!Settings.System.canWrite(this)) {
                Toast.makeText(this, "Pehle 'Modify System Settings' permission allow karein", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, PermissionsActivity::class.java))
                return@setOnClickListener
            }

            Prefs.setStartTime(this, startHour, startMinute)
            Prefs.setEndTime(this, endHour, endMinute)
            Prefs.setBrightnessPct(this, seekBrightness.progress)
            Prefs.setVolumePct(this, seekVolume.progress)
            Prefs.setScheduleEnabled(this, switchEnabled.isChecked)

            if (switchEnabled.isChecked) {
                val serviceIntent = Intent(this, NightGuardService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this, serviceIntent)
                } else {
                    startService(serviceIntent)
                }
                tvStatus.text = "✅ Schedule active: ${formatTime(startHour, startMinute)} – ${formatTime(endHour, endMinute)}"
            } else {
                stopService(Intent(this, NightGuardService::class.java))
                tvStatus.text = "Schedule band hai"
            }

            Toast.makeText(this, "Settings save ho gayi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatTime(h: Int, m: Int): String {
        val period = if (h >= 12) "PM" else "AM"
        var displayHour = h % 12
        if (displayHour == 0) displayHour = 12
        return String.format("%d:%02d %s", displayHour, m, period)
    }
}
