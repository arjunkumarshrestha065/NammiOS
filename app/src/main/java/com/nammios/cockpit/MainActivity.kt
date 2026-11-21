package com.nammios.cockpit

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {
    private val bg = Color.rgb(5, 14, 30)
    private val panel = Color.rgb(13, 31, 58)
    private val blue = Color.rgb(31, 181, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(166, 190, 220)
    private lateinit var root: LinearLayout
    private lateinit var body: LinearLayout
    private var page = "Home"
    private val keyLog = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        buildShell(); showPage("Home")
    }

    private fun shape(color: Int, stroke: Int = Color.TRANSPARENT, radius: Float = 22f) = GradientDrawable().apply {
        setColor(color); cornerRadius = radius
        if (stroke != Color.TRANSPARENT) setStroke(2, stroke)
    }
    private fun label(s: String, size: Float, color: Int = white, bold: Boolean = false) = TextView(this).apply {
        text = s; textSize = size; setTextColor(color); gravity = Gravity.CENTER_VERTICAL
        if (bold) typeface = Typeface.DEFAULT_BOLD
    }
    private fun buildShell() {
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(14, 8, 14, 5); setBackgroundColor(bg) }
        val top = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        top.addView(label("⌂", 28f, blue, true), LinearLayout.LayoutParams(42, 42))
        top.addView(label("Nammi", 20f, white, true)); top.addView(label(" OS", 20f, blue, true))
        top.addView(label("   •   SMART EV COCKPIT", 11f, muted), LinearLayout.LayoutParams(0, 42, 1f))
        top.addView(label("12:09   •   24°C   •   Wi-Fi", 12f, muted)); root.addView(top)
        body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val scroll = ScrollView(this).apply { isFillViewport = true; addView(body) }
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        val nav = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER; background = shape(Color.rgb(8,22,43), Color.rgb(35,67,110), 28f) }
        listOf("⌂" to "Home", "🚘" to "Car", "♫" to "Music", "◎" to "Camera", "▦" to "Apps").forEach { (ico, name) ->
            val b = Button(this).apply { text = "$ico  $name"; isAllCaps = false; textSize = 11f; setTextColor(muted); setBackgroundColor(Color.TRANSPARENT); setOnClickListener { showPage(name) } }
            nav.addView(b, LinearLayout.LayoutParams(0, 46, 1f))
        }
        root.addView(nav); setContentView(root)
    }
    private fun showPage(p: String) {
        page = p; body.removeAllViews()
        when (p) {
            "Home" -> home()
            "Car" -> carPage()
            "Music" -> infoPage("♫  Media", "Music player", "No media source is connected in this prototype.")
            "Camera" -> infoPage("◎  360° Camera", "OEM camera connection required", "The original camera feed needs a supported OEM interface. This page does not access the camera yet.")
            "Apps" -> appsPage()
            "Navigation" -> infoPage("⌖  Navigation", "Map integration", "Navigation and route guidance are not connected yet.")
            "AC" -> infoPage("❄  Climate", "OEM climate interface required", "This is a visual placeholder. No commands are sent to the vehicle AC system.")
            "Settings" -> settingsPage()
        }
    }
    private fun home() {
        val main = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val hero = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; background = shape(Color.rgb(20,52,105), Color.rgb(40,119,220), 26f); setPadding(16,10,16,10) }
        val readings = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        readings.addView(metric("SPEED", "--", "km/h"), LinearLayout.LayoutParams(0,72,1f))
        readings.addView(metric("GEAR", "P", "Park"), LinearLayout.LayoutParams(0,72,1f)); hero.addView(readings)
        hero.addView(label("NAMMI  •  ELECTRIC DRIVE", 11f, Color.rgb(185,218,255), true))
        hero.addView(label("Your drive. Your dashboard.", 17f, white, true))
        hero.addView(label("Connect an approved vehicle data interface to show live readings.", 11f, Color.rgb(215,231,250)))
        val stats = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        stats.addView(label("▰  Battery  --%", 13f, white, true), LinearLayout.LayoutParams(0,38,1f))
        stats.addView(label("◉  Range  -- km", 13f, white, true), LinearLayout.LayoutParams(0,38,1f)); hero.addView(stats)
        main.addView(hero, LinearLayout.LayoutParams(0,-1,1.05f).apply { setMargins(0,0,8,0) })
        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val r1 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val r2 = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        r1.addView(tile("⌖","Navigation",Color.rgb(0,103,220)){showPage("Navigation")}, LinearLayout.LayoutParams(0,-1,1f).apply{setMargins(4,0,4,4)})
        r1.addView(tile("♫","Media",Color.rgb(133,35,174)){showPage("Music")}, LinearLayout.LayoutParams(0,-1,1f).apply{setMargins(4,0,4,4)})
        r2.addView(tile("◎","360° Camera",Color.rgb(0,132,151)){showPage("Camera")}, LinearLayout.LayoutParams(0,-1,1f).apply{setMargins(4,4,4,0)})
        r2.addView(tile("❄","AC Control",Color.rgb(0,93,190)){showPage("AC")}, LinearLayout.LayoutParams(0,-1,1f).apply{setMargins(4,4,4,0)})
        grid.addView(r1, LinearLayout.LayoutParams(-1,0,1f)); grid.addView(r2, LinearLayout.LayoutParams(-1,0,1f))
        main.addView(grid, LinearLayout.LayoutParams(0,-1,1.35f)); body.addView(main, LinearLayout.LayoutParams(-1,0,1f).apply{setMargins(0,2,0,8)})
        val quick = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; background = shape(Color.rgb(10,29,57),Color.rgb(28,61,103),22f); setPadding(5,4,5,4) }
        quick.addView(quick("⇥  Exit","Original home"){exitHome()}, LinearLayout.LayoutParams(0,64,1f))
        quick.addView(quick("♫  Media","Open player"){showPage("Music")}, LinearLayout.LayoutParams(0,64,1f))
        quick.addView(quick("❄  Climate","OEM required"){showPage("AC")}, LinearLayout.LayoutParams(0,64,1f))
        quick.addView(quick("🎙  Voice","Page commands"){voice()}, LinearLayout.LayoutParams(0,64,1f))
        body.addView(quick); body.addView(label("Apps → Settings includes steering-wheel key-event diagnostics.",10f,muted))
    }
    private fun metric(a:String,b:String,c:String) = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER; addView(label(a,10f,muted,true)); addView(label(b,29f,white,true)); addView(label(c,10f,muted)) }
    private fun tile(icon:String,name:String,color:Int,click:()->Unit) = LinearLayout(this).apply {
        orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER; background=shape(color,Color.argb(90,255,255,255),22f); setPadding(4,4,4,4)
        addView(label(icon,32f,white,true).apply{gravity=Gravity.CENTER}); addView(label(name,13f,white,true).apply{gravity=Gravity.CENTER}); setOnClickListener{click()}
    }
    private fun quick(a:String,b:String,click:()->Unit) = LinearLayout(this).apply {
        orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER; addView(label(a,14f,white,true).apply{gravity=Gravity.CENTER}); addView(label(b,9f,muted).apply{gravity=Gravity.CENTER}); setOnClickListener{click()}
    }
    private fun carPage() {
        body.addView(label("Vehicle overview",24f,white,true).apply{setPadding(8,10,8,10)})
        val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}
        listOf("BATTERY" to "--%","RANGE" to "-- km","SPEED" to "-- km/h","ODOMETER" to "-- km").forEach { (k,v)->row.addView(metric(k,v,"Not connected"),LinearLayout.LayoutParams(0,110,1f).apply{setMargins(4,4,4,4)}) }
        body.addView(row); card("Vehicle data status","Live values remain unavailable until a documented, read-only OEM data connection is verified.")
        card("TPMS / energy use","Not available in this prototype.")
    }
    private fun appsPage() { body.addView(label("All apps",24f,white,true)); listOf("Navigation","Music","Camera","AC","Settings","Car").forEach { n -> body.addView(action("▦    $n"){showPage(n)}) } }
    private fun settingsPage() {
        body.addView(label("Settings",24f,white,true))
        val prefs=getSharedPreferences("nammios",MODE_PRIVATE)
        body.addView(Switch(this).apply { text="Request launch when vehicle starts";setTextColor(white);isChecked=prefs.getBoolean("launch_at_boot",false);setOnCheckedChangeListener{_,v->prefs.edit().putBoolean("launch_at_boot",v).apply()} })
        card("Steering-wheel key test","While parked, press a steering-wheel button. Android key events delivered to this app appear below. The app does not consume or remap the buttons.")
        body.addView(label(if(keyLog.isEmpty()) "No key events received." else keyLog.takeLast(10).joinToString("\n"),12f,muted))
        body.addView(action("Clear diagnostic log"){keyLog.clear();showPage("Settings")}); body.addView(action("Exit to original home"){exitHome()})
    }
    private fun infoPage(title:String,sub:String,detail:String){body.addView(label(title,24f,white,true).apply{setPadding(8,12,8,12)});card(sub,detail);body.addView(action("←  Back to Home"){showPage("Home")})}
    private fun card(title:String,detail:String){val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;background=shape(panel,Color.rgb(35,68,111),20f);setPadding(16,13,16,13)};box.addView(label(title,17f,blue,true));box.addView(label(detail,13f,white).apply{setPadding(0,7,0,0)});body.addView(box,LinearLayout.LayoutParams(-1,-2).apply{setMargins(5,5,5,8)})}
    private fun action(s:String,click:()->Unit)=Button(this).apply{text=s;isAllCaps=false;textSize=13f;setTextColor(white);background=shape(Color.rgb(18,43,78),Color.rgb(42,95,151),16f);setOnClickListener{click()}}
    private fun voice(){val i=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());putExtra(RecognizerIntent.EXTRA_PROMPT,"Say home, car, navigation, music, camera, AC or settings")};try{@Suppress("DEPRECATION") startActivityForResult(i,1001)}catch(_:Exception){Toast.makeText(this,"Speech recognition unavailable",Toast.LENGTH_SHORT).show()}}
    @Deprecated("Compatibility callback") override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){super.onActivityResult(requestCode,resultCode,data);if(requestCode==1001&&resultCode==RESULT_OK){val s=data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.lowercase(Locale.getDefault())?:return;showPage(when{ "navigation" in s||"map" in s->"Navigation";"music" in s||"media" in s->"Music";"camera" in s->"Camera";"climate" in s||s=="ac"->"AC";"setting" in s->"Settings";"car" in s->"Car";else->"Home"})}}
    override fun dispatchKeyEvent(event:KeyEvent):Boolean{if(event.action==KeyEvent.ACTION_DOWN){keyLog.add("KeyCode ${event.keyCode} / Scan ${event.scanCode}");if(keyLog.size>30)keyLog.removeAt(0);if(page=="Settings")showPage("Settings")};return super.dispatchKeyEvent(event)}
    private fun exitHome(){try{startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME))}catch(_:Exception){Toast.makeText(this,"Could not open system home",Toast.LENGTH_SHORT).show()}}
}
