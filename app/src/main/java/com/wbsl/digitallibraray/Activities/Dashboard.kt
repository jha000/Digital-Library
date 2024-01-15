package com.wbsl.digitallibraray.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wbsl.digitallibraray.Fragments.CatalogueFragment
import com.wbsl.digitallibraray.Fragments.profileFragment
import com.wbsl.digitallibraray.R
import com.wbsl.digitallibrary.Fragments.HomeFragment

class Dashboard : AppCompatActivity() {
    var bottom_navigation: BottomNavigationView? = null
    var HomeFragment = HomeFragment()
    var ProfileFragment = profileFragment()
    var CatalogueFragment = CatalogueFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        bottom_navigation = findViewById(R.id.bottom_navigation)
        supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment).commit()
        bottom_navigation!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, ProfileFragment).commit()


                    return@OnNavigationItemSelectedListener true
                }
                R.id.catalogue -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, CatalogueFragment).commit()

                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    override fun onBackPressed() {
        val currentFragment =
            this.supportFragmentManager.findFragmentById(R.id.container)
        if (currentFragment is profileFragment || currentFragment is CatalogueFragment) {
            supportFragmentManager.beginTransaction()
                .apply { replace(R.id.container, HomeFragment) .commit() }
        } else {
            super.onBackPressed()
        }
    }


}