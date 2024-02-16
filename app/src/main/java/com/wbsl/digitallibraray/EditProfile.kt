package com.wbsl.digitallibraray

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.wbsl.digitallibraray.Activities.Dashboard
import java.io.IOException
import java.util.Locale

class EditProfile : AppCompatActivity() {

    private lateinit var getName: String
    private lateinit var getAddress: String
    private lateinit var getState: String
    private lateinit var getPincode: String
    private lateinit var name: EditText
    private lateinit var address: EditText
    private lateinit var address2: EditText
    private lateinit var state: EditText
    private lateinit var pincode: EditText
    private lateinit var save: Button
    private lateinit var back: ImageView
    private lateinit var current: TextView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val intent = intent
        getName = intent.getStringExtra("name") ?: ""
        getAddress = intent.getStringExtra("address") ?: ""
        getState = intent.getStringExtra("state") ?: ""
        getPincode = intent.getStringExtra("pincode") ?: ""

        name = findViewById(R.id.name)
        address = findViewById(R.id.Address)
        address2 = findViewById(R.id.Address2)
        state = findViewById(R.id.State)
        pincode = findViewById(R.id.Pincode)
        save = findViewById(R.id.save)
        back = findViewById(R.id.back)
        current = findViewById(R.id.current)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        name.setText(getName)
        address.setText(getAddress)
        state.setText(getState)
        pincode.setText(getPincode)

        back.setOnClickListener {
            super.onBackPressed()
        }

        current.setOnClickListener {
            getLastLocation()
        }

        save.setOnClickListener {

            val updatedName = name.text.toString()
            val updatedAddress = address.text.toString()
            val updatedAddress2 = address2.text.toString()
            val updatedState = state.text.toString()
            val updatedPincode = pincode.text.toString()

            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { user ->
                val userNumber = user.phoneNumber ?: ""
                val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userNumber)

                val updates = hashMapOf<String, Any>(
                    "name" to updatedName,
                    "address" to updatedAddress,
                    "address2" to updatedAddress2,
                    "state" to updatedState,
                    "pincode" to updatedPincode
                )

                userRef.updateChildren(updates)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this@EditProfile,
                            "Profile saved",
                            Toast.LENGTH_SHORT
                        ).show()
                        val i = Intent(this@EditProfile, Dashboard::class.java)
                        startActivity(i)
                        finish()
                    }
                    .addOnFailureListener {
                        // Handle failure
                    }
            }


        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(
                    this@EditProfile,
                    "Please provide the required permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getLastLocation() {
        val currentActivity = this

        if (currentActivity != null &&
            ActivityCompat.checkSelfPermission(
                currentActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        try {
                            val geocoder = Geocoder(currentActivity, Locale.getDefault())
                            val addresses: List<Address>? =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)

                            address.text = Editable.Factory.getInstance()
                                .newEditable(addresses!![0].subLocality + ", " + addresses[0].locality)
                            state.text =
                                Editable.Factory.getInstance().newEditable(addresses[0].adminArea)
                            pincode.text =
                                Editable.Factory.getInstance().newEditable(addresses[0].postalCode)

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
        } else {
            askPermission()
        }
    }


    private fun askPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }
}