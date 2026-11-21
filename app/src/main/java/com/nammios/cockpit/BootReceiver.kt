package com.nammios.cockpit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val prefs = context.getSharedPreferences("nammios", Context.MODE_PRIVATE)
        if (prefs.getBoolean("launch_at_boot", false)) {
            // Best-effort only. Android/OEM firmware may block background activity launches.
            // User can always open Nammi OS from the launcher.
            val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
            launch?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                if (launch != null) context.startActivity(launch)
            } catch (_: Exception) {
                // Do not interfere with the vehicle's original launcher.
            }
        }
    }
}
