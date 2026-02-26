package com.cookingapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity  // Add this import
import androidx.fragment.app.Fragment
import com.cookingapp.databinding.ActivityMainBinding  // Add this import
import com.cookingapp.ui.PantryFragment
import com.cookingapp.ui.RecipeSearchFragment

/**
 * MainActivity — the single Activity for this app.
 *
 * Hosts PantryFragment and RecipeSearchFragment via a BottomNavigationView.
 * All 6 standard Activity lifecycle methods are logged here for the checkpoint.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "LIFECYCLE_ACTIVITY"
    }

    // ─── Lifecycle Methods ────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the default fragment on first creation only
        if (savedInstanceState == null) {
            loadFragment(PantryFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pantry -> {
                    loadFragment(PantryFragment())
                    true
                }
                R.id.nav_recipes -> {
                    loadFragment(RecipeSearchFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Replaces the fragment container contents with the given fragment.
     * Uses the back stack so the user can navigate back naturally.
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)  // Optional: add this if you want back navigation
            .commit()
    }
}