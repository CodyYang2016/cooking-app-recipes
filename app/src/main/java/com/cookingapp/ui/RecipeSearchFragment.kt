package com.cookingapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookingapp.databinding.FragmentRecipeSearchBinding
import com.cookingapp.databinding.ListItemRecipeBinding
import com.cookingapp.model.Recipe

/**
 * RecipeSearchFragment — lets the user search through available recipes.
 *
 * This is a real screen in the app's flow: in a later checkpoint this will
 * query Room and TheMealDB API. For now it filters a hardcoded list locally.
 *
 * All 8 Fragment lifecycle methods are logged as required by the checkpoint.
 */
class RecipeSearchFragment : Fragment() {

    private var _binding: FragmentRecipeSearchBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "LIFECYCLE_RECIPE"

        /** Hardcoded recipes — stand-ins for eventual Room + API data. */
        val ALL_RECIPES = listOf(
            Recipe(1,  "Spaghetti Carbonara",        "Pasta",   4),
            Recipe(2,  "Chicken Tikka Masala",       "Curry",   4),
            Recipe(3,  "Beef Tacos",                 "Mexican", 2),
            Recipe(4,  "Garlic Butter Shrimp",       "Seafood", 2),
            Recipe(5,  "Vegetable Stir Fry",         "Asian",   3),
            Recipe(6,  "Classic Caesar Salad",       "Salad",   2),
            Recipe(7,  "Mushroom Risotto",           "Italian", 4),
            Recipe(8,  "Honey Garlic Salmon",        "Seafood", 2),
            Recipe(9,  "Black Bean Soup",            "Soup",    6),
            Recipe(10, "Margherita Pizza",           "Italian", 4)
        )
    }

    // ─── Lifecycle Methods ────────────────────────────────────────────────────

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach() called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView() called")
        _binding = FragmentRecipeSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ── Good breakpoint location for the debugger demo ──
        val recipes = ALL_RECIPES       // <-- set breakpoint here, inspect `recipes`
        Log.d(TAG, "onViewCreated() — loaded ${recipes.size} recipes")

        val adapter = RecipeAdapter(recipes.toMutableList())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // Live-filter the list as the user types
        binding.searchBar.addTextChangedListener { text ->
            val query = text.toString().trim().lowercase()
            Log.d(TAG, "Search query: \"$query\"")

            val filtered = if (query.isEmpty()) {
                ALL_RECIPES
            } else {
                ALL_RECIPES.filter {
                    it.title.lowercase().contains(query) ||
                    it.category.lowercase().contains(query)
                }
            }
            adapter.updateData(filtered)
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

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach() called")
    }

    // ─── RecyclerView Adapter ─────────────────────────────────────────────────

    private class RecipeAdapter(
        private val items: MutableList<Recipe>
    ) : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

        inner class ViewHolder(
            private val binding: ListItemRecipeBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(recipe: Recipe) {
                binding.textTitle.text    = recipe.title
                binding.textCategory.text = recipe.category
                binding.textServings.text = "Serves ${recipe.servings}"
            }
        }

        fun updateData(newItems: List<Recipe>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListItemRecipeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(items[position])

        override fun getItemCount() = items.size
    }
}
