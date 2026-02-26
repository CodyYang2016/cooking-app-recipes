package com.cookingapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookingapp.databinding.FragmentPantryBinding
import com.cookingapp.databinding.ListItemPantryBinding
import com.cookingapp.model.PantryItem

/**
 * PantryFragment — displays the user's pantry as a scrollable list.
 *
 * This is a real screen in the app's flow: users will eventually add/remove
 * ingredients here. For this checkpoint, it shows hardcoded data.
 *
 * All 8 Fragment lifecycle methods are logged (the 6 standard + onCreateView
 * and onDestroyView) as required by the checkpoint.
 */
class PantryFragment : Fragment() {

    private var _binding: FragmentPantryBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "LIFECYCLE_PANTRY"

        /** Hardcoded pantry items — stand-ins for eventual Room DB data. */
        val SAMPLE_ITEMS = listOf(
            PantryItem(1, "Chicken Breast",  2.0,  "lbs"),
            PantryItem(2, "Garlic",          5.0,  "cloves"),
            PantryItem(3, "Olive Oil",       0.5,  "cup"),
            PantryItem(4, "Onion",           2.0,  "whole"),
            PantryItem(5, "Canned Tomatoes", 2.0,  "cans"),
            PantryItem(6, "Pasta",           1.0,  "lb"),
            PantryItem(7, "Parmesan",        0.25, "cup"),
            PantryItem(8, "Salt",            1.0,  "tbsp"),
            PantryItem(9, "Black Pepper",    1.0,  "tsp"),
            PantryItem(10,"Butter",          2.0,  "tbsp")
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
        _binding = FragmentPantryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ── Good breakpoint location for the debugger demo ──
        val items = SAMPLE_ITEMS        // <-- set breakpoint here, inspect `items`
        Log.d(TAG, "onViewCreated() — loading ${items.size} pantry items")

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = PantryAdapter(items)
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
        _binding = null     // Avoid memory leaks — required pattern with ViewBinding
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

    private class PantryAdapter(
        private val items: List<PantryItem>
    ) : RecyclerView.Adapter<PantryAdapter.ViewHolder>() {

        inner class ViewHolder(
            private val binding: ListItemPantryBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(item: PantryItem) {
                binding.textName.text     = item.name
                binding.textQuantity.text = "${item.quantity} ${item.unit}"
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListItemPantryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) =
            holder.bind(items[position])

        override fun getItemCount() = items.size
    }
}
