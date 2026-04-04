package com.cookingapp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cookingapp.R
import com.cookingapp.network.ApiRecipe
import com.cookingapp.viewmodel.RecipeViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecipeDetailFragment : Fragment() {

    private lateinit var recipe: ApiRecipe
    private lateinit var viewModel: RecipeViewModel
    private var currentPhotoUri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) launchCamera()
        else Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoUri?.let { uri ->
                view?.findViewById<ImageView>(R.id.capturedPhotoImageView)?.apply {
                    setImageURI(uri)
                    visibility = View.VISIBLE
                }
                // Deduct ingredients from pantry
                viewModel.deductPantryIngredients(recipe.id)
                Toast.makeText(requireContext(), "Recipe completed! Pantry updated.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipe = arguments?.getSerializable("recipe") as? ApiRecipe
            ?: ApiRecipe(0, "Unknown", null, null, null, null, null)
        viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.recipeTitle).text = recipe.title
        view.findViewById<TextView>(R.id.recipeTime).text = "Ready in: ${recipe.readyInMinutes ?: "?"} min"
        view.findViewById<TextView>(R.id.recipeServings).text = "Serves: ${recipe.servings ?: "?"}"

        // Load full recipe details (ingredients)
        viewModel.loadRecipeDetails(recipe.id)
        viewModel.recipeDetails.observe(viewLifecycleOwner) { details ->
            if (details != null) {
                val ingredientText = details.extendedIngredients.joinToString("\n") {
                    "• ${it.amount} ${it.unit} ${it.name}"
                }
                view.findViewById<TextView>(R.id.recipeIngredients).text = ingredientText

                // Add instructions
                val instructions = details.instructions
                    ?.replace("<ol>", "")
                    ?.replace("</ol>", "")
                    ?.replace("<li>", "\n• ")
                    ?.replace("</li>", "")
                    ?.replace("<p>", "")
                    ?.replace("</p>", "\n")
                    ?.trim()
                    ?: "No instructions available"

                view.findViewById<TextView>(R.id.recipeInstructions).text = instructions
            }
        }

        view.findViewById<Button>(R.id.completeRecipeButton).setOnClickListener {
            checkCameraAndLaunch()
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
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val storageDir = requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            val photoFile = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            currentPhotoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
            cameraLauncher.launch(intent)
        } else {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun newInstance(recipe: ApiRecipe): RecipeDetailFragment {
            val fragment = RecipeDetailFragment()
            val args = Bundle()
            args.putSerializable("recipe", recipe)
            fragment.arguments = args
            return fragment
        }
    }
}