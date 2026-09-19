package com.quickcopy.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.quickcopy.app.data.ClipboardRepository
import com.quickcopy.app.util.UrlCleaner

/**
 * Headless translucent activity that handles incoming Android shares:
 * 1. Reads Intent.EXTRA_TEXT
 * 2. Cleans tracking params (UTM, fbclid, si, etc.) if enabled
 * 3. Copies to Android ClipboardManager
 * 4. Shows "Copied to clipboard!" toast with cleaned URL info
 * 5. Saves to local history
 * 6. Calls finish() instantly with zero UI disruption
 */
class ShareReceiverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Intent.ACTION_SEND == intent.action && intent.type == "text/plain") {
            handleSharedContent(intent)
        }

        // Finish immediately so the user remains in their current app
        finish()
    }

    private fun handleSharedContent(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) 
            ?: intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
            ?: return

        val prefs = getSharedPreferences("quickcopy_settings", Context.MODE_PRIVATE)
        val cleanTracking = prefs.getBoolean("clean_tracking", true)
        val showToast = prefs.getBoolean("show_toast", true)
        val hapticEnabled = prefs.getBoolean("haptic_feedback", true)

        // Clean tracking parameters if configured
        val cleanResult = if (cleanTracking) {
            UrlCleaner.clean(sharedText)
        } else {
            UrlCleaner.CleanResult(sharedText, false, 0)
        }

        // Copy to system clipboard
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied via QuickCopy", cleanResult.cleanedText)
        clipboard.setPrimaryClip(clip)

        // Haptic feedback
        if (hapticEnabled) {
            triggerHaptic()
        }

        // Optional confirmation toast (Android 13+ already shows clipboard overlay,
        // but Toast highlights parameter cleaning if any occurred!)
        if (showToast) {
            val message = if (cleanResult.paramsRemovedCount > 0) {
                "Copied to clipboard! (Removed ${cleanResult.paramsRemovedCount} tracking tags)"
            } else {
                "Copied to clipboard!"
            }
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }

        // Record to local history repository
        ClipboardRepository.getInstance(applicationContext).addEntry(
            content = cleanResult.cleanedText,
            original = sharedText,
            paramsRemoved = cleanResult.paramsRemovedCount
        )
    }

    private fun triggerHaptic() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(35)
        }
    }
}