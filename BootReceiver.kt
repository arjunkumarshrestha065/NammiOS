package com.nammios.cockpit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val prefs = context.getSharedPreferences("nammios", Context.MODE_PRIVATE)
        if (prefs.getBoolean("launch_on_boot", false)) {
            // Best-effort only: Android/OEM background-start restrictions may block this.
            val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
            launch?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (launch != null) runCatching { context.startActivity(launch) }
        }
    }
}
