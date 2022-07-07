package com.example.vve_mobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.vve_mobile.checkout.CheckoutsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar: androidx.appcompat.app.ActionBar? = supportActionBar
        actionBar?.hide()
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bindNavigationBar()
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment).commit()
        }

    private fun bindNavigationBar() {
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.checkouts -> setCurrentFragment(CheckoutsFragment())
                R.id.settings -> setCurrentFragment(SettingsFragment())
                R.id.log_in -> setCurrentFragment(LogInFragment())
            }
            true
        }
//        bottomNavigationView.selectedItemId = R.id.checkouts
        bottomNavigationView.selectedItemId = R.id.log_in
    }
}