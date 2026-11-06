package com.ricosuave141.a101575606_comp3074_exec06

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ricosuave141.a101575606_comp3074_exec06.ui.theme._101575606_COMP3074_exec06Theme
import android.content.Context 
import android.hardware.Sensor 
import android.hardware.SensorEvent 
import android.hardware.SensorEventListener 
import androidx.activity.compose.setContent 
import androidx.compose.foundation.layout.Arrangement 
import androidx.compose.foundation.layout.Column 
import androidx.compose.foundation.layout.Spacer 
import androidx.compose.foundation.layout.fillMaxSize 
import androidx.compose.foundation.layout.fillMaxWidth 
import androidx.compose.foundation.layout.height 
import androidx.compose.foundation.layout.padding 
import androidx.compose.material3.Card 
import androidx.compose.material3.MaterialTheme 
import androidx.compose.material3.Scaffold 
import androidx.compose.material3.Text 
import androidx.compose.runtime.getValue 
import androidx.compose.runtime.mutableStateOf 
import androidx.compose.runtime.setValue 
import androidx.compose.ui.unit.dp
import android.hardware.SensorManager
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.ColumnScope
import java.util.Locale

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var tempSensor: Sensor? = null
    private var gyroSensor: Sensor? = null
    private var temperature by mutableStateOf<Float?>(null)
    private var gyroX by mutableStateOf(0f)
    private var gyroY by mutableStateOf(0f)
    private var gyroZ by mutableStateOf(0f)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        setContent {
            MaterialTheme {
                SensorScreen(
                    temperature = temperature,
                    temperatureAvailable = tempSensor != null,
                    gyroX = gyroX,
                    gyroY = gyroY,
                    gyroZ = gyroZ,
                    gyroscopeAvailable = gyroSensor != null
                )
            }
        }
    }
    override fun onResume() {
        super.onResume()
        listOf(tempSensor, gyroSensor).forEach { sensor ->
            if (sensor != null) {
                sensorManager.registerListener(
                    this, sensor, SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
    }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    override fun onSensorChanged(event: SensorEvent?) {
        val e = event ?: return
        if (e.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            temperature = e.values[0]
        } else if (e.sensor.type == Sensor.TYPE_GYROSCOPE) {
            gyroX = e.values[0]
            gyroY = e.values[1]
            gyroZ = e.values[2]
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    @Composable
    fun SensorScreen(
        temperature: Float?,
        temperatureAvailable: Boolean,
        gyroX: Float,
        gyroY: Float,
        gyroZ: Float,
        gyroscopeAvailable: Boolean
    ) {
        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Sensor Dashboard",
                    style = MaterialTheme.typography.headlineMedium
                )

                SensorValueCard(
                    title = "Ambient Temperature",
                    available = temperatureAvailable,
                    unavailableMessage = "No ambient temperature sensor on this device."
                ) {
                    if (temperature == null) {
                        Text("Waiting for first reading...")
                    } else {
                        Text(
                            text = String.format(Locale.US, "%.1f °C", temperature),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                SensorValueCard(
                    title = "Gyroscope",
                    available = gyroscopeAvailable,
                    unavailableMessage = "No gyroscope on this device."
                ) {
                    val axes = listOf("X" to gyroX, "Y" to gyroY, "Z" to gyroZ)
                    axes.forEach { (name, value) ->
                        Text(String.format(Locale.US, "%s: %.3f rad/s", name, value))
                    }
                }
            }
        }
    }

    @Composable
    fun SensorValueCard(
        title: String,
        available: Boolean,
        unavailableMessage: String,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)

                if (available) {
                    content()
                } else {
                    Text(unavailableMessage)
                }
            }
        }
    }
}