package com.cookingapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookingapp.R
import com.cookingapp.databinding.FragmentPantryBinding
import com.cookingapp.model.PantryItem
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

        // Initialize ViewModel
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
        observeData()

        // Add sample data if needed (you can remove this in production)
        // viewModel.addSampleData()
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

                // Clear input fields
                binding.editTextName.text.clear()
                binding.editTextQuantity.text.clear()
                binding.editTextUnit.text.clear()
            }
        }
    }

    private fun observeData() {
        viewModel.allPantryItems.observe(viewLifecycleOwner) { items ->
            Log.d(TAG, "Data updated — ${items.size} items in database")
            adapter.submitList(items)
        }
    }

    private fun editItem(item: PantryItem) {
        // Simple edit - pre-fill the form
        binding.editTextName.setText(item.name)
        binding.editTextQuantity.setText(item.quantity.toString())
        binding.editTextUnit.setText(item.unit)

        // Delete old and will insert new when add is clicked
        // Or you could create an update dialog
        viewModel.deleteItem(item)
    }

    override fun onStart() {2
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
    class PantryAdapter(
        private val onItemClick: (PantryItem) -> Unit,
        private val onDeleteClick: (PantryItem) -> Unit,
        private val onQuantityChange: (PantryItem, Double) -> Unit
    ) : RecyclerView.Adapter<PantryAdapter.PantryViewHolder>() {

        private var items = listOf<PantryItem>()

        fun submitList(newItems: List<PantryItem>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PantryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_pantry, parent, false)
            return PantryViewHolder(view)
        }

        override fun onBindViewHolder(holder: PantryViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount() = items.size

        inner class PantryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvName = itemView.findViewById<android.widget.TextView>(R.id.tvItemName)
            private val tvQuantity = itemView.findViewById<android.widget.TextView>(R.id.tvItemQuantity)
            private val btnPlus = itemView.findViewById<android.widget.Button>(R.id.btnPlus)
            private val btnMinus = itemView.findViewById<android.widget.Button>(R.id.btnMinus)
            private val btnDelete = itemView.findViewById<android.widget.Button>(R.id.btnDelete)

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