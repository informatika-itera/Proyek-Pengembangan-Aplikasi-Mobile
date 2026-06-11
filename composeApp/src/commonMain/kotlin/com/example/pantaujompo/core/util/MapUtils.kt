package com.example.pantaujompo.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.Color as AndroidColor

object MapUtils {
    // Fungsi Custom Marker Biru dengan Senter Arah (Directional Cone)
    fun createCustomMarkerDrawable(context: Context): Drawable {
        val size = 120 // ukuran marker diperbesar untuk menampung senter
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val centerX = size / 2f
        val centerY = size / 2f
        val dotRadius = 16f
        
        // 1. Gambar Senter Arah (Directional Cone)
        val paintCone = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            shader = android.graphics.LinearGradient(
                centerX, centerY, centerX, 0f,
                AndroidColor.parseColor("#80007AFF"), // Kuat di dekat titik tengah
                AndroidColor.parseColor("#00007AFF"), // Memudar di ujung
                android.graphics.Shader.TileMode.CLAMP
            )
        }
        val conePath = android.graphics.Path()
        conePath.moveTo(centerX, centerY)
        // Menyebar sekitar 60 derajat ke atas
        conePath.lineTo(centerX - 45f, 15f)
        // Lengkungan di bagian ujung senter
        conePath.quadTo(centerX, -10f, centerX + 45f, 15f)
        conePath.close()
        canvas.drawPath(conePath, paintCone)
        
        // 2. Gambar Lingkaran luar putih (border)
        val paintBorder = Paint().apply {
            color = AndroidColor.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
            setShadowLayer(6f, 0f, 3f, AndroidColor.parseColor("#60000000"))
        }
        canvas.drawCircle(centerX, centerY, dotRadius + 6f, paintBorder)
        
        // 3. Gambar Lingkaran dalam biru
        val paintInner = Paint().apply {
            color = AndroidColor.parseColor("#007AFF") // Biru khas GPS
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(centerX, centerY, dotRadius, paintInner)
        
        return BitmapDrawable(context.resources, bitmap)
    }
}
