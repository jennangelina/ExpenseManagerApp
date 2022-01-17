package com.example.uas_c14190099

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val _bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavView)
        val _navController = findNavController(R.id.fragmentContainer)
        _bottomNavView.setBackground(null)
        _bottomNavView.setupWithNavController(_navController)

        val _addBtn = findViewById<FloatingActionButton>(R.id.addBtn)

        _addBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, addTransactionAct::class.java)
            startActivity(intent)
        }
    }
}