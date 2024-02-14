package com.wbsl.digitallibraray.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.content.Intent
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.facebook.shimmer.ShimmerFrameLayout
import com.wbsl.digitallibraray.Activities.Authentication
import com.wbsl.digitallibraray.R

class profileFragment : Fragment() {

    private lateinit var shimmerLayout : ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        shimmerLayout = view.findViewById(R.id.shimmerLayout)

        shimmerLayout.startShimmer()

        val intent = Intent(requireActivity(), Authentication::class.java)
        startActivity(intent)

        return view
    }

}