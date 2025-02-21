package jp.eyrin.rosjoydroid

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import jp.eyrin.rosjoydroid.ui.theme.ROSJoyDroidTheme
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate

class TouchScreenActivity : GamepadActivity() {


    private var touchX: Float = -1f
    private var touchY: Float = -1f
    private var modeZ: Float = 0f

    private var realX: Float = -1f
    private var realY: Float = -1f

    private lateinit var publishTouchTimer: Timer
    private lateinit var publishJoyTimer: Timer

    private var imageWidthPx: Float = 0.0f
    private var imageHeightPx: Float = 0.0f

    // Variables to store the configurations passed from MainActivity
    private var domainId: Int = 0
    private var ns: String = ""
    private var period: Long = 20L

    private lateinit var markerView: MarkerView

    @SuppressLint("ClickableViewAccessibility", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve variables from Intent extras
        domainId = intent.getIntExtra("domainId", 0)
        ns = intent.getStringExtra("ns") ?: ""
        period = intent.getLongExtra("period", 2L)
        deadZone = intent.getFloatExtra("deadZone", 0.05f)

        // Remove Compose lifecycle effect
        setContent {
//            TouchScreen()
        }

        // Use the XML layout
        setContentView(R.layout.activity_touch_screen)

        // Retrieve the Home Button and set its click listener
        val homeButton = findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this@TouchScreenActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Mode Button setup
        val modeButton = findViewById<Button>(R.id.modeButton)
        modeButton.setOnClickListener {
            if (modeZ == 0f) {
                modeZ = 1f
                modeButton.text = "AUTO"
                modeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_700))
            } else {
                modeZ = 0f
                modeButton.text = "MANUAL"
                modeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.dark))
            }
        }

        // Clear Button setup
        val clearButton = findViewById<Button>(R.id.clearButton)
        clearButton.setOnClickListener {
            touchX = -1f
            touchY = -1f

            realY = -1.0f
            realX = -1.0f

            val TextViewX = findViewById<TextView>(R.id.xDisplay)
            val TextViewY = findViewById<TextView>(R.id.yDisplay)

            TextViewX.text = String.format("X: %.2f", realX)
            TextViewY.text = String.format("Y: %.2f", realY)

            markerView.setMarkerPosition(touchX, touchY)
        }

        val TextViewX = findViewById<TextView>(R.id.xDisplay)
        val TextViewY = findViewById<TextView>(R.id.yDisplay)
        val backgroundImageView = findViewById<ImageView>(R.id.backgroundImageView)

        backgroundImageView.post {
            imageWidthPx = backgroundImageView.width.toFloat()
            imageHeightPx = backgroundImageView.height.toFloat()
        }

        // Get reference to markerView
        markerView = findViewById(R.id.markerView)

        // Set up touch listener on markerView
        markerView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    touchX = event.x
                    touchY = event.y

                    // Update the marker position
                    markerView.setMarkerPosition(touchX, touchY)

                    realX = (touchY / imageHeightPx) * 8.0f
                    realY = (touchX / imageWidthPx) * 15.0f

                    TextViewX.text = String.format("X: %.2f", realX)
                    TextViewY.text = String.format("Y: %.2f", realY)

                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        startPublishJoy(/*domainId, ns, period*/)
        startPublishTouch()

    }

    override fun onPause() {
        super.onPause()
        stopPublishTouch()
        stopPublishJoy()
    }

    override fun onStop() {
        super.onStop()
        stopPublishJoy()
        stopPublishTouch()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPublishTouch()
        stopPublishJoy()
        destroyTouchPublisher()
        destroyJoyPublisher()
    }

    private fun startPublishTouch() {
        stopPublishTouch()
        createTouchPublisher(domainId, ns)
        publishTouchTimer = Timer().also {
            it.schedule(object : TimerTask() {
                override fun run() {
                    publishTouch(realX, realY, modeZ)
                }
            }, 0, period)
        }
    }

    private fun stopPublishTouch() {
        runCatching { publishTouchTimer.cancel() }
        destroyTouchPublisher()
    }


    private fun startPublishJoy(/*domainId: Int, ns: String, period: Long*/) {
        stopPublishJoy()
        createJoyPublisher(domainId, ns)
        publishJoyTimer = Timer().also {
            it.schedule(object : TimerTask() {
                override fun run() {
                    publishJoy(axes, buttons)
                }
            }, 0, period)
        }
    }

    private fun stopPublishJoy() {
        runCatching { publishJoyTimer.cancel() }
        destroyJoyPublisher()
    }

    private external fun createTouchPublisher(domainId: Int, ns: String)
    private external fun destroyTouchPublisher()
    private external fun publishTouch(x: Float, y: Float, z: Float)

    private external fun createJoyPublisher(domainId: Int, ns: String)
    private external fun destroyJoyPublisher()
    private external fun publishJoy(axes: FloatArray, buttons: IntArray)

    companion object {
        init {
            System.loadLibrary("rosjoydroid")
        }
    }
}

