package com.nammios.cockpit

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {
    private val bg = Color.rgb(5, 14, 29)
    private val panel = Color.rgb(13, 31, 54)
    private val panelLine = Color.rgb(31, 67, 103)
    private val cyan = Color.rgb(47, 188, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(151, 177, 205)
    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private lateinit var pageTitle: TextView
    private var page = "Home"
    private val keyLog = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        buildShell(); showPage("Home")
    }

    private fun rounded(color: Int, stroke: Int = Color.TRANSPARENT, radius: Float = 20f) = GradientDrawable().apply {
        setColor(color); cornerRadius = radius
        if (stroke != Color.TRANSPARENT) setStroke(1, stroke)
    }
    private fun grad(a: Int, b: Int) = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(a,b)).apply { cornerRadius = 24f }
    private fun txt(s: String, size: Float, color: Int = white, bold: Boolean = false) = TextView(this).apply {
        text=s; textSize=size; setTextColor(color); gravity=Gravity.CENTER_VERTICAL
        if (bold) typeface=Typeface.create("sans-serif-medium", Typeface.BOLD)
    }

    private fun buildShell() {
        root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(18,8,18,8); setBackgroundColor(bg) }
        val header=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
        val logo=txt("N",22f,white,true).apply { gravity=Gravity.CENTER; background=rounded(Color.rgb(0,132,215),cyan,12f) }
        header.addView(logo,LinearLayout.LayoutParams(38,38))
        val brand=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(9,0,0,0) }
        brand.addView(txt("NAMMI OS",17f,white,true)); brand.addView(txt("SMART ELECTRIC COCKPIT",8f,muted,true))
        header.addView(brand); header.addView(Space(this),LinearLayout.LayoutParams(0,1,1f))
        header.addView(txt("● READY",9f,Color.rgb(77,220,173),true))
        header.addView(txt("     12:09     24°C     ",10f,muted))
        header.addView(topAction("VOICE", { startVoice() }))
        header.addView(topAction("EXIT", { goHome() }))
        root.addView(header,LinearLayout.LayoutParams(-1,43))
        pageTitle=txt("OVERVIEW  /  YOUR ELECTRIC DRIVE",10f,cyan,true).apply { setPadding(3,4,0,5) }
        root.addView(pageTitle)
        content=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL }
        val scroll=ScrollView(this).apply { isFillViewport=true; addView(content) }
        root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f))
        val dock=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER; background=rounded(Color.rgb(9,24,43),panelLine,20f); setPadding(5,2,5,2) }
        listOf("⌂" to "Home","◈" to "Vehicle","♫" to "Media","◎" to "Camera","▦" to "Apps").forEach { (ic,n)-> dock.addView(dockButton(ic,n),LinearLayout.LayoutParams(0,44,1f)) }
        root.addView(dock,LinearLayout.LayoutParams(-1,48)); setContentView(root)
    }

    private fun topAction(name:String, action:()->Unit)=txt(name,9f,white,true).apply {
        gravity=Gravity.CENTER; background=rounded(Color.rgb(19,49,78),panelLine,11f); setOnClickListener{action()}
        layoutParams=LinearLayout.LayoutParams(62,32).apply{setMargins(3,0,3,0)}
    }
    private fun dockButton(icon:String,name:String)=LinearLayout(this).apply {
        orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER
        addView(txt(icon,18f,if(page==name) cyan else muted,true).apply{gravity=Gravity.CENTER})
        addView(txt(name.uppercase(),8f,if(page==name) cyan else muted,true).apply{gravity=Gravity.CENTER})
        setOnClickListener{showPage(name)}
    }

    private fun showPage(name:String) {
        page=name; pageTitle.text=if(name=="Home") "OVERVIEW  /  YOUR ELECTRIC DRIVE" else "${name.uppercase()}  /  NAMMI OS"
        content.removeAllViews()
        when(name) {
            "Home" -> homePage()
            "Vehicle" -> vehiclePage()
            "Media" -> simplePage("MEDIA CENTER","Music and entertainment","Connect a supported USB or music app to add playback. Media playback is not yet implemented in this prototype.")
            "Camera" -> simplePage("360° SURROUND VIEW","OEM camera connection required","A supported OEM camera feed is required. This screen does not access or control the original camera system.")
            "Apps" -> appsPage()
            "Navigation" -> simplePage("NAVIGATION","Maps and route guidance","Map provider integration is not included in this prototype.")
            "Climate" -> simplePage("CLIMATE CONTROL","OEM climate interface required","This is a visual placeholder. No AC commands are sent to the vehicle.")
            "Phone" -> simplePage("PHONE","Phone connection","A supported phone integration is required for calls and contacts.")
            "Settings" -> settingsPage()
        }
    }

    private fun homePage() {
        val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}
        val hero=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;background=grad(Color.rgb(19,61,112),Color.rgb(7,27,55));setPadding(17,12,17,10)}
        val heroHead=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
        val headText=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        headText.addView(txt("NAMMI ELECTRIC",9f,Color.rgb(178,219,255),true))
        headText.addView(txt("Ready for the road",18f,white,true))
        heroHead.addView(headText,LinearLayout.LayoutParams(0,-2,1f))
        heroHead.addView(txt("P",18f,white,true).apply{gravity=Gravity.CENTER;background=rounded(Color.rgb(0,119,205),cyan,30f)},LinearLayout.LayoutParams(36,36))
        hero.addView(heroHead)
        hero.addView(VehicleDrawing(this),LinearLayout.LayoutParams(-1,105))
        hero.addView(View(this).apply{setBackgroundColor(Color.argb(70,180,220,255))},LinearLayout.LayoutParams(-1,1))
        val metrics=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
        metrics.addView(metric("BATTERY","--","%"),LinearLayout.LayoutParams(0,65,1f))
        metrics.addView(metric("RANGE","--","km"),LinearLayout.LayoutParams(0,65,1f))
        metrics.addView(metric("SPEED","--","km/h"),LinearLayout.LayoutParams(0,65,1f))
        hero.addView(metrics); hero.addView(txt("LIVE VEHICLE DATA  •  NOT CONNECTED",8f,Color.rgb(177,211,239),true))
        row.addView(hero,LinearLayout.LayoutParams(0,-1,1.15f).apply{setMargins(0,0,9,0)})
        val tiles=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
        val r1=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}; val r2=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}
        r1.addView(appTile("⌖","NAVIGATION","Maps & routes",Color.rgb(12,107,204)){showPage("Navigation")},LinearLayout.LayoutParams(0,-1,1f).apply{setMargins(4,0,4,5)})
        r1.addView(appTile("♫","MUSIC","USB & streaming",Color.rgb(112,48,169)){showPage("Media")},LinearLayout.LayoutParams(0,-1,1f).apply{setMargins(4,0,0,5)})
        r2.addView(appTile("◎","360° CAMERA","Surround view",Color.rgb(0,119,137)){showPage("Camera")},LinearLayout.LayoutParams(0,-1,1f).apply{setMargins(4,5,4,0)})
        r2.addView(appTile("❄","CLIMATE","AC controls",Color.rgb(19,91,174)){showPage("Climate")},LinearLayout.LayoutParams(0,-1,1f).apply{setMargins(4,5,0,0)})
        tiles.addView(r1,LinearLayout.LayoutParams(-1,0,1f)); tiles.addView(r2,LinearLayout.LayoutParams(-1,0,1f))
        row.addView(tiles,LinearLayout.LayoutParams(0,-1,1f)); content.addView(row,LinearLayout.LayoutParams(-1,0,1f).apply{setMargins(0,0,0,8)})
        val quick=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER;background=rounded(Color.rgb(9,24,43),panelLine,18f);setPadding(5,3,5,3)}
        quick.addView(quickItem("☎","PHONE","Connect device"){showPage("Phone")},LinearLayout.LayoutParams(0,52,1f))
        quick.addView(quickItem("♫","AUDIO","Media controls"){showPage("Media")},LinearLayout.LayoutParams(0,52,1f))
        quick.addView(quickItem("❄","CLIMATE","OEM interface"){showPage("Climate")},LinearLayout.LayoutParams(0,52,1f))
        quick.addView(quickItem("⚙","SETTINGS","App settings"){showPage("Settings")},LinearLayout.LayoutParams(0,52,1f))
        quick.addView(quickItem("⇥","EXIT","System home"){goHome()},LinearLayout.LayoutParams(0,52,1f))
        content.addView(quick)
    }

    private fun metric(title:String,value:String,unit:String)=LinearLayout(this).apply{
        orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER
        addView(txt(title,8f,Color.rgb(176,209,237),true).apply{gravity=Gravity.CENTER})
        addView(txt(value,24f,white,true).apply{gravity=Gravity.CENTER})
        addView(txt(unit,8f,muted).apply{gravity=Gravity.CENTER})
    }
    private fun appTile(icon:String,title:String,sub:String,color:Int,action:()->Unit)=LinearLayout(this).apply{
        orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;background=grad(color,Color.rgb((Color.red(color)*.68).toInt(),(Color.green(color)*.68).toInt(),(Color.blue(color)*.68).toInt()));setPadding(4,4,4,4)
        addView(txt(icon,29f,white,true).apply{gravity=Gravity.CENTER})
        addView(txt(title,10f,white,true).apply{gravity=Gravity.CENTER})
        addView(txt(sub,8f,Color.rgb(224,235,248)).apply{gravity=Gravity.CENTER})
        setOnClickListener{action()}
    }
    private fun quickItem(icon:String,title:String,sub:String,action:()->Unit)=LinearLayout(this).apply{
        orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER
        addView(txt("$icon  $title",10f,white,true).apply{gravity=Gravity.CENTER})
        addView(txt(sub,8f,muted).apply{gravity=Gravity.CENTER});setOnClickListener{action()}
    }
    private fun vehiclePage(){
        content.addView(txt("VEHICLE OVERVIEW",21f,white,true).apply{setPadding(5,12,5,10)})
        val row=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}
        listOf("BATTERY" to "--%","RANGE" to "-- km","SPEED" to "-- km/h","ODOMETER" to "-- km").forEach{(k,v)->
            val card=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;background=rounded(panel,panelLine,18f);addView(txt(k,9f,muted,true));addView(txt(v,23f,white,true))}
            row.addView(card,LinearLayout.LayoutParams(0,100,1f).apply{setMargins(4,4,4,4)})
        };content.addView(row);infoCard("Vehicle data connection","No verified read-only OEM data interface is connected. Live readings remain unavailable rather than showing invented values.")
        infoCard("TPMS and energy consumption","These require a supported vehicle data interface.")
    }
    private fun appsPage(){content.addView(txt("ALL APPLICATIONS",21f,white,true).apply{setPadding(5,10,5,10)});listOf("Navigation","Media","Camera","Climate","Phone","Vehicle","Settings").forEach{n->content.addView(menuItem(n){showPage(n)})}}
    private fun settingsPage(){
        content.addView(txt("SYSTEM SETTINGS",21f,white,true).apply{setPadding(5,10,5,10)})
        val sw=Switch(this).apply{text="Request launch when vehicle starts";setTextColor(white);isChecked=getSharedPreferences("nammios",MODE_PRIVATE).getBoolean("launch_at_boot",false);setOnCheckedChangeListener{_,v->getSharedPreferences("nammios",MODE_PRIVATE).edit().putBoolean("launch_at_boot",v).apply();Toast.makeText(this@MainActivity,"Saved. Vehicle firmware may block auto-launch.",Toast.LENGTH_LONG).show()}}
        content.addView(sw);infoCard("Steering-wheel key test","While parked, press a steering-wheel button. If Android forwards its event to this app, the key code appears below. This app does not remap controls.")
        content.addView(txt(if(keyLog.isEmpty())"No key events received." else keyLog.takeLast(10).joinToString("\n"),12f,muted))
        content.addView(menuItem("Clear key event log"){keyLog.clear();showPage("Settings")});content.addView(menuItem("Exit to original system home"){goHome()})
    }
    private fun simplePage(title:String,subtitle:String,detail:String){content.addView(txt(title,23f,white,true).apply{setPadding(8,14,8,10)});infoCard(subtitle,detail);content.addView(menuItem("←  BACK TO HOME"){showPage("Home")})}
    private fun infoCard(title:String,body:String){val c=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;background=rounded(panel,panelLine,18f);setPadding(15,13,15,13);addView(txt(title,15f,cyan,true));addView(txt(body,12f,white).apply{setPadding(0,6,0,0)})};content.addView(c,LinearLayout.LayoutParams(-1,-2).apply{setMargins(4,5,4,7)})}
    private fun menuItem(name:String,action:()->Unit)=txt("   $name                                       ›",13f,white).apply{background=rounded(Color.rgb(17,40,67),panelLine,14f);setPadding(12,0,12,0);layoutParams=LinearLayout.LayoutParams(-1,46).apply{setMargins(4,4,4,4)};setOnClickListener{action()}}

    private fun startVoice(){val i=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());putExtra(RecognizerIntent.EXTRA_PROMPT,"Say home, vehicle, navigation, music, camera, climate or settings")};try{@Suppress("DEPRECATION") startActivityForResult(i,77)}catch(_:Exception){Toast.makeText(this,"Speech recognition unavailable.",Toast.LENGTH_SHORT).show()}}
    @Deprecated("Legacy callback for Android compatibility") override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?){super.onActivityResult(requestCode,resultCode,data);if(requestCode==77&&resultCode==RESULT_OK){val s=data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.lowercase(Locale.getDefault())?:return;showPage(when{ "navigation" in s||"map" in s->"Navigation";"music" in s||"media" in s||"radio" in s->"Media";"camera" in s->"Camera";"climate" in s||"air" in s||s=="ac"->"Climate";"vehicle" in s||"car" in s->"Vehicle";"setting" in s->"Settings";else->"Home"})}}
    override fun dispatchKeyEvent(event:KeyEvent):Boolean{if(event.action==KeyEvent.ACTION_DOWN){keyLog.add("Key ${event.keyCode} / Scan ${event.scanCode}");if(keyLog.size>30)keyLog.removeAt(0);if(page=="Settings")showPage("Settings")};return super.dispatchKeyEvent(event)}
    private fun goHome(){try{startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME))}catch(_:Exception){Toast.makeText(this,"System home unavailable.",Toast.LENGTH_SHORT).show()}}

    private class VehicleDrawing(context:android.content.Context):View(context){
        private val shell=Paint(3).apply{color=Color.rgb(185,211,232);style=Paint.Style.FILL}
        private val glass=Paint(3).apply{color=Color.rgb(18,54,86);style=Paint.Style.FILL}
        private val line=Paint(3).apply{color=Color.rgb(55,193,255);style=Paint.Style.STROKE;strokeWidth=3f}
        private val wheel=Paint(3).apply{color=Color.rgb(5,17,32);style=Paint.Style.FILL}
        override fun onDraw(c:Canvas){super.onDraw(c);val w=width.toFloat();val h=height.toFloat();val p=Path().apply{moveTo(w*.10f,h*.70f);lineTo(w*.18f,h*.49f);quadTo(w*.22f,h*.39f,w*.38f,h*.38f);lineTo(w*.62f,h*.38f);quadTo(w*.72f,h*.39f,w*.79f,h*.51f);lineTo(w*.90f,h*.58f);quadTo(w*.94f,h*.62f,w*.92f,h*.73f);lineTo(w*.86f,h*.78f);lineTo(w*.14f,h*.78f);close()};c.drawPath(p,shell);val g=Path().apply{moveTo(w*.31f,h*.44f);lineTo(w*.61f,h*.44f);lineTo(w*.73f,h*.56f);lineTo(w*.25f,h*.56f);close()};c.drawPath(g,glass);c.drawLine(w*.49f,h*.44f,w*.49f,h*.56f,line);c.drawCircle(w*.28f,h*.75f,h*.105f,wheel);c.drawCircle(w*.76f,h*.75f,h*.105f,wheel);c.drawCircle(w*.28f,h*.75f,h*.047f,shell);c.drawCircle(w*.76f,h*.75f,h*.047f,shell);c.drawLine(w*.11f,h*.69f,w*.89f,h*.69f,line)}
    }
}
