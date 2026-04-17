package com.cookingapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookingapp.R
import com.cookingapp.databinding.FragmentPantryBinding
import com.cookingapp.model.PantryItem
import com.cookingapp.utils.DataSeeder
import com.cookingapp.viewmodel.PantryViewModel

class PantryFragment : Fragment() {

    private var _binding: FragmentPantryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PantryViewModel
    private lateinit var adapter: PantryAdapter

    companion object {
        private const val TAG = "LIFECYCLE_PANTRY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
        viewModel = ViewModelProvider(this)[PantryViewModel::class.java]
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
        Log.d(TAG, "onViewCreated() called — setting up RecyclerView")

        setupRecyclerView()
        setupAddButton()
        setupSeedButton()
        setupClearAllButton()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = PantryAdapter(
            onItemClick = { item -> editItem(item) },
            onDeleteClick = { item -> viewModel.deleteItem(item) },
            onQuantityChange = { item, newQuantity ->
                viewModel.updateQuantity(item, newQuantity)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PantryFragment.adapter
        }
    }

    private fun setupAddButton() {
        binding.buttonAdd.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val quantityStr = binding.editTextQuantity.text.toString()
            val unit = binding.editTextUnit.text.toString()

            if (name.isNotBlank() && quantityStr.isNotBlank()) {
                val quantity = quantityStr.toDoubleOrNull() ?: 1.0
                viewModel.insertItem(name, quantity, unit)

                binding.editTextName.text.clear()
                binding.editTextQuantity.text.clear()
                binding.editTextUnit.text.clear()
            }
        }
    }

    private fun setupSeedButton() {
        binding.seedButton.setOnClickListener {
            val seeder = DataSeeder(viewModel.pantryRepository)
            seeder.seedLargePantry()

            Toast.makeText(
                requireContext(),
                "Seeding 50+ items...",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupClearAllButton() {
        binding.clearAllButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear All Items")
                .setMessage("Are you sure you want to delete ALL pantry items? This action cannot be undone.")
                .setPositiveButton("Yes, Clear All") { _, _ ->
                    viewModel.clearAllItems()
                    Toast.makeText(requireContext(), "All items cleared", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun observeData() {
        viewModel.allPantryItems.observe(viewLifecycleOwner) { items ->
            Log.d(TAG, "Data updated — ${items.size} items in database")
            adapter.submitListPaged(items)
        }
    }

    private fun editItem(item: PantryItem) {
        binding.editTextName.setText(item.name)
        binding.editTextQuantity.setText(item.quantity.toString())
        binding.editTextUnit.setText(item.unit)
        viewModel.deleteItem(item)
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

    // ==================== ADAPTER WITH PAGINATION ====================

    inner class PantryAdapter(
        private val onItemClick: (PantryItem) -> Unit,
        private val onDeleteClick: (PantryItem) -> Unit,
        private val onQuantityChange: (PantryItem, Double) -> Unit
    ) : RecyclerView.Adapter<PantryAdapter.PantryViewHolder>() {

        private val PAGE_SIZE = 10
        private var allItems = listOf<PantryItem>()
        private var displayedItems = mutableListOf<PantryItem>()
        private var currentPage = 0
        private var isLoading = false
        private var isAllPagesLoaded = false

        fun submitListPaged(items: List<PantryItem>) {
            allItems = items
            currentPage = 0
            isAllPagesLoaded = false
            isLoading = false

            val sizeBefore = displayedItems.size
            displayedItems.clear()
            if (sizeBefore > 0) {
                notifyItemRangeRemoved(0, sizeBefore)
            }

            loadNextPage()
        }

        fun loadNextPage() {
            if (isLoading || isAllPagesLoaded) return

            val start = currentPage * PAGE_SIZE
            if (start >= allItems.size) {
                isAllPagesLoaded = true
                return
            }

            isLoading = true

            val end = minOf(start + PAGE_SIZE, allItems.size)
            val pageItems = allItems.subList(start, end)

            val startPosition = displayedItems.size
            displayedItems.addAll(pageItems)
            notifyItemRangeInserted(startPosition, pageItems.size)

            currentPage++
            isLoading = false
        }

        fun hasMoreItems(): Boolean {
            return (currentPage * PAGE_SIZE) < allItems.size
        }

        @Deprecated("Use submitListPaged instead", ReplaceWith("submitListPaged(items)"))
        fun submitList(items: List<PantryItem>) {
            submitListPaged(items)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_pantry, parent, false)
            return PantryViewHolder(view)
        }

        override fun onBindViewHolder(holder: PantryViewHolder, position: Int) {
            holder.bind(displayedItems[position])

            if (position >= displayedItems.size - 3 && hasMoreItems() && !isLoading) {
                holder.itemView.post {
                    if (!isLoading && hasMoreItems()) {
                        loadNextPage()
                    }
                }
            }
        }

        override fun getItemCount(): Int = displayedItems.size

        inner class PantryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvName = itemView.findViewById<TextView>(R.id.tvItemName)
            private val tvQuantity = itemView.findViewById<TextView>(R.id.tvItemQuantity)
            private val btnPlus = itemView.findViewById<Button>(R.id.btnPlus)
            private val btnMinus = itemView.findViewById<Button>(R.id.btnMinus)
            private val btnDelete = itemView.findViewById<Button>(R.id.btnDelete)

            fun bind(item: PantryItem) {
                tvName.text = item.name
                tvQuantity.text = "${item.quantity} ${item.unit}"

                itemView.setOnClickListener { onItemClick(item) }
                btnDelete.setOnClickListener { onDeleteClick(item) }

                btnPlus.setOnClickListener {
                    onQuantityChange(item, item.quantity + 1)
                }

                btnMinus.setOnClickListener {
                    if (item.quantity > 0) {
                        onQuantityChange(item, item.quantity - 1)
                    }
                }
            }
        }
    }
}