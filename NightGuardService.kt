package com.nightguard.parent

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationCompat
import java.util.Calendar

/**
 * Foreground service that keeps checking (every 4 seconds) whether the
 * current time is inside the parent-selected night window. If it is,
 * it forces screen brightness and media/ring volume back to the chosen
 * levels — so even if the child changes them, they snap back quickly.
 */
class NightGuardService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var isEnforcing = false

    private val enforceRunnable = object : Runnable {
        override fun run() {
            checkAndEnforce()
            handler.postDelayed(this, CHECK_INTERVAL_MS)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification("NightGuard chal raha hai"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.removeCallbacks(enforceRunnable)
        handler.post(enforceRunnable)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(enforceRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun checkAndEnforce() {
        if (!Prefs.isScheduleEnabled(this)) {
            isEnforcing = false
            return
        }

        val inWindow = isWithinWindow(
            Prefs.getStartHour(this), Prefs.getStartMinute(this),
            Prefs.getEndHour(this), Prefs.getEndMinute(this)
        )

        if (inWindow) {
            enforceBrightness(Prefs.getBrightnessPct(this))
            enforceVolume(Prefs.getVolumePct(this))
            if (!isEnforcing) {
                isEnforcing = true
                updateNotification("Night mode active — brightness & volume locked")
            }
        } else {
            if (isEnforcing) {
                isEnforcing = false
                updateNotification("NightGuard chal raha hai (window ke bahar)")
            }
        }
    }

    private fun isWithinWindow(startH: Int, startM: Int, endH: Int, endM: Int): Boolean {
        val cal = Calendar.getInstance()
        val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val startMinutes = startH * 60 + startM
        val endMinutes = endH * 60 + endM

        return if (startMinutes <= endMinutes) {
            nowMinutes in startMinutes until endMinutes
        } else {
            // Window crosses midnight, e.g. 23:00 -> 06:00
            nowMinutes >= startMinutes || nowMinutes < endMinutes
        }
    }

    private fun enforceBrightness(pct: Int) {
        if (!Settings.System.canWrite(this)) return
        try {
            // Force manual brightness mode so auto-brightness doesn't fight us
            Settings.System.putInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )
            val value = (pct.coerceIn(1, 100) * 255) / 100
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, value)
        } catch (_: Exception) {
            // Ignore — permission may have been revoked
        }
    }

    private fun enforceVolume(pct: Int) {
        try {
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val streams = intArrayOf(
                AudioManager.STREAM_MUSIC,
                AudioManager.STREAM_RING,
                AudioManager.STREAM_NOTIFICATION,
                AudioManager.STREAM_SYSTEM
            )
            for (stream in streams) {
                val max = am.getStreamMaxVolume(stream)
                val target = (pct.coerceIn(0, 100) * max) / 100
                am.setStreamVolume(stream, target, 0)
            }
        } catch (_: Exception) {
            // Some streams (e.g. RING) may be restricted by DND policy on certain devices
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "NightGuard Status",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NightGuard")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setOngoing(true)
            .build()

    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    companion object {
        private const val CHANNEL_ID = "nightguard_channel"
        private const val NOTIFICATION_ID = 1001
        private const val CHECK_INTERVAL_MS = 4000L
    }
}
