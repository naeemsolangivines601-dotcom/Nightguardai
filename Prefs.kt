package com.nightguard.parent

import android.content.Context
import java.security.MessageDigest

/**
 * Central helper for all saved settings: PIN, schedule, brightness/volume levels.
 */
object Prefs {
    private const val FILE = "nightguard_prefs"

    private const val KEY_PIN_HASH = "pin_hash"
    private const val KEY_ENABLED = "schedule_enabled"
    private const val KEY_START_HOUR = "start_hour"
    private const val KEY_START_MIN = "start_min"
    private const val KEY_END_HOUR = "end_hour"
    private const val KEY_END_MIN = "end_min"
    private const val KEY_BRIGHTNESS_PCT = "brightness_pct"   // 0-100
    private const val KEY_VOLUME_PCT = "volume_pct"           // 0-100

    private fun prefs(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun isPinSet(context: Context): Boolean =
        prefs(context).contains(KEY_PIN_HASH)

    fun setPin(context: Context, pin: String) {
        prefs(context).edit().putString(KEY_PIN_HASH, sha256(pin)).apply()
    }

    fun verifyPin(context: Context, pin: String): Boolean {
        val saved = prefs(context).getString(KEY_PIN_HASH, null) ?: return false
        return saved == sha256(pin)
    }

    fun setScheduleEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun isScheduleEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ENABLED, false)

    fun setStartTime(context: Context, hour: Int, minute: Int) {
        prefs(context).edit().putInt(KEY_START_HOUR, hour).putInt(KEY_START_MIN, minute).apply()
    }

    fun setEndTime(context: Context, hour: Int, minute: Int) {
        prefs(context).edit().putInt(KEY_END_HOUR, hour).putInt(KEY_END_MIN, minute).apply()
    }

    fun getStartHour(context: Context) = prefs(context).getInt(KEY_START_HOUR, 19) // default 7 PM
    fun getStartMinute(context: Context) = prefs(context).getInt(KEY_START_MIN, 0)
    fun getEndHour(context: Context) = prefs(context).getInt(KEY_END_HOUR, 23)      // default 11 PM
    fun getEndMinute(context: Context) = prefs(context).getInt(KEY_END_MIN, 0)

    fun setBrightnessPct(context: Context, pct: Int) {
        prefs(context).edit().putInt(KEY_BRIGHTNESS_PCT, pct).apply()
    }
    fun getBrightnessPct(context: Context) = prefs(context).getInt(KEY_BRIGHTNESS_PCT, 30)

    fun setVolumePct(context: Context, pct: Int) {
        prefs(context).edit().putInt(KEY_VOLUME_PCT, pct).apply()
    }
    fun getVolumePct(context: Context) = prefs(context).getInt(KEY_VOLUME_PCT, 50)
}
