package jp.eyrin.rosjoydroid

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import jp.eyrin.rosjoydroid.ui.theme.ROSJoyDroidTheme
import kotlin.math.max
import java.util.Timer
import java.util.TimerTask

class MainActivity : GamepadActivity() {

    private lateinit var publishJoyTimer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getPreferences(Context.MODE_PRIVATE)

        setContent {

            var domainId by remember { prefs.mutableStateOf("domainId", 0) }
            var ns by remember { prefs.mutableStateOf("ns", "") }
            var period by remember { prefs.mutableStateOf("period", 2L) }
            var deadZone by remember { prefs.mutableStateOf("deadZone", 0.05f) }

            // Update deadZone in GamepadActivity whenever it changes
            LaunchedEffect(deadZone) {
                super.deadZone = deadZone
            }

            LifecycleResumeEffect(domainId, ns, period) {
                if (ns != null) {
//                    startPublishJoy(domainId, ns, period)
                    startJoyPublisherService(domainId, ns, period, deadZone, axes, buttons)
                }
                onPauseOrDispose {
//                    stopJoystickPublisherService()
//                    stopPublishJoy()
                }
            }

            ROSJoyDroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "UTMRBC Controller App",
                            fontSize = 32.sp
                        )
                        OutlinedTextField(
                            modifier = Modifier.width(400.dp),
                            value = ns,
                            onValueChange = { ns = it },
                            label = { Text("Namespace") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                        )
                        Row(
                            modifier = Modifier.width(400.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = domainId.toString(),
                                onValueChange = {
                                    domainId = (it.toIntOrNull() ?: 0).coerceIn(0, 101)
                                },
                                label = { Text("Domain ID") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                singleLine = true,
                            )
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = period.toString(),
                                onValueChange = {
                                    period = max(it.toLongOrNull() ?: 20, 1)
                                },
                                label = { Text("Period (ms)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                singleLine = true,
                            )
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = deadZone.toString(),
                                onValueChange = {
                                    deadZone = (it.toFloatOrNull() ?: 0.05f).coerceIn(0f, 1f)
                                },
                                label = { Text("Dead zone") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                            )
                        }

                        Row (
                            modifier = Modifier.width(400.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {


                            Button(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(50.dp),
                                onClick = {
                                    // Launch TouchScreenActivity with the current parameters
                                    val intent = Intent(
                                        applicationContext,
                                        TouchScreenActivity::class.java
                                    ).apply {
                                        putExtra("domainId", domainId)
                                        putExtra("ns", ns)
                                        putExtra("period", period)
                                        putExtra("deadZone", deadZone)
                                    }
                                    startActivity(intent)
                                    finish()
                                }
                            ) {
                                Text(text = "Game Field")
                            }

                            Button(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(50.dp),
                                onClick = {
                                    // Launch TouchScreenActivity with the current parameters
                                    val intent = Intent(
                                        applicationContext,
                                        ControlPanelActivity::class.java
                                    )
                                    startActivity(intent)
                                    finish()
                                }
                            ) {
                                Text(text = "Contorl Panel")
                            }
                        }
                    }
                }
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        // If needed, we can re-start publishing here, but LaunchedEffect in compose already handles changes
//    }
//
//    override fun onPause() {
//        super.onPause()
//        stopPublishJoy()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        stopPublishJoy()
//        destroyJoyPublisher()
//    }

    private fun startPublishJoy(domainId: Int, ns: String, period: Long) {
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
        // destroyJoyPublisher() called in onDestroy
    }

    private fun startJoyPublisherService(domainId: Int, ns: String, period: Long, deadZone: Float, axes : FloatArray, buttons: IntArray) {
        val serviceIntent = Intent(this, JoyPublisherService::class.java).apply {
            putExtra("domainId", domainId)
            putExtra("ns", ns)
            putExtra("period", period)
            putExtra("deadZone", deadZone)
            putExtra("axes", axes)
            putExtra("buttons", buttons)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun stopJoystickPublisherService() {
        val serviceIntent = Intent(this, JoyPublisherService::class.java)
        stopService(serviceIntent)
    }

    private external fun createJoyPublisher(domainId: Int, ns: String)
    private external fun destroyJoyPublisher()
    private external fun publishJoy(axes: FloatArray, buttons: IntArray)

    companion object {
        init {
            System.loadLibrary("rosjoydroid")
        }
    }
}