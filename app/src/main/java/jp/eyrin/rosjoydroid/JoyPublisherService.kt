// JoystickPublisherService.kt
package jp.eyrin.rosjoydroid

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.Timer
import java.util.TimerTask

class JoyPublisherService : Service() {

    private lateinit var publishJoyTimer: Timer

    companion object {
        const val CHANNEL_ID = "JoyPublisherServiceChannel"
        const val NOTIFICATION_ID = 1
        const val ACTION_STOP_SERVICE = "STOP_JOY_PUBLISHER_SERVICE"

        init {
            System.loadLibrary("rosjoydroid")
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForegroundServiceWithNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                stopForeground(true)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                // Extract parameters from Intent
                val domainId = intent?.getIntExtra("domainId", 0) ?: 0
                val ns = intent?.getStringExtra("ns") ?: ""
                val period = intent?.getLongExtra("period", 20L) ?: 20L
                val deadZone = intent?.getFloatExtra("deadZone", 0.05f) ?: 0.05f
                val axes = intent?.getFloatArrayExtra("axes") ?: floatArrayOf(0.0f)
                val buttons = intent?.getIntArrayExtra("buttons") ?: intArrayOf(0)
                // Update deadZone in GamepadActivity if necessary
                // Since the service is separate, consider how to communicate this if needed

                startPublishJoy(domainId, ns, period, axes, buttons)
            }
        }
        // If the service is killed by the system, it will not restart automatically
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPublishJoy()
        destroyJoyPublisher()
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Binding is not used in this service
        return null
    }

    private fun startPublishJoy(domainId: Int, ns: String, period: Long, axes: FloatArray, buttons: IntArray) {
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
        runCatching { publishJoyTimer.purge() }
    }

    private fun startForegroundServiceWithNotification() {
        // Intent to stop the service from the notification
        val stopIntent = Intent(this, JoyPublisherService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }

        val stopPendingIntent: PendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_IMMUTABLE
            else
                0
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Joystick Publisher")
            .setContentText("Publishing joystick data...")
            .setSmallIcon(R.drawable.utmrbc_logo) // Replace with your app's icon
            .setOngoing(true) // Prevents user from swiping away the notification
            .addAction(R.drawable.utmrbc_logo, "Stop", stopPendingIntent) // Add a stop button
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Joystick Publisher Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    // Native methods
    private external fun createJoyPublisher(domainId: Int, ns: String)
    private external fun destroyJoyPublisher()
    private external fun publishJoy(axes: FloatArray, buttons: IntArray)

}
