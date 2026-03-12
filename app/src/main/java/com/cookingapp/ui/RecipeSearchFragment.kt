package com.cookingapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookingapp.R
import com.cookingapp.databinding.FragmentRecipeSearchBinding
import com.cookingapp.model.Recipe
import com.cookingapp.viewmodel.RecipeViewModel

class RecipeSearchFragment : Fragment() {

    private var _binding: FragmentRecipeSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecipeViewModel
    private lateinit var adapter: RecipeAdapter

    companion object {
        private const val TAG = "LIFECYCLE_RECIPE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")

        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
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
        Log.d(TAG, "onViewCreated() called — setting up RecyclerView")

        setupRecyclerView()
        setupAddButton()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = RecipeAdapter(
            onItemClick = { recipe -> toggleFavorite(recipe) },
            onDeleteClick = { recipe -> viewModel.deleteRecipe(recipe) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecipeSearchFragment.adapter
        }
    }

    private fun setupAddButton() {
        binding.buttonAddRecipe.setOnClickListener {
            val title = binding.editTextTitle.text.toString()
            val category = binding.editTextCategory.text.toString()
            val servingsStr = binding.editTextServings.text.toString()

            if (title.isNotBlank() && category.isNotBlank()) {
                val servings = servingsStr.toIntOrNull() ?: 2
                viewModel.insertRecipe(title, category, servings)

                // Clear input fields
                binding.editTextTitle.text.clear()
                binding.editTextCategory.text.clear()
                binding.editTextServings.text.clear()
            }
        }
    }

    private fun observeData() {
        viewModel.allRecipes.observe(viewLifecycleOwner) { recipes ->
            Log.d(TAG, "Data updated — ${recipes.size} recipes in database")
            adapter.submitList(recipes)
        }
    }

    private fun toggleFavorite(recipe: Recipe) {
        viewModel.toggleFavorite(recipe)
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

    // Inner adapter class
    class RecipeAdapter(
        private val onItemClick: (Recipe) -> Unit,
        private val onDeleteClick: (Recipe) -> Unit
    ) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

        private var recipes = listOf<Recipe>()

        fun submitList(newRecipes: List<Recipe>) {
            recipes = newRecipes
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_recipe, parent, false)
            return RecipeViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
            holder.bind(recipes[position])
        }

        override fun getItemCount() = recipes.size

        inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvTitle = itemView.findViewById<TextView>(R.id.tvRecipeTitle)
            private val tvCategory = itemView.findViewById<TextView>(R.id.tvRecipeCategory)
            private val tvServings = itemView.findViewById<TextView>(R.id.tvServings)
            private val btnFavorite = itemView.findViewById<Button>(R.id.btnFavorite)
            private val btnDelete = itemView.findViewById<Button>(R.id.btnDelete)

            fun bind(recipe: Recipe) {
                tvTitle.text = recipe.title
                tvCategory.text = recipe.category
                tvServings.text = "Serves: ${recipe.servings}"
                btnFavorite.text = if (recipe.isFavorite) "★" else "☆"

                itemView.setOnClickListener { onItemClick(recipe) }
                btnDelete.setOnClickListener { onDeleteClick(recipe) }
                btnFavorite.setOnClickListener { onItemClick(recipe) }
            }
        }
    }
}