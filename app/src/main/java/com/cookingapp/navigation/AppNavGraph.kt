package com.example.cookingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cookingapp.CookingApp
import com.example.cookingapp.di.ViewModelFactory
import com.example.cookingapp.ui.auth.LoginScreen
import com.example.cookingapp.ui.auth.SignUpScreen
import com.example.cookingapp.ui.cook.CookScreen
import com.example.cookingapp.ui.history.HistoryScreen
import com.example.cookingapp.ui.pantry.PantryScreen
import com.example.cookingapp.ui.recipe.RecipeDetailScreen
import com.example.cookingapp.ui.recipe.RecipeSearchScreen
import com.example.cookingapp.viewmodel.AuthViewModel

object Route {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val PANTRY = "pantry"
    const val RECIPE_SEARCH = "recipe_search"
    const val RECIPE_DETAIL = "recipe_detail/{recipeId}"
    const val COOK = "cook/{recipeId}"
    const val HISTORY = "history"

    fun recipeDetail(id: String) = "recipe_detail/$id"
    fun cook(id: String) = "cook/$id"
}

@Composable
fun AppNavGraph() {
    val context = LocalContext.current
    val app = context.applicationContext as CookingApp
    val factory = ViewModelFactory(app.container)

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val authState by authViewModel.state.collectAsState()

    val startDest = if (authState.isAuthenticated) Route.PANTRY else Route.LOGIN

    NavHost(navController = navController, startDestination = startDest) {

        composable(Route.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Route.PANTRY) { popUpTo(Route.LOGIN) { inclusive = true } } },
                onNavigateToSignUp = { navController.navigate(Route.SIGNUP) },
                vm = authViewModel
            )
        }

        composable(Route.SIGNUP) {
            SignUpScreen(
                onSignUpSuccess = { navController.navigate(Route.PANTRY) { popUpTo(Route.SIGNUP) { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() },
                vm = authViewModel
            )
        }

        composable(Route.PANTRY) {
            PantryScreen(
                onNavigateToSearch = { navController.navigate(Route.RECIPE_SEARCH) },
                vm = viewModel(factory = factory)
            )
        }

        composable(Route.RECIPE_SEARCH) {
            RecipeSearchScreen(
                onRecipeSelected = { id -> navController.navigate(Route.recipeDetail(id)) },
                vm = viewModel(factory = factory)
            )
        }

        composable(
            route = Route.RECIPE_DETAIL,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
            RecipeDetailScreen(
                onStartCooking = { id -> navController.navigate(Route.cook(id)) },
                onBack = { navController.popBackStack() },
                vm = viewModel(factory = factory, key = recipeId)
            )
        }

        composable(
            route = Route.COOK,
            arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
            CookScreen(
                onFinished = { navController.navigate(Route.HISTORY) { popUpTo(Route.PANTRY) } },
                onBack = { navController.popBackStack() },
                vm = viewModel(factory = factory, key = recipeId),
                authVm = authViewModel
            )
        }

        composable(Route.HISTORY) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                vm = viewModel(factory = factory)
            )
        }
    }
}