package com.nammios.cockpit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val Navy = Color(0xFF071426)
private val Panel = Color(0xFF10243B)
private val Blue = Color(0xFF28A9FF)
private val White = Color(0xFFEAF5FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NammiApp() }
    }
}

@Composable
fun NammiApp() {
    val context = LocalContext.current
    var page by remember { mutableStateOf("Cockpit") }
    val prefs = remember { context.getSharedPreferences("nammios", 0) }
    var boot by remember { mutableStateOf(prefs.getBoolean("launch_on_boot", false)) }
    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val words = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()?.lowercase(Locale.getDefault()).orEmpty()
            page = when {
                "map" in words || "navigation" in words -> "Navigation"
                "radio" in words -> "Radio"
                "music" in words || "usb" in words -> "USB Media"
                "youtube" in words -> "YouTube Music"
                "camera" in words -> "360 Camera"
                "air" in words || "climate" in words || "ac" in words -> "AC"
                "setting" in words -> "Settings"
                else -> "Cockpit"
            }
        }
    }
    MaterialTheme(colorScheme = darkColorScheme(
        background = Navy, surface = Panel, primary = Blue, onBackground = White,
        onSurface = White, onPrimary = Navy
    )) {
        Column(Modifier.fillMaxSize().background(Navy).padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("NAMMI OS", color = Blue, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Text(page, color = White, fontSize = 16.sp)
                Spacer(Modifier.width(16.dp))
                Text("EXIT", color = Blue, modifier = Modifier.clickable {
                    val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                    context.startActivity(home)
                }.padding(8.dp))
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.weight(1f)) {
                Column(Modifier.width(170.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Cockpit", "Navigation", "Radio", "USB Media", "YouTube Music",
                        "360 Camera", "AC", "Settings").forEach { item ->
                        Surface(
                            color = if (page == item) Blue else Panel,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().clickable { page = item }
                        ) {
                            Text(item, color = if (page == item) Navy else White,
                                modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                        }
                        runCatching { voiceLauncher.launch(intent) }
                    }, modifier = Modifier.fillMaxWidth()) { Text("VOICE") }
                }
                Spacer(Modifier.width(14.dp))
                Surface(Modifier.weight(1f).fillMaxHeight(), color = Panel,
                    shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.fillMaxSize().padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        when (page) {
                            "Cockpit" -> {
                                Text("EV COCKPIT", color = Blue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Metric("Battery", "--")
                                    Metric("Range", "-- km")
                                    Metric("Speed", "-- km/h")
                                    Metric("TPMS", "--")
                                }
                                Text("Vehicle data unavailable – requires a supported OEM interface.", color = White)
                            }
                            "Settings" -> {
                                Text("SETTINGS", color = Blue, fontSize = 22.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Launch Nammi OS at startup", Modifier.weight(1f), color = White)
                                    Switch(checked = boot, onCheckedChange = {
                                        boot = it
                                        prefs.edit().putBoolean("launch_on_boot", it).apply()
                                    })
                                }
                                Text("Startup behavior depends on Android and vehicle firmware.", color = White)
                            }
                            "360 Camera" -> Placeholder("360° CAMERA", "Requires a supported OEM camera feed/API.")
                            "AC" -> Placeholder("CLIMATE CONTROL", "Requires a documented OEM climate-control API.")
                            else -> Placeholder(page.uppercase(), "Integration screen placeholder. Connect a supported app or media source.")
                        }
                    }
                }
            }
            Text("NAMMI OS • V1 PROTOTYPE • OEM FUNCTIONS NOT CONNECTED",
                color = Color(0xFF8298AE), fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Surface(color = Navy, shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = Blue, fontSize = 13.sp)
            Text(value, color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
private fun Placeholder(title: String, detail: String) {
    Text(title, color = Blue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    Text(detail, color = White, fontSize = 16.sp)
}
