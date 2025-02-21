package jp.eyrin.rosjoydroid

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.SizeF
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.FontScaling
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.LifecycleResumeEffect
import jp.eyrin.rosjoydroid.ui.theme.ROSJoyDroidTheme
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Timer
import java.util.TimerTask

class ControlPanelActivity : GamepadActivity() {

    private lateinit var publishControlTimer: Timer

    var variables = FloatArray(4)
    var flags = IntArray(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getPreferences(Context.MODE_PRIVATE)

        val domainId = 0
        val ns  = ""
        val period = 2L
        val deadZone = 0.05f

        setContent {

            LifecycleResumeEffect(domainId, ns, period) {
                if (ns != null) {
                    startPublishControl(domainId, ns, period)
                }
                onPauseOrDispose {
                    stopPublishControl()
                }
            }

            ROSJoyDroidTheme {
                ControlPanelScreen(prefs, variables, flags)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // If needed, we can re-start publishing here, but LaunchedEffect in compose already handles changes
        startPublishControl(0, "", 2L)
    }

    override fun onPause() {
        super.onPause()
        stopPublishControl()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPublishControl()
        destroyControlPublisher()
    }

    private fun startPublishControl(domainId: Int, ns: String, period: Long) {
        stopPublishControl()
        createControlPublisher(domainId, ns)
        publishControlTimer = Timer().also {
            it.schedule(object : TimerTask() {
                override fun run() {
                    publishControl(variables, flags)
                }
            }, 0, period)
        }
    }

    private fun stopPublishControl() {
        runCatching { publishControlTimer.cancel() }
        runCatching { publishControlTimer.purge() }
        // destroyJoyPublisher() called in onDestroy
    }

    private external fun createControlPublisher(domainId: Int, ns: String)
    private external fun destroyControlPublisher()
    private external fun publishControl(axes: FloatArray, buttons: IntArray)

}


@Composable
fun ControlPanelScreen(prefs : android. content. SharedPreferences, variables : FloatArray, flags : IntArray) {

    val context = LocalContext.current

    var showSettings by remember { mutableStateOf(false) }

    var incrementValue1 by remember { prefs.mutableStateOf("incrementValue1", 1) }
    var incrementValue2 by remember { prefs.mutableStateOf("incrementValue2", 1) }
    var incrementValue3 by remember { prefs.mutableStateOf("incrementValue3", 1) }
    var incrementValue4 by remember { prefs.mutableStateOf("incrementValue4", 1) }

    var value1 by remember { prefs.mutableStateOf("value1", 0)}
    var value2 by remember { prefs.mutableStateOf("value2", 0)}
    var value3 by remember { prefs.mutableStateOf("value3", 0)}
    var value4 by remember { prefs.mutableStateOf("value4", 0)}

    var flagU by remember { prefs.mutableStateOf("flagU", 0)}
    var flagT by remember { prefs.mutableStateOf("flagT", 0)}
    var flagM by remember { prefs.mutableStateOf("flagM", 0)}
    var flagR by remember { prefs.mutableStateOf("flagR", 0)}
    var flagB by remember { prefs.mutableStateOf("flagB", 0)}
    var flagC by remember { prefs.mutableStateOf("flagC", 0)}

    var label1 by remember { prefs.mutableStateOf("label1", "value1")}
    var label2 by remember { prefs.mutableStateOf("label2", "value2")}
    var label3 by remember { prefs.mutableStateOf("label3", "value3")}
    var label4 by remember { prefs.mutableStateOf("label4", "value4")}

    variables[0] = value1.toFloat()
    variables[1] = value2.toFloat()
    variables[2] = value3.toFloat()
    variables[3] = value4.toFloat()

//    flags[0] = flagU.toInt()

    flags[0] = (flagU and 1) or
            ((flagT and 1) shl 1) or
            ((flagM and 1) shl 2) or
            ((flagR and 1) shl 3) or
            ((flagB and 1) shl 4) or
            ((flagC and 1) shl 5)

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column (
            modifier = Modifier
                .padding(16.dp),
        ){

            IconButton(
                onClick = { showSettings = true },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (showSettings) {
                AlertDialog(
                    onDismissRequest = { showSettings = false },
                    title = { Text("Settings") },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                        ) {

                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ){
                                OutlinedTextField(
                                    value = label1,
                                    onValueChange = { label1 = it },
                                    label = { Text("Label") },
                                    modifier = Modifier.width(160.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))

                                OutlinedTextField(
                                    value = incrementValue1.toString(),
                                    onValueChange = {
                                        incrementValue1 = it.toIntOrNull() ?: incrementValue1
                                    },
                                    label = { Text("Inc") },
                                    modifier = Modifier.width(80.dp),
                                )
                            }
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ){
                                OutlinedTextField(
                                    value = label2,
                                    onValueChange = { label2 = it },
                                    label = { Text("Label") },
                                    modifier = Modifier.width(160.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))

                                OutlinedTextField(
                                    value = incrementValue2.toString(),
                                    onValueChange = {
                                        incrementValue2 = it.toIntOrNull() ?: incrementValue2
                                    },
                                    label = { Text("Inc") },
                                    modifier = Modifier.width(80.dp),
                                )
                            }
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ){
                                OutlinedTextField(
                                    value = label3,
                                    onValueChange = { label3 = it },
                                    label = { Text("Label") },
                                    modifier = Modifier.width(160.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))

                                OutlinedTextField(
                                    value = incrementValue3.toString(),
                                    onValueChange = {
                                        incrementValue3 = it.toIntOrNull() ?: incrementValue3
                                    },
                                    label = { Text("Inc") },
                                    modifier = Modifier.width(80.dp),
                                )
                            }
                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ){
                                OutlinedTextField(
                                    value = label4,
                                    onValueChange = { label4 = it },
                                    label = { Text("Label") },
                                    modifier = Modifier.width(160.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))

                                OutlinedTextField(
                                    value = incrementValue4.toString(),
                                    onValueChange = {
                                        incrementValue4 = it.toIntOrNull() ?: incrementValue4
                                    },
                                    label = { Text("Inc") },
                                    modifier = Modifier.width(80.dp),
                                )
                            }

                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showSettings = false }
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showSettings = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FlagButton(
                text = "U",
                flag = flagU,
                onToggle = { flagU = if (flagU == 1) 0 else 1 }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FlagButton(
                text = "T",
                flag = flagT,
                onToggle = { flagT = if (flagT == 1) 0 else 1 }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FlagButton(
                text = "M",
                flag = flagM,
                onToggle = { flagM = if (flagM == 1) 0 else 1 }
            )
        }

        Column {

            Spacer(modifier = Modifier.height(40.dp))

            Row {
                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    ValueSection(
                        label = label1,
                        value = value1,
                        direction = "left",
                        onDecrement = { value1 -= incrementValue1 },
                        onIncrement = { value1 += incrementValue1 }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ValueSection(
                        label = label3,
                        value = value3,
                        direction = "left",
                        onDecrement = { value3 -= incrementValue3 },
                        onIncrement = { value3 += incrementValue3 }
                    )

                }

                Column(
                    modifier = Modifier
                        .padding(5.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    ValueSection(
                        label = label2,
                        value = value2,
                        direction = "right",
                        onDecrement = { value2 -= incrementValue2 },
                        onIncrement = { value2 += incrementValue2 }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ValueSection(
                        label = label4,
                        value = value4,
                        direction = "right",
                        onDecrement = { value4 -= incrementValue4 },
                        onIncrement = { value4 += incrementValue4 }
                    )
                }
            }

        }

        Column (
            modifier = Modifier
                .padding(16.dp),
        ){
            IconButton(
                onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    (context as? Activity)?.finish()
                },
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FlagButton(
                text = "R",
                flag = flagR,
                onToggle = { flagR = if (flagR == 1) 0 else 1 }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FlagButton(
                text = "B",
                flag = flagB,
                onToggle = { flagB = if (flagB == 1) 0 else 1 }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FlagButton(
                text = "C",
                flag = flagC,
                onToggle = { flagC = if (flagC == 1) 0 else 1 }
            )
        }

    }

}

@Composable
fun FlagButton(text: String, flag: Int, onToggle: () -> Unit) {
    Button(
        onClick = onToggle,
        modifier = Modifier
            .width(60.dp)
            .height(60.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (flag == 1) MaterialTheme.colorScheme.primary else Color.Gray
        )
    ) {
        Text(
            text = text,
            color = Color.White,
        )
    }
}


@Composable
fun ValueSection(
    label: String,
    value: Int,
    direction: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {

    if (direction == "left"){

        Row {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = onIncrement,
                    modifier = Modifier
                        .width(80.dp)
                        .height(60.dp)
                        .padding(4.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "▲",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onDecrement,
                    modifier = Modifier
                        .width(80.dp)
                        .height(60.dp)
                        .padding(4.dp),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = "▼",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }

            }

            Column {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(120.dp)
                        .padding(4.dp)
                        .background(
                            Color.LightGray,
                            shape = RoundedCornerShape(10.dp),
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = label,
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    // Value at the Center
                    Text(
                        text = "$value",
                        color = Color.DarkGray,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    else if (direction == "right"){

        Row {
            Column {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(120.dp)
                        .padding(4.dp)
                        .background(
                            Color.LightGray,
                            shape = RoundedCornerShape(10.dp),
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Label at the Top Center
                    Text(
                        text = label,
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    // Value at the Center
                    Text(
                        text = "$value",
                        color = Color.DarkGray,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    onClick = onIncrement,
                    modifier = Modifier
                        .width(80.dp)
                        .height(60.dp)
                        .padding(4.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "▲",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onDecrement,
                    modifier = Modifier
                        .width(80.dp)
                        .height(60.dp)
                        .padding(4.dp),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(
                        text = "▼",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }

            }

        }
    }

}