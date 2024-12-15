package com.example.capstone.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import com.example.capstone.databinding.FragmentHomeBinding
import com.example.capstone.ui.auth.LoginActivity
import com.example.capstone.view.bookmark.Bookmark
import com.example.capstone.view.bookmark.BookmarkAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BookmarkAdapter
    private val bookmarks = mutableListOf<Bookmark>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : androidx.core.view.MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.bottom_nav_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_logout -> {
                        performLogout()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        loadBookmarks()
        setupRecyclerView()
        toggleEmptyState()

        binding.capstoneBtnScanNow.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scannerFragment)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        loadBookmarks()
        adapter.notifyDataSetChanged()
        toggleEmptyState()
    }

    private fun performLogout() {
        val sharedPref = requireContext().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("IS_LOGGED_IN", false).apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setupRecyclerView() {
        adapter = BookmarkAdapter(bookmarks)
        binding.capstoneRvBookmarks.layoutManager = LinearLayoutManager(requireContext())
        binding.capstoneRvBookmarks.adapter = adapter

        // Animasi RecyclerView
        binding.capstoneRvBookmarks.itemAnimator = object : DefaultItemAnimator() {
            override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
                holder?.itemView?.apply {
                    val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                    startAnimation(animation)
                }
                return super.animateAdd(holder)
            }

            override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
                holder?.itemView?.apply {
                    val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
                    startAnimation(animation)
                }
                return super.animateRemove(holder)
            }
        }

        adapter.setOnItemClickListener { bookmark ->
            openResultFragment(bookmark)
        }

        adapter.setOnDeleteClickListener { bookmark ->
            // Gunakan adapter untuk menghapus item dengan transisi
            adapter.removeBookmark(bookmark)

            // Simpan perubahan pada SharedPreferences
            saveBookmarks()

            // Periksa jika daftar kosong setelah penghapusan
            toggleEmptyState()
        }
    }

    private fun loadBookmarks() {
        val sharedPreferences = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val bookmarkSet = sharedPreferences.getStringSet("bookmark_list", emptySet()) ?: emptySet()

        bookmarks.clear()
        bookmarks.addAll(bookmarkSet.mapNotNull {
            val parts = it.split("|")
            if (parts.size == 4 && parts.all { part -> part.isNotBlank() }) {
                Bookmark(parts[0], parts[1], parts[2], parts[3])
            } else {
                null
            }
        })
    }

    private fun saveBookmarks() {
        val sharedPreferences = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val bookmarkSet = bookmarks.map { "${it.title}|${it.diagnose}|${it.thumbnail}|${it.treatment}" }.toSet()

        editor.putStringSet("bookmark_list", bookmarkSet)
        editor.apply()
    }

    private fun toggleEmptyState() {
        if (bookmarks.isEmpty()) {
            binding.capstoneRvBookmarks.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.capstoneRvBookmarks.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
        }
    }

    private fun openResultFragment(bookmark: Bookmark) {
        val bundle = Bundle().apply {
            putString("RESULT_TITLE", bookmark.title)
            putString("DIAGNOSIS", bookmark.diagnose)
            putString("IMAGE_URI", bookmark.thumbnail)
            putString("RESULT_TREATMENT", bookmark.treatment)
        }
        findNavController().navigate(R.id.action_homeFragment_to_resultFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}