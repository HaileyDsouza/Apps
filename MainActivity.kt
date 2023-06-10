package com.example.pedometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity(), SensorEventListener {
    //implement all members in the class mainActivity


    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()
        resetSteps()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        running = true

        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null){
            //error message if no sensor in device
            Toast.makeText(this, "No sensor is detected on this device", Toast.LENGTH_SHORT).show()
            } else{
                sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val stepsTaken = findViewById<TextView>(R.id.stepsTaken)

        if (running){
            totalSteps = event!!.values[0]
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            stepsTaken.text = ("$currentSteps")
        }
    }
    private fun resetSteps(){
        val stepsTaken = findViewById<TextView>(R.id.stepsTaken)
        stepsTaken.setOnClickListener {
            //reset steps
            Toast.makeText(this, "hold to reset steps", Toast.LENGTH_SHORT).show()
        }
        stepsTaken.setOnLongClickListener{
            previousTotalSteps = totalSteps
            stepsTaken.text = 0.toString()
            saveData()
            true
        }
    }


    private fun saveData(){
        //save data
        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadData(){
        // retrieves data
        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}