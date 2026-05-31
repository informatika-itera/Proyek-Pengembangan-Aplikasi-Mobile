package com.example.pantaujompo.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.Color as AndroidColor

object MapUtils {
    // Fungsi Custom Marker Biru
    fun createCustomMarkerDrawable(context: Context): Drawable {
        val size = 50 // ukuran marker
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Lingkaran luar putih (border)
        val paintBorder = Paint().apply {
            color = AndroidColor.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paintBorder)
        
        // Lingkaran dalam biru
        val paintInner = Paint().apply {
            color = AndroidColor.parseColor("#007AFF") // Biru khas GPS
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(size / 2f, size / 2f, (size / 2f) - 6f, paintInner)
        
        return BitmapDrawable(context.resources, bitmap)
    }
}
