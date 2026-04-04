package com.cookingapp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookingapp.R
import com.cookingapp.databinding.FragmentRecipeSearchBinding
import com.cookingapp.utils.CameraHelper
import com.cookingapp.viewmodel.RecipeViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecipeSearchFragment : Fragment() {

    private var _binding: FragmentRecipeSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecipeViewModel
    private lateinit var cameraHelper: CameraHelper
    private lateinit var recipeAdapter: RecipeAdapter
    private var currentPhotoUri: Uri? = null
    private var currentPhotoPath: String? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) launchCamera()
        else Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_LONG).show()
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoUri?.let { uri -> handleCapturedPhoto(uri) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
        cameraHelper = CameraHelper(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecipeSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchButtons()
        setupCameraButton()
        observeData()
    }

    private fun setupRecyclerView() {
        recipeAdapter = RecipeAdapter { recipe ->
            // Navigate to detail fragment on click
            val fragment = RecipeDetailFragment.newInstance(recipe)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recipeRecyclerView.adapter = recipeAdapter
    }

    private fun setupSearchButtons() {
        binding.searchButton.setOnClickListener {
            val query = binding.searchInput.text.toString()
            if (query.isNotBlank()) viewModel.searchRecipes(query)
        }

        binding.pantrySearchButton.setOnClickListener {
            viewModel.searchByPantryIngredients()
        }
    }

    private fun setupCameraButton() {
        binding.root.findViewById<Button>(R.id.cameraButton)?.setOnClickListener {
            checkCameraAndLaunch()
        }
    }

    private fun observeData() {
        viewModel.searchResults.observe(viewLifecycleOwner) { apiRecipes ->
            recipeAdapter.submitList(apiRecipes)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun checkCameraAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            val photoFile = createImageFile()
            if (photoFile != null) {
                currentPhotoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                cameraLauncher.launch(intent)
            }
        } else {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun handleCapturedPhoto(uri: Uri) {
        currentPhotoPath?.let { path ->
            val compressedPath = cameraHelper.compressAndSaveImage(path, 70)
            Log.d("CAMERA", "Photo saved at: $compressedPath")
        }
        Toast.makeText(requireContext(), "Photo saved!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}