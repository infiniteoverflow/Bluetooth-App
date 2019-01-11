package com.bluetoothapp.aswin.bluetoothapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView

import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    internal lateinit var listView: ListView
    internal lateinit var statusTextView: TextView
    internal lateinit var searchButton: Button
    internal lateinit var cancelButton: Button
    internal var bluetoothDevices = ArrayList<String>()
    internal var addresses = ArrayList<String>()
    internal lateinit var arrayAdapter: ArrayAdapter<*>


    internal lateinit var bluetoothAdapter: BluetoothAdapter

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                statusTextView.text = "Finished"
                cancelButton.isEnabled = false
                searchButton.isEnabled = true
            } else if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val name = device.name
                val address = device.address
                val rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, java.lang.Short.MIN_VALUE).toInt())

                if (!addresses.contains(address)) {
                    addresses.add(address)
                    var deviceString = ""
                    if (name == null || name == "") {
                        deviceString = address + " - RSSI " + rssi + "dBm"
                    } else {
                        deviceString = name + " - RSSI " + rssi + "dBm"
                    }

                    bluetoothDevices.add(deviceString)
                    arrayAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    fun searchDevices(view: View) {
        statusTextView.text = "Searching..."
        searchButton.isEnabled = false
        cancelButton.isEnabled = true
        bluetoothDevices.clear()
        addresses.clear()
        bluetoothAdapter.startDiscovery()
    }

    fun cancelSearching(view: View) {
        statusTextView.text = "Cancelled"
        searchButton.isEnabled = true
        cancelButton.isEnabled = false
        bluetoothAdapter.cancelDiscovery()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.devicesListView)
        statusTextView = findViewById(R.id.textView)
        searchButton = findViewById(R.id.searchButton)
        cancelButton = findViewById(R.id.cancelButton)

        cancelButton.isEnabled = false

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bluetoothDevices)

        listView.adapter = arrayAdapter

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(broadcastReceiver, intentFilter)


    }
}