package com.wbsl.digitallibraray.Fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.wbsl.digitallibraray.Activities.EditProfile
import com.wbsl.digitallibraray.Activities.LogIn
import com.wbsl.digitallibraray.Activities.Registration
import com.wbsl.digitallibraray.R
import java.io.ByteArrayOutputStream

class profileFragment : Fragment() {

    private lateinit var signUp: TextView
    private lateinit var logIn: TextView
    private lateinit var logout: TextView
    private lateinit var name: TextView
    private lateinit var username: TextView
    private lateinit var phone: TextView
    private lateinit var address: TextView
    private lateinit var pincode: TextView
    private lateinit var authLayout: LinearLayout
    private lateinit var profileLayout: LinearLayout
    private lateinit var editLayout: LinearLayout
    private lateinit var profileLoading: LottieAnimationView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var getState: String
    private lateinit var getAddress: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val cover = view.findViewById<View>(R.id.profile_image) as ImageView

        val sharedPreferences =
            requireActivity().getSharedPreferences("myKey", Context.MODE_PRIVATE)

        val base64: String? = sharedPreferences.getString("image", null)
        if (base64 != null && base64.isNotEmpty()) {
            val byteArray = Base64.decode(base64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            cover.setImageBitmap(bitmap)
        }

        cover.setOnClickListener {
            ImagePicker.with(this@profileFragment)
                .crop()
                .compress(1024)
                .maxResultSize(
                    1080,
                    1080
                )
                .start()
        }

        signUp = view.findViewById(R.id.signUp)
        logIn = view.findViewById(R.id.logIn)
        logout = view.findViewById(R.id.logout)
        authLayout = view.findViewById(R.id.authLayout)
        profileLayout = view.findViewById(R.id.profileLayout)
        editLayout = view.findViewById(R.id.editLayout)
        profileLoading = view.findViewById(R.id.lottieProfile)

        name = view.findViewById(R.id.name)
        username = view.findViewById(R.id.username)
        phone = view.findViewById(R.id.phone)
        pincode = view.findViewById(R.id.pincode)
        address = view.findViewById(R.id.address)

        editLayout.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfile::class.java)
            intent.putExtra("name", name.text.toString())
            intent.putExtra("address", getAddress)
            intent.putExtra("state", getState)
            intent.putExtra("pincode", pincode.text.toString())
            startActivity(intent)
        }

        databaseReference = FirebaseDatabase.getInstance().reference

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // User is already logged in

            // Retrieve user data from Firebase Realtime Database
            databaseReference.child("Users").child(currentUser.phoneNumber ?: "")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userMap = snapshot.value as? Map<*, *>?
                        userMap?.let {
                            name.text = it["name"] as? String ?: ""
                            username.text = it["name"] as? String ?: ""
                            phone.text = it["phone"] as? String ?: ""
                            pincode.text = it["pincode"] as? String ?: ""

                            val address1 = it["address"] as? String ?: ""
                            val address2 = it["address2"] as? String ?: ""
                            val state = it["state"] as? String ?: ""
                            getState = it["state"] as? String ?: ""

                            getAddress = if (address2.isNotEmpty()) {
                                "$address1, $address2"
                            } else {
                                address1
                            }

                            // Construct the address based on whether address2 is empty or not
                            address.text = if (address2.isNotEmpty()) {
                                "$address1, $address2, $state"
                            } else {
                                "$address1, $state"
                            }
                            profileLayout.visibility = View.VISIBLE
                            authLayout.visibility = View.GONE
                            profileLoading.visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })


        } else {
            // User is not logged in, show sign up or login options
            profileLoading.visibility = View.GONE
            profileLayout.visibility = View.GONE
            authLayout.visibility = View.VISIBLE

        }

        logIn.setOnClickListener {
            val intent = Intent(requireActivity(), LogIn::class.java)
            startActivity(intent)
        }

        signUp.setOnClickListener {
            val intent = Intent(requireActivity(), Registration::class.java)
            startActivity(intent)
        }

        logout.setOnClickListener {
            // Display AlertDialog to confirm logout
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { dialog, which ->
                    FirebaseAuth.getInstance().signOut()

                    profileLayout.visibility = View.GONE
                    authLayout.visibility = View.VISIBLE
                }
                .setNegativeButton("No", null)
                .show()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val selectedImg = data!!.data
            val cover = view?.findViewById<View>(R.id.profile_image) as ImageView
            cover.setImageURI(selectedImg)

            val drawable = cover.drawable
            if (drawable != null && drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                val base64 = Base64.encodeToString(byteArray, Base64.DEFAULT)

                val sharedPreferences = requireActivity().getSharedPreferences("myKey", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("image", base64)
                editor.apply()
            } else {
                // Handle the case where the drawable is null or not a BitmapDrawable
            }
        }
    }

}