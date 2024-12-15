package com.example.capstone.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.MainActivity
import com.example.capstone.databinding.ActivitySplashBinding
import com.example.capstone.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Simulasi delay splash screen
        binding.root.postDelayed({
            val sharedPref = getSharedPreferences("USER_PREF", MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("IS_LOGGED_IN", false) // Periksa status login

            if (isLoggedIn) {
                // Jika sudah login, arahkan ke MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Jika belum login, arahkan ke LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000) // Delay 2 detik
    }
}
