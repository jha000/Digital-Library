package com.wbsl.digitallibraray.Activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.chaos.view.PinView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wbsl.digitallibraray.R

class VerifyOtp : AppCompatActivity() {

    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var address: String
    private lateinit var address2: String
    private lateinit var state: String
    private lateinit var pincode: String
    private lateinit var verificationId: String
    private lateinit var progressbar: ProgressBar
    private lateinit var submitOTP: Button
    private lateinit var lottie: LottieAnimationView
    private lateinit var layoutOtp: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        // Retrieving data from intent
        val intent = intent
        name = intent.getStringExtra("name") ?: ""
        phone = intent.getStringExtra("phone") ?: ""
        address = intent.getStringExtra("address") ?: ""
        address2 = intent.getStringExtra("address2") ?: ""
        state = intent.getStringExtra("state") ?: ""
        pincode = intent.getStringExtra("pincode") ?: ""

        // Retrieving verificationId
        verificationId = intent.getStringExtra("verificationId") ?: ""

        val back = findViewById<ImageView>(R.id.back)

        back.setOnClickListener {
            super.onBackPressed()
        }

        // Finding views
        submitOTP = findViewById(R.id.submitOTP)
        val pinview = findViewById<PinView>(R.id.pinview)
        progressbar = findViewById(R.id.progressbar)

        layoutOtp = findViewById(R.id.layoutOtp)
        lottie = findViewById(R.id.lottie)



        pinview.requestFocus()

        submitOTP.setOnClickListener {
            // Hiding keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

            // Showing progress bar
            progressbar.visibility = View.VISIBLE
            submitOTP.visibility = View.GONE

            // Getting entered OTP
            val enteredOtp = pinview.text.toString()
            verifyOTPWithFirebase(enteredOtp)
        }

    }

    private fun verifyOTPWithFirebase(enteredOtp: String) {
        val credential: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(verificationId, enteredOtp)

        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val userNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber ?: ""
                    val userRef = FirebaseDatabase.getInstance().getReference("Users")

                    // Check if user phone number already exists in the database
                    userRef.child(userNumber).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                // User phone number already exists, no need to update details
                                // Launch Dashboard activity directly
                                val i = Intent(this@VerifyOtp, Dashboard::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(i)
                                finish()
                            } else {
                                // User phone number does not exist, update details and launch Dashboard activity
                                layoutOtp.visibility = View.GONE
                                lottie.visibility = View.VISIBLE

                                val userMap = HashMap<String, String>()
                                userMap["name"] = name
                                userMap["phone"] = phone
                                userMap["address"] = address
                                userMap["address2"] = address2
                                userMap["state"] = state
                                userMap["pincode"] = pincode

                                userRef.child(userNumber).setValue(userMap)
                                    .addOnSuccessListener {
                                        // Launch Dashboard activity after saving details
                                        lottie.addAnimatorListener(object : AnimatorListenerAdapter() {
                                            override fun onAnimationEnd(animation: Animator) {
                                                val i = Intent(this@VerifyOtp, Dashboard::class.java)
                                                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                startActivity(i)
                                                finish()
                                            }
                                        })
                                    }
                                    .addOnFailureListener { e ->
                                        // Handle the error
                                        Toast.makeText(
                                            this@VerifyOtp,
                                            "Failed to save user details: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })

                } else {
                    // Show error message for invalid OTP
                    Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }



}