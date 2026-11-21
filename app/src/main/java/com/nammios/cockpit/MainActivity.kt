package com.nammios.cockpit

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private lateinit var status: TextView
    private var currentPage = "Cockpit"
    private val receivedKeys = mutableListOf<String>()

    private val bg = Color.rgb(7, 17, 31)
    private val panel = Color.rgb(17, 34, 55)
    private val blue = Color.rgb(54, 183, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(166, 190, 213)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        buildShell()
        showPage("Cockpit")
    }

    private fun buildShell() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(18, 12, 18, 12)
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val brand = TextView(this).apply {
            text = "NAMMI OS  •  SMART EV COCKPIT"
            textSize = 19f
            setTextColor(white)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        header.addView(brand, LinearLayout.LayoutParams(0, 54, 1f))
        header.addView(actionButton("VOICE") { startVoice() })
        header.addView(actionButton("EXIT") { returnToSystemHome() })
        root.addView(header)

        status = TextView(this).apply {
            text = "SYSTEM READY   •   VEHICLE DATA: NOT CONNECTED"
            textSize = 11f
            setTextColor(blue)
            setPadding(4, 8, 4, 12)
        }
        root.addView(status)

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val scroll = ScrollView(this).apply { addView(content) }
        root.addView(scroll, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        listOf("Cockpit", "Navigation", "Media", "Camera", "AC", "Settings").forEach {
            nav.addView(actionButton(it.uppercase(Locale.getDefault())) { showPage(it) },
                LinearLayout.LayoutParams(0, 48, 1f).apply { setMargins(3, 5, 3, 0) })
        }
        root.addView(nav)
        setContentView(root)
    }

    private fun showPage(page: String) {
        currentPage = page
        content.removeAllViews()
        when (page) {
            "Cockpit" -> {
                heading("Vehicle cockpit")
                val grid = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                listOf("BATTERY" to "--", "RANGE" to "-- km", "SPEED" to "-- km/h",
                    "ODOMETER" to "-- km").forEach { (label, value) ->
                    grid.addView(metricCard(label, value),
                        LinearLayout.LayoutParams(0, 118, 1f).apply { setMargins(4, 4, 4, 4) })
                }
                content.addView(grid)
                infoCard("Live vehicle data",
                    "Not connected. Values are intentionally unavailable until a documented, read-only OEM data interface is verified.")
                infoCard("TPMS and energy use", "Tyre pressure and consumption: not available from this prototype.")
            }
            "Navigation" -> {
                heading("Navigation")
                infoCard("Map integration placeholder",
                    "A map provider and internet connection are required. This prototype does not yet provide turn-by-turn navigation.")
            }
            "Media" -> {
                heading("Media")
                infoCard("Music and USB media", "Media playback is not implemented in this starter build.")
                addAction("Open YouTube Music") {
                    val launch = packageManager.getLaunchIntentForPackage("com.google.android.apps.youtube.music")
                    if (launch != null) startActivity(launch)
                    else Toast.makeText(this, "YouTube Music is not installed.", Toast.LENGTH_SHORT).show()
                }
            }
            "Camera" -> {
                heading("360° camera")
                infoCard("OEM camera connection required",
                    "A supported camera feed/API is required. This screen does not access or control the vehicle camera.")
            }
            "AC" -> {
                heading("Climate control")
                infoCard("OEM climate interface required",
                    "No AC commands are sent. Use the vehicle's original climate controls until a documented OEM interface is available.")
            }
            "Settings" -> {
                heading("Settings")
                val prefs = getSharedPreferences("nammios", MODE_PRIVATE)
                val toggle = Switch(this).apply {
                    text = "Request launch when vehicle starts"
                    setTextColor(white)
                    isChecked = prefs.getBoolean("launch_at_boot", false)
                    setOnCheckedChangeListener { _, checked ->
                        prefs.edit().putBoolean("launch_at_boot", checked).apply()
                        Toast.makeText(this@MainActivity,
                            "Preference saved. Vehicle firmware may prevent automatic launch.",
                            Toast.LENGTH_LONG).show()
                    }
                }
                content.addView(toggle)
                infoCard("Steering-wheel diagnostics",
                    "Press a steering-wheel button only while parked. Android key events received by this app may appear below. Original vehicle functions are not changed.")
                content.addView(TextView(this).apply {
                    text = if (receivedKeys.isEmpty()) "No key events received yet."
                    else receivedKeys.takeLast(8).joinToString("\\n")
                    setTextColor(muted)
                    textSize = 14f
                    setPadding(12, 12, 12, 12)
                })
                addAction("Clear diagnostic log") { receivedKeys.clear(); showPage("Settings") }
            }
        }
    }

    private fun heading(text: String) {
        content.addView(TextView(this).apply {
            this.text = text
            textSize = 23f
            setTextColor(white)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(4, 10, 4, 12)
        })
    }

    private fun infoCard(title: String, body: String) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(panel)
            setPadding(16, 14, 16, 14)
        }
        card.addView(TextView(this).apply {
            text = title; textSize = 17f; setTextColor(blue)
        })
        card.addView(TextView(this).apply {
            text = body; textSize = 14f; setTextColor(white); setPadding(0, 8, 0, 0)
        })
        content.addView(card, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(4, 5, 4, 7) })
    }

    private fun metricCard(label: String, value: String): View =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(panel)
            addView(TextView(this@MainActivity).apply {
                text = label; textSize = 12f; setTextColor(muted)
            })
            addView(TextView(this@MainActivity).apply {
                text = value; textSize = 23f; setTextColor(white)
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            })
        }

    private fun actionButton(label: String, action: () -> Unit) =
        Button(this).apply {
            text = label
            textSize = 11f
            setTextColor(white)
            setBackgroundColor(Color.rgb(28, 75, 112))
            setOnClickListener { action() }
        }

    private fun addAction(label: String, action: () -> Unit) {
        content.addView(actionButton(label, action))
    }

    private fun startVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say cockpit, navigation, media, camera, AC or settings")
        }
        try {
            @Suppress("DEPRECATION")
            startActivityForResult(intent, 1001)
        } catch (_: Exception) {
            Toast.makeText(this, "Speech recognition is unavailable.", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated by Android; retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val spoken = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()?.lowercase(Locale.getDefault()) ?: return
            val page = when {
                "navigation" in spoken || "map" in spoken -> "Navigation"
                "music" in spoken || "media" in spoken || "radio" in spoken -> "Media"
                "camera" in spoken -> "Camera"
                "air" in spoken || "climate" in spoken || "ac" == spoken -> "AC"
                "setting" in spoken -> "Settings"
                else -> "Cockpit"
            }
            showPage(page)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            val label = "keyCode=${event.keyCode}, scanCode=${event.scanCode}, time=${event.eventTime}"
            receivedKeys.add(label)
            if (receivedKeys.size > 30) receivedKeys.removeAt(0)
            if (currentPage == "Settings") showPage("Settings")
        }
        // Do not consume events: preserve normal Android/OEM behavior.
        return super.dispatchKeyEvent(event)
    }

    private fun returnToSystemHome() {
        val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        try { startActivity(home) } catch (_: Exception) {
            Toast.makeText(this, "System home could not be opened.", Toast.LENGTH_SHORT).show()
        }
    }
}
