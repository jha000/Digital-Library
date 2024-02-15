package com.wbsl.digitallibraray.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.wbsl.digitallibraray.LogIn
import com.wbsl.digitallibraray.R
import com.wbsl.digitallibraray.Registration

class profileFragment : Fragment() {

    private lateinit var signUp: TextView
    private lateinit var logIn: TextView
    private lateinit var logout: TextView
    private lateinit var authLayout: LinearLayout
    private lateinit var profileLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        signUp = view.findViewById(R.id.signUp)
        logIn = view.findViewById(R.id.logIn)
        logout = view.findViewById(R.id.logout)
        authLayout = view.findViewById(R.id.authLayout)
        profileLayout = view.findViewById(R.id.profileLayout)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // User is already logged in
            profileLayout.visibility = View.VISIBLE
            authLayout.visibility = View.GONE

        } else {
            // User is not logged in, show sign up or login options
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
            FirebaseAuth.getInstance().signOut()

            profileLayout.visibility = View.GONE
            authLayout.visibility = View.VISIBLE

        }

        return view
    }

}