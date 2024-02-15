package com.wbsl.digitallibraray

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.io.IOException
import java.util.Locale

class Registration : AppCompatActivity() {

    private lateinit var manually: TextView
    private lateinit var current: TextView
    private lateinit var name: EditText
    private lateinit var phone: EditText
    private lateinit var address: EditText
    private lateinit var address2: EditText
    private lateinit var state: EditText
    private lateinit var pincode: EditText
    private lateinit var save: Button
    private lateinit var progressbar: ProgressBar
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var firebaseAuth: FirebaseAuth
    private val REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        firebaseAuth = FirebaseAuth.getInstance()

        val back = findViewById<ImageView>(R.id.back)

        back.setOnClickListener {
            super.onBackPressed()
        }


        name = findViewById(R.id.name)
        phone = findViewById(R.id.phone)
        save = findViewById(R.id.save)
        progressbar = findViewById(R.id.progressbar)

        current = findViewById(R.id.current)
        manually = findViewById(R.id.manually)
        address = findViewById(R.id.Address)
        address2 = findViewById(R.id.Address2)
        state = findViewById(R.id.State)
        pincode = findViewById(R.id.Pincode)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        current.setOnClickListener {
            getLastLocation()
        }

        manually.setOnClickListener {
            address.text.clear()
            state.text.clear()
            pincode.text.clear()
        }


        save.setOnClickListener {

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)


            // Check if all mandatory fields are empty
            if (name.text.isEmpty()) {
                name.error = "Name is required"
                name.requestFocus()
                return@setOnClickListener
            }
            if (address.text.isEmpty()) {
                address.error = "Address is required"
                address.requestFocus()
                return@setOnClickListener
            }
            if (state.text.isEmpty()) {
                state.error = "State is required"
                state.requestFocus()
                return@setOnClickListener
            }
            if (pincode.text.isEmpty()) {
                pincode.error = "Pincode is required"
                pincode.requestFocus()
                return@setOnClickListener
            }
            if (phone.text.isEmpty()) {
                phone.error = "Phone number is required"
                phone.requestFocus()
                return@setOnClickListener
            }

            val phoneNumberRegex = Regex("\\d{10}")
            val phoneNumber = phone.text.toString()
            val countryCode = "+91"
            val formattedPhoneNumber = "$countryCode$phoneNumber"
            if (phoneNumber.matches(phoneNumberRegex)) {

                name.clearFocus()
                phone.clearFocus()
                address.clearFocus()
                address2.clearFocus()
                state.clearFocus()
                pincode.clearFocus()


                progressbar.visibility = View.VISIBLE
                save.visibility = View.GONE
                sendVerificationCode(formattedPhoneNumber)
            } else {
                phone.error = "Enter a valid 10-digit phone number"
                phone.requestFocus()
            }
        }


    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun sendVerificationCode(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            java.util.concurrent.TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    val intent = Intent(this@Registration, VerifyOtp::class.java)
                    intent.putExtra("name", name.text.toString())
                    intent.putExtra("phone", phone.text.toString())
                    intent.putExtra("address", address.text.toString())
                    intent.putExtra("address2", address2.text.toString())
                    intent.putExtra("state", state.text.toString())
                    intent.putExtra("pincode", pincode.text.toString())
                    intent.putExtra("verificationId", verificationId)
                    startActivity(intent)
                    progressbar.visibility = View.GONE
                    save.visibility = View.VISIBLE
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                }

                override fun onVerificationFailed(exception: FirebaseException) {

                    progressbar.visibility = View.GONE
                    save.visibility = View.VISIBLE

                    Toast.makeText(
                        this@Registration,
                        "Verification failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
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
                showToast("Please provide the required permission")
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