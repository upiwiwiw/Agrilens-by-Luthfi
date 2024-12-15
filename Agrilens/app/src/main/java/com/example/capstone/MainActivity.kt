package com.example.capstone

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.ui.auth.LoginActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek status login sebelum melanjutkan
        showLoading(true)
        checkLoginStatus()

        // Setup Navigation
        setupNavigation()

        // Handle BottomNavigation actions
        handleBottomNavigation()
        showLoading(false)
    }

    private fun checkLoginStatus() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (!isLoggedIn) {
            // Jika belum login, arahkan ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupNavigation() {
        // Initialize NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        if (navHostFragment != null) {
            navController = navHostFragment.navController

            // Link BottomNavigationView with NavController
            binding.bottomNavigation.setupWithNavController(navController)
        } else {
            // Log error if NavHostFragment is not found
            Timber.tag("MainActivity").e("NavHostFragment not found.")
            Toast.makeText(this, "Navigation error occurred.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    if (navController.currentDestination?.id != R.id.homeFragment) {
                        // Navigate to HomeFragment only if not already on it
                        showLoading(true)
                        navController.navigate(R.id.homeFragment)
                        showLoading(false)
                    }
                    true
                }
                R.id.scannerFragment -> {
                    if (navController.currentDestination?.id != R.id.scannerFragment) {
                        // Navigate to ScannerFragment only if not already on it
                        showLoading(true)
                        navController.navigate(R.id.scannerFragment)
                        showLoading(false)
                    }
                    true
                }
                R.id.menu_logout -> {
                    // Confirm before logout
                    logoutWithConfirmation()
                    true
                }
                else -> false
            }
        }
    }

    private fun logoutWithConfirmation() {
        // Show confirmation dialog
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ -> performLogout() }
            .setNegativeButton("No", null)
            .create()
        dialog.show()
    }

    @Suppress("DEPRECATION")
    private fun performLogout() {
        // Logout logic using SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("is_logged_in", false).apply()

        // Show a logout confirmation toast
        Toast.makeText(this, "You have successfully logged out.", Toast.LENGTH_SHORT).show()

        // Navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        if (::binding.isInitialized) {
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.loadingOverlay.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.loadingOverlay.visibility = View.GONE
            }
        } else {
            Timber.e("Binding not initialized")
        }
    }
}