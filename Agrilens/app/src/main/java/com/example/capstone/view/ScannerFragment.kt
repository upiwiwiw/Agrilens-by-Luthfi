package com.example.capstone.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.capstone.R
import com.example.capstone.databinding.FragmentScannerBinding
import com.example.capstone.model.ClassificationResult
import com.example.capstone.model.ImageClassifierHelper
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ScannerFragment : Fragment(), ImageClassifierHelper.ClassifierListener {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var selectedImageUri: Uri? = null

    // ActivityResult API untuk menggantikan startActivityForResult
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            try {
                // Copy URI ke penyimpanan internal
                val safeUri = copyUriToInternalStorage(uri) ?: uri

                // Tampilkan gambar pada ImageView
                val bitmap = loadBitmapFromUri(safeUri)
                if (bitmap != null) {
                    binding.ivScanImage.setImageBitmap(bitmap)
                    binding.btnAnalyze.isEnabled = true
                } else {
                    Toast.makeText(requireContext(), "Failed to load image.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Timber.e("Error loading image: ${e.message}")
                Toast.makeText(requireContext(), "Error loading image.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi TensorFlow Lite Classifier
        imageClassifierHelper = ImageClassifierHelper(requireContext())

        // Tombol untuk membuka galeri
        binding.btnOpenGallery.setOnClickListener { openGallery() }

        // Tombol untuk memulai analisis
        binding.btnAnalyze.setOnClickListener { analyzeImage() }

        // Nonaktifkan tombol analisis hingga gambar dipilih
        binding.btnAnalyze.isEnabled = false
    }

    private fun openGallery() {
        // Luncurkan galeri menggunakan ActivityResult API
        galleryLauncher.launch("image/*")
    }

    private fun analyzeImage() {
        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please choose an image first", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Salin URI ke penyimpanan internal
            val safeUri = copyUriToInternalStorage(selectedImageUri!!) ?: selectedImageUri!!

            // Konversi URI ke Bitmap
            val bitmap = loadBitmapFromUri(safeUri)

            // Kirim gambar ke TensorFlow Lite untuk analisis
            if (bitmap != null) {
                imageClassifierHelper.classifyBitmap(bitmap, this)
            } else {
                Toast.makeText(requireContext(), "Error processing image.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Timber.e("Error analyzing image: ${e.message}")
            Toast.makeText(requireContext(), "Error analyzing image.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Timber.e("Error loading bitmap: ${e.message}")
            null
        }
    }

    private fun copyUriToInternalStorage(uri: Uri): Uri? {
        return try {
            val fileName = getFileNameFromUri(uri)
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file)
        } catch (e: Exception) {
            Timber.e("Error copying URI to internal storage: ${e.message}")
            null
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var fileName: String? = null
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex != -1) {
                    fileName = it.getString(columnIndex)
                } else {
                    Timber.e("DISPLAY_NAME column not found.")
                }
            }
        }
        return fileName ?: "unknown_file"
    }

    override fun onResult(result: ClassificationResult) {
        val bundle = Bundle().apply {
            putString("RESULT_TITLE", result.title)
            putString("RESULT_TREATMENT", result.treatment)
            putString("DIAGNOSIS", result.diagnosis)
            putFloat("CONFIDENCE", result.confidence)
            putString("IMAGE_URI", selectedImageUri.toString())
        }
        findNavController().navigate(R.id.action_scannerFragment_to_resultFragment, bundle)
    }

    override fun onError(error: String) {
        Timber.e("Classification error: $error")
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}