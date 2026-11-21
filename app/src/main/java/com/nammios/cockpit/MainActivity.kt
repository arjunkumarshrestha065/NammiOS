package com.nammios.cockpit

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.math.min

class MainActivity : Activity() {
    private lateinit var cockpit: CockpitView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.statusBarColor = Color.rgb(5, 13, 26)
        window.navigationBarColor = Color.rgb(5, 13, 26)
        cockpit = CockpitView()
        setContentView(cockpit)
    }

    private fun goHome() {
        try { startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)) }
        catch (_: Exception) { Toast.makeText(this, "System home unavailable", Toast.LENGTH_SHORT).show() }
    }

    inner class CockpitView : View(this) {
        private val p = Paint(Paint.ANTI_ALIAS_FLAG)
        private val bounds = RectF()
        private var page = "HOME"
        private var scale = 1f
        private var ox = 0f
        private var oy = 0f
        private val navy = Color.rgb(5, 14, 28)
        private val panel = Color.rgb(12, 28, 49)
        private val muted = Color.rgb(148, 173, 200)
        private val cyan = Color.rgb(58, 195, 255)
        private val white = Color.WHITE

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            scale = min(width / 1024f, height / 600f)
            ox = (width - 1024f * scale) / 2f
            oy = (height - 600f * scale) / 2f
            canvas.save()
            canvas.translate(ox, oy)
            canvas.scale(scale, scale)
            drawBackground(canvas)
            if (page == "HOME") drawHome(canvas) else drawSubPage(canvas)
            canvas.restore()
        }

        private fun paint(color: Int, style: Paint.Style = Paint.Style.FILL, stroke: Float = 1f): Paint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; this.style = style; this.strokeWidth = stroke }

        private fun rr(c: Canvas, x: Float, y: Float, w: Float, h: Float, r: Float, color: Int,
                       stroke: Int? = null, sw: Float = 1f) {
            p.reset(); p.isAntiAlias = true; p.color = color
            c.drawRoundRect(x, y, x+w, y+h, r, r, p)
            if (stroke != null) {
                p.style = Paint.Style.STROKE; p.strokeWidth = sw; p.color = stroke
                c.drawRoundRect(x, y, x+w, y+h, r, r, p); p.style = Paint.Style.FILL
            }
        }

        private fun txt(c: Canvas, s: String, x: Float, y: Float, size: Float,
                        color: Int = white, bold: Boolean = false, align: Paint.Align = Paint.Align.LEFT) {
            p.reset(); p.isAntiAlias = true; p.color = color; p.textSize = size; p.textAlign = align
            p.typeface = if (bold) Typeface.create("sans-serif-medium", Typeface.BOLD)
                         else Typeface.create("sans-serif", Typeface.NORMAL)
            c.drawText(s, x, y, p)
        }

        private fun line(c: Canvas, x1: Float, y1: Float, x2: Float, y2: Float, color: Int, sw: Float = 1f) {
            p.reset(); p.isAntiAlias = true; p.color = color; p.strokeWidth = sw
            c.drawLine(x1, y1, x2, y2, p)
        }

        private fun circle(c: Canvas, x: Float, y: Float, r: Float, color: Int,
                           style: Paint.Style = Paint.Style.FILL, sw: Float = 1f) {
            p.reset(); p.isAntiAlias = true; p.color = color; p.style = style; p.strokeWidth = sw
            c.drawCircle(x, y, r, p)
        }

        private fun drawBackground(c: Canvas) {
            p.shader = LinearGradient(0f, 0f, 1024f, 600f,
                intArrayOf(Color.rgb(5,13,27), Color.rgb(8,22,41), Color.rgb(5,13,27)),
                null, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, 1024f, 600f, p); p.shader = null
            // Subtle ambient light blooms
            p.shader = RadialGradient(270f, 280f, 430f,
                intArrayOf(Color.argb(35, 15, 126, 210), Color.TRANSPARENT), null, Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, 1024f, 600f, p); p.shader = null
            for (i in 0..8) for (j in 0..4) circle(c, 18f+i*125, 92f+j*112, 1.1f, Color.argb(35,120,190,230))
        }

        private fun drawHome(c: Canvas) {
            // Top brand bar
            rr(c, 24f, 18f, 42f, 42f, 13f, Color.rgb(16,133,210), Color.rgb(67,198,255), 1.3f)
            txt(c, "N", 45f, 47f, 28f, white, true, Paint.Align.CENTER)
            txt(c, "NAMMI", 78f, 37f, 17f, white, true)
            txt(c, "OS  /  SMART ELECTRIC COCKPIT", 79f, 51f, 8.5f, muted, true)
            circle(c, 745f, 39f, 4f, Color.rgb(69,225,173))
            txt(c, "SYSTEM READY", 757f, 43f, 9f, Color.rgb(105,229,190), true)
            txt(c, "25°C", 865f, 43f, 11f, muted, true)
            txt(c, "12:09", 920f, 43f, 11f, white, true)
            rr(c, 965f, 21f, 34f, 34f, 11f, Color.rgb(17,42,68), Color.rgb(43,91,130))
            txt(c, "⌕", 982f, 45f, 20f, cyan, true, Paint.Align.CENTER)

            txt(c, "YOUR DRIVE, CONNECTED.", 28f, 91f, 10f, cyan, true)
            txt(c, "Good afternoon", 28f, 119f, 23f, white, true)
            txt(c, "Everything you need for a smarter journey.", 28f, 138f, 10f, muted)

            // Hero vehicle card
            rr(c, 24f, 155f, 570f, 345f, 25f, Color.rgb(12,31,55), Color.rgb(31,75,112), 1.2f)
            p.shader = LinearGradient(24f,155f,594f,500f,
                intArrayOf(Color.rgb(20,68,111),Color.rgb(10,34,62),Color.rgb(8,25,46)),
                null,Shader.TileMode.CLAMP)
            c.drawRoundRect(24f,155f,594f,500f,25f,25f,p); p.shader=null
            txt(c, "NAMMI BOX", 49f, 190f, 10f, Color.rgb(167,219,255), true)
            txt(c, "Electric. Elegant. Yours.", 49f, 216f, 21f, white, true)
            rr(c, 490f, 178f, 75f, 28f, 14f, Color.rgb(16,103,169), Color.rgb(65,177,238))
            txt(c, "PARKED", 527f, 196f, 9f, white, true, Paint.Align.CENTER)
            drawCar(c)
            // lower data strip
            line(c, 48f, 420f, 570f, 420f, Color.argb(75,143,193,230))
            drawMetric(c, 52f, "BATTERY", "--", "%")
            drawMetric(c, 222f, "EST. RANGE", "--", "km")
            drawMetric(c, 392f, "SPEED", "--", "km/h")
            txt(c, "VEHICLE DATA NOT CONNECTED", 568f, 485f, 7.5f, Color.rgb(151,190,221), true, Paint.Align.RIGHT)

            // Right side feature cards
            txt(c, "QUICK ACCESS", 620f, 169f, 10f, muted, true)
            feature(c, 620f, 185f, 181f, 142f, "NAVIGATION", "Maps & route guidance", "N", Color.rgb(14,105,190), "⌖")
            feature(c, 816f, 185f, 181f, 142f, "MUSIC", "Your favourite audio", "M", Color.rgb(114,59,174), "♫")
            feature(c, 620f, 343f, 181f, 142f, "360° CAMERA", "Surround view", "C", Color.rgb(0,119,139), "◎")
            feature(c, 816f, 343f, 181f, 142f, "CLIMATE", "Cabin comfort", "A", Color.rgb(24,103,176), "❄")

            // bottom floating dock
            rr(c, 24f, 520f, 973f, 61f, 19f, Color.rgb(10,24,42), Color.rgb(34,67,98), 1f)
            dock(c, 90f, "⌂", "HOME", true)
            dock(c, 270f, "▣", "VEHICLE", false)
            dock(c, 450f, "♫", "MEDIA", false)
            dock(c, 630f, "◎", "CAMERA", false)
            dock(c, 810f, "⚙", "SETTINGS", false)
            line(c, 900f, 531f, 900f, 570f, Color.rgb(42,70,98))
            txt(c, "EXIT", 950f, 555f, 9f, Color.rgb(255,173,173), true, Paint.Align.CENTER)
            txt(c, "↗", 950f, 571f, 15f, Color.rgb(255,173,173), true, Paint.Align.CENTER)
        }

        private fun drawCar(c: Canvas) {
            // soft blue glow under the vehicle
            p.shader = RadialGradient(310f, 352f, 245f,
                intArrayOf(Color.argb(105,25,157,237),Color.argb(0,20,100,180)),null,Shader.TileMode.CLAMP)
            c.drawOval(75f,260f,550f,414f,p); p.shader=null
            // Wheels
            circle(c, 170f, 370f, 37f, Color.rgb(4,12,23))
            circle(c, 170f, 370f, 22f, Color.rgb(80,112,139))
            circle(c, 170f, 370f, 10f, Color.rgb(16,35,54))
            circle(c, 439f, 370f, 37f, Color.rgb(4,12,23))
            circle(c, 439f, 370f, 22f, Color.rgb(80,112,139))
            circle(c, 439f, 370f, 10f, Color.rgb(16,35,54))
            val body = Path().apply {
                moveTo(89f,347f); lineTo(111f,310f); quadTo(124f,286f,168f,281f)
                lineTo(213f,254f); quadTo(232f,242f,267f,242f); lineTo(357f,242f)
                quadTo(387f,244f,411f,276f); lineTo(460f,293f)
                quadTo(489f,303f,504f,329f); lineTo(510f,352f)
                quadTo(507f,365f,486f,367f); lineTo(123f,367f)
                quadTo(91f,366f,89f,347f); close()
            }
            p.shader = LinearGradient(100f,245f,470f,370f,
                intArrayOf(Color.rgb(229,244,252),Color.rgb(102,155,190),Color.rgb(39,88,126)),
                null,Shader.TileMode.CLAMP)
            c.drawPath(body,p); p.shader=null
            val glass = Path().apply {
                moveTo(194f,277f); lineTo(225f,253f); quadTo(240f,248f,267f,248f)
                lineTo(353f,248f); quadTo(374f,249f,393f,279f); close()
            }
            p.color = Color.rgb(16,47,74); c.drawPath(glass,p)
            line(c, 286f, 249f, 286f, 280f, Color.rgb(91,159,197), 2f)
            line(c, 358f, 249f, 382f, 280f, Color.rgb(91,159,197), 2f)
            rr(c, 95f, 328f, 26f, 8f, 4f, Color.rgb(124,224,255))
            rr(c, 485f, 330f, 19f, 8f, 4f, Color.rgb(255,92,105))
            line(c, 132f, 351f, 471f, 351f, Color.rgb(220,241,252), 2f)
            // stylized road lines
            line(c, 75f, 408f, 510f, 408f, Color.argb(65,90,178,220), 1f)
        }

        private fun drawMetric(c: Canvas, x: Float, title: String, value: String, unit: String) {
            txt(c, title, x, 444f, 8f, muted, true)
            txt(c, value, x, 478f, 27f, white, true)
            txt(c, unit, x+37f, 478f, 10f, cyan, true)
        }

        private fun feature(c: Canvas, x: Float, y: Float, w: Float, h: Float,
                            title: String, sub: String, tag: String, color: Int, glyph: String) {
            rr(c, x, y, w, h, 20f, Color.rgb(13,30,51), Color.rgb(34,67,98), 1f)
            p.shader = LinearGradient(x,y,x+w,y+h,
                intArrayOf(Color.argb(95,Color.red(color),Color.green(color),Color.blue(color)),Color.TRANSPARENT),
                null,Shader.TileMode.CLAMP)
            c.drawRoundRect(x,y,x+w,y+h,20f,20f,p); p.shader=null
            rr(c, x+14f, y+13f, 48f, 48f, 15f, color)
            txt(c, glyph, x+38f, y+47f, 25f, white, true, Paint.Align.CENTER)
            txt(c, title, x+14f, y+91f, 11f, white, true)
            txt(c, sub, x+14f, y+110f, 8.5f, muted)
            txt(c, "OPEN  ›", x+w-14f, y+h-14f, 8f, cyan, true, Paint.Align.RIGHT)
        }

        private fun dock(c: Canvas, x: Float, glyph: String, name: String, active: Boolean) {
            if (active) rr(c, x-50f, 526f, 100f, 49f, 14f, Color.rgb(17,65,102))
            txt(c, glyph, x, 550f, 18f, if(active) cyan else muted, true, Paint.Align.CENTER)
            txt(c, name, x, 568f, 7f, if(active) white else muted, true, Paint.Align.CENTER)
        }

        private fun drawSubPage(c: Canvas) {
            rr(c, 24f, 18f, 42f, 42f, 13f, Color.rgb(16,133,210), Color.rgb(67,198,255))
            txt(c, "N", 45f, 47f, 28f, white, true, Paint.Align.CENTER)
            txt(c, "NAMMI OS", 80f, 45f, 17f, white, true)
            txt(c, page, 32f, 126f, 28f, white, true)
            txt(c, "SMART ELECTRIC COCKPIT", 32f, 148f, 9f, cyan, true)
            rr(c, 32f, 178f, 960f, 300f, 24f, panel, Color.rgb(34,70,105))
            val title = when(page) {
                "VEHICLE" -> "Live vehicle information"
                "MEDIA" -> "Music and entertainment"
                "CAMERA" -> "360° surround camera"
                "SETTINGS" -> "Application settings"
                "NAVIGATION" -> "Navigation and route guidance"
                "CLIMATE" -> "Cabin climate"
                else -> page
            }
            txt(c, title, 70f, 235f, 22f, white, true)
            val desc = when(page) {
                "VEHICLE" -> "Battery, range, speed, odometer and TPMS require a supported read-only OEM data interface."
                "MEDIA" -> "Connect a supported music app or USB media source. Playback integration is not yet configured."
                "CAMERA" -> "The factory 360° camera feed requires an approved OEM camera interface."
                "SETTINGS" -> "Launch at startup is best-effort. Vehicle firmware may restrict automatic app launch."
                "NAVIGATION" -> "Map provider integration is the next development step."
                "CLIMATE" -> "Use the original vehicle climate controls until an approved OEM interface is available."
                else -> "Nammi OS"
            }
            txt(c, desc, 70f, 272f, 12f, muted)
            rr(c, 70f, 330f, 150f, 52f, 14f, Color.rgb(16,103,169))
            txt(c, "‹  BACK HOME", 145f, 362f, 11f, white, true, Paint.Align.CENTER)
            rr(c, 32f, 520f, 960f, 61f, 18f, Color.rgb(10,24,42), Color.rgb(34,67,98))
            txt(c, "NAMMI OS  •  PREMIUM COCKPIT", 55f, 555f, 9f, muted, true)
            txt(c, "HOME", 950f, 555f, 10f, cyan, true, Paint.Align.CENTER)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (event.action != MotionEvent.ACTION_UP) return true
            val x = (event.x - ox) / scale
            val y = (event.y - oy) / scale
            if (page != "HOME") {
                if (y > 300f || (x < 250f && y > 300f)) page = "HOME"
            } else {
                when {
                    y in 185f..327f && x in 620f..801f -> page = "NAVIGATION"
                    y in 185f..327f && x in 816f..997f -> page = "MEDIA"
                    y in 343f..485f && x in 620f..801f -> page = "CAMERA"
                    y in 343f..485f && x in 816f..997f -> page = "CLIMATE"
                    y in 520f..590f && x in 210f..350f -> page = "VEHICLE"
                    y in 520f..590f && x in 390f..520f -> page = "MEDIA"
                    y in 520f..590f && x in 570f..700f -> page = "CAMERA"
                    y in 520f..590f && x in 740f..880f -> page = "SETTINGS"
                    y in 520f..590f && x > 900f -> {
                        startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME))
                    }
                    y in 520f..590f && x < 160f -> page = "HOME"
                }
            }
            invalidate()
            return true
        }
    }
}
