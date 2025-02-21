package jp.eyrin.rosjoydroid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class MarkerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var markerX: Float = -1f
    private var markerY: Float = -1f
    private val markerPaint = Paint().apply {
        color = Color.RED  // Customize the marker color as desired
        style = Paint.Style.FILL
    }

    fun setMarkerPosition(x: Float, y: Float) {
        markerX = x
        markerY = y
        invalidate()  // Request a redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (markerX >= 0 && markerY >= 0) {
            // Draw a circle at the touch point
            canvas.drawCircle(markerX, markerY, 20f, markerPaint)  // Adjust radius as needed
        }
    }
}
