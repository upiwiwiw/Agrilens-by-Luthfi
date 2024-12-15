package com.example.capstone.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.capstone.R
import com.example.capstone.databinding.FragmentResultBinding
import timber.log.Timber
import java.io.InputStream

class ResultFragment : Fragment() {

    private var isBookmarked = false
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)

        // Setup back press callback
        setupOnBackPressed()

        // Display the classification result
        displayResult()

        // Setup bookmark button
        setupBookmarkButton()

        return binding.root
    }

    private fun setupOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.popBackStack()
            }
        })
    }

    private fun displayResult() {
        val resultTitle = arguments?.getString("RESULT_TITLE") ?: "Unknown Issue"
        val diagnosis = arguments?.getString("DIAGNOSIS") ?: "Unknown Diagnosis"
        val resultTreatment = arguments?.getString("RESULT_TREATMENT") ?: "No treatment available."
        val imageUriString = arguments?.getString("IMAGE_URI")

        binding.tvResultTitle.text = resultTitle
        binding.tvDiagnosis.text = diagnosis
        binding.tvResultDescription.text = resultTreatment

        // Load and display image
        if (!imageUriString.isNullOrEmpty()) {
            val imageUri = Uri.parse(imageUriString)
            val bitmap = loadBitmapFromUri(imageUri)
            if (bitmap != null) {
                binding.ivScanImage.setImageBitmap(bitmap)
            } else {
                binding.ivScanImage.setImageResource(R.drawable.ic_placeholder_image)
                Toast.makeText(requireContext(), "Failed to load image.", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.ivScanImage.setImageResource(R.drawable.ic_placeholder_image)
        }

        // Update bookmark state
        isBookmarked = checkIfBookmarked(resultTitle, diagnosis)
        updateBookmarkButton()
    }

    private fun setupBookmarkButton() {
        binding.btnBookmark.setOnClickListener {
            val resultTitle = binding.tvResultTitle.text.toString()
            val diagnosis = binding.tvDiagnosis.text.toString()
            val imageUri = arguments?.getString("IMAGE_URI") ?: ""
            val treatment = arguments?.getString("RESULT_TREATMENT") ?: ""

            if (isBookmarked) {
                removeFromBookmark(resultTitle, diagnosis)
            } else {
                saveToBookmark(resultTitle, diagnosis, imageUri, treatment)
            }
            isBookmarked = !isBookmarked
            updateBookmarkButton()
        }
    }

    @SuppressLint("MutatingSharedPrefs")
    private fun saveToBookmark(title: String, diagnose: String, imageUri: String, treatment: String) {
        val sharedPreferences = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val bookmarkSet = sharedPreferences.getStringSet("bookmark_list", mutableSetOf()) ?: mutableSetOf()

        val bookmark = "$title|$diagnose|$imageUri|$treatment"
        if (!bookmarkSet.contains(bookmark)) {
            bookmarkSet.add(bookmark)
            editor.putStringSet("bookmark_list", bookmarkSet)
            editor.apply()
            Toast.makeText(requireContext(), "Saved to bookmark", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Bookmark already exists.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MutatingSharedPrefs")
    private fun removeFromBookmark(title: String, diagnose: String) {
        val sharedPreferences = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val bookmarkSet = sharedPreferences.getStringSet("bookmark_list", mutableSetOf()) ?: mutableSetOf()

        val bookmarkToRemove = bookmarkSet.find { it.startsWith("$title|$diagnose|") }
        if (bookmarkToRemove != null) {
            bookmarkSet.remove(bookmarkToRemove)
            editor.putStringSet("bookmark_list", bookmarkSet)
            editor.apply()
            Toast.makeText(requireContext(), "Removed from bookmark", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkIfBookmarked(title: String, diagnose: String): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val bookmarkSet = sharedPreferences.getStringSet("bookmark_list", mutableSetOf()) ?: mutableSetOf()
        return bookmarkSet.any { it.startsWith("$title|$diagnose|") }
    }

    @SuppressLint("SetTextI18n")
    private fun updateBookmarkButton() {
        if (isBookmarked) {
            binding.btnBookmark.text = "Remove from Bookmark"
            binding.btnBookmark.setBackgroundColor(Color.WHITE)
            binding.btnBookmark.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            binding.btnBookmark.text = "Save to Bookmark"
            binding.btnBookmark.setBackgroundColor(Color.parseColor("#4CAF50"))
            binding.btnBookmark.setTextColor(Color.WHITE)
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Timber.tag("ResultFragment").e("Error loading image: ${e.message}")
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}