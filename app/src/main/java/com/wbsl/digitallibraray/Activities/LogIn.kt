package com.wbsl.digitallibraray.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wbsl.digitallibraray.R
import java.util.regex.Pattern

class LogIn : AppCompatActivity() {

    private lateinit var sendOTP: Button
    private lateinit var phone: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        sendOTP = findViewById(R.id.sendOTP)
        phone = findViewById(R.id.phone)
        progressBar = findViewById(R.id.progressbarforotp)

        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        sendOTP.setOnClickListener {

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val phoneNumber = phone.text.toString()
            if (isValidPhoneNumber(phoneNumber)) {
                progressBar.visibility= View.VISIBLE
                sendOTP.visibility= View.GONE
                val countryCode = "+91"
                val formattedPhoneNumber = "$countryCode$phoneNumber"

                // Check if user exists in database
                checkUserExists(formattedPhoneNumber)

            } else {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val pattern = Pattern.compile("^[0-9]{10}\$")
        return pattern.matcher(phoneNumber).matches()
    }

    private fun checkUserExists(phoneNumber: String) {
        databaseReference.child(phoneNumber).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // User exists, send verification code
                    sendVerificationCode(phoneNumber)
                } else {
                    // User does not exist, display error message
                    progressBar.visibility = View.GONE
                    sendOTP.visibility = View.VISIBLE

                    // User does not exists, display alert dialog
                    AlertDialog.Builder(this@LogIn)
                        .setTitle("Account not found")
                        .setMessage("This phone number is not registered. Please sign up first.")
                        .setPositiveButton("Sign up") { dialog, _ ->
                            finish()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                sendOTP.visibility = View.VISIBLE
                Toast.makeText(this@LogIn, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
                    progressBar.visibility= View.GONE
                    sendOTP.visibility= View.VISIBLE

                    val intent = Intent(this@LogIn, VerifyOtp::class.java)
                    intent.putExtra("verificationId", verificationId)
                    startActivity(intent)
                }

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    Toast.makeText(
                        this@LogIn,
                        "Verification failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

}