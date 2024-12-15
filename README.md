# AgriLens

AgriLens adalah aplikasi berbasis Android yang dikembangkan sebagai bagian dari tugas Capstone untuk Studi Independen Bangkit Akademi 2024. Aplikasi ini bertujuan untuk mendeteksi penyakit pada tanaman menggunakan gambar dengan model Machine Learning berbasis TensorFlow Lite. Proyek ini juga merupakan salah satu syarat kelulusan program Bangkit.

## Tujuan Proyek
Proyek ini bertujuan untuk:
1. Membantu petani dan pengguna umum dalam mendeteksi penyakit tanaman secara cepat dan akurat melalui gambar.
2. Memberikan panduan perawatan tanaman berdasarkan penyakit yang terdeteksi.
3. Menyediakan pengalaman pengguna yang mudah dan praktis melalui fitur bookmark dan integrasi offline.

## Fitur Utama
- **Deteksi Penyakit Tanaman**: Menggunakan TensorFlow Lite untuk menganalisis gambar tanaman dan mendeteksi penyakit.
- **Fitur Bookmark**: Pengguna dapat menyimpan hasil deteksi dan nanti akan langsung terlihat di home page.
- **UI/UX Modern**: Aplikasi dirancang dengan antarmuka yang ramah pengguna untuk memberikan pengalaman terbaik.

## Teknologi yang Digunakan
- **Android Development**: Kotlin
- **Machine Learning**: TensorFlow Lite
- **Cloud API**: Integrasi API untuk mendukung fitur deteksi berbasis cloud

## Cara Kerja
1. Pengguna mengunggah gambar tanaman melalui aplikasi.
2. Gambar dianalisis menggunakan model Machine Learning.
3. Hasil deteksi berupa nama penyakit dan rekomendasi perawatan akan ditampilkan.
4. Pengguna dapat menyimpan hasil tersebut ke dalam fitur bookmark.

## Skema Fallback Mode
- Ketika API mengalami gangguan, aplikasi memungkinkan pengguna untuk langsung mengakses fitur utama (deteksi penyakit tanaman) tanpa harus melalui proses login.

## Cara Menjalankan Aplikasi
1. Clone repository ini ke komputer Anda.
2. Buka proyek di Android Studio.
3. Sync Gradle dan jalankan aplikasi di emulator atau perangkat fisik.
4. Jika API tidak aktif, aplikasi akan otomatis beralih ke mode offline.

## Tim Pengembang
- **Muhammad Luthfi** (Mobile Development)
- **Annisa Shafa Brilianty Lebeharia** (Machine Learning)
- **Farell Hafidz Irkhami** (Machine Learning)
- **Sinta Ayu Dwi Ardita** (Machine Learning)
- **Nabilah Salwa Salsabila** (Cloud Computing)
- **Nur Ahmad Siroj Rohmatillah** (Cloud Computing)
- **Yusuf Kelvin Siregar** (Mobile Development)

## Informasi pentiung

jika server pada API down maka gunakan kodingan MainActivity berikut, lalu run pada MainActivty, untuk sekedar mengakses fitur utama.


## package com.example.capstone

    import android.os.Bundle
    import android.view.View
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.navigation.NavController
    import androidx.navigation.fragment.NavHostFragment
    import androidx.navigation.ui.setupWithNavController
    import com.example.capstone.databinding.ActivityMainBinding
    import timber.log.Timber
    
    class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
    
            // Inisialisasi binding
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
    
            // Setup Navigation
            setupNavigation()
    
            // Handle BottomNavigation actions
            handleBottomNavigation()
            showLoading(false)
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
                    else -> false
                }
            }
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







