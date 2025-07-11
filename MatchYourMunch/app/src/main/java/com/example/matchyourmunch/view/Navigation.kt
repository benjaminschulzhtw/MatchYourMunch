package com.example.matchyourmunch.view

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.matchyourmunch.helper.LocationPermissionWrapper
import com.example.matchyourmunch.model.ListRepository
import com.example.matchyourmunch.helper.ListViewModelInterface
import com.example.matchyourmunch.helper.provideFoodSpotViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Steuerung der Navigation zwischen den verschiedenen Views
 * @param navController der Navigationscontroller
 * @param listViewModel ViewModel für Listenansicht
 * @param listIdOverride übschreibt geladene ID der Listen
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    listViewModel: ListViewModelInterface,
    listIdOverride: Int
) {
    NavHost(navController = navController, startDestination = "main") {

        /**
         * Haupt-Bildschirm
         */
        composable("main") {
            MainScreen(
                viewModel = listViewModel,
                onListClick = { listName ->
                    navController.navigate("detail/${Uri.encode(listName)}")
                }
            )
        }

        /**
         * Detailansicht FoodSpot
         */
        composable(
            route = "spotDetail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            val foodSpotViewModel = provideFoodSpotViewModel(listId = -1, spotId = id)
            val spot by foodSpotViewModel.spot.collectAsState()

            LocationPermissionWrapper { deviceCoordinates ->
                if (spot != null) {
                    FoodSpotDetailScreen(
                        spot = spot!!,
                        onBackClick = { navController.popBackStack() },
                        onDeleteClick = {
                            foodSpotViewModel.deleteSpot(it)
                            navController.popBackStack()
                        },
                        navController = navController,
                        deviceCoordinates = deviceCoordinates
                    )
                }
            }
        }

        /**
         * Ansicht bei Bearbeitung eines FoodSpots
         */
        composable(
            "edit_spot/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable

            val foodSpotViewModel = provideFoodSpotViewModel(listId = -1, spotId = id)

            EditSpotScreen(
                spotId = id,
                viewModel = foodSpotViewModel,
                onBack = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("reload_spots", true)

                    navController.popBackStack()
                }
            )
        }

        /**
         * Detailansicht Liste
         */
        composable(
            "detail/{listName}",
            arguments = listOf(navArgument("listName") { defaultValue = "Unbenannt" })
        ) { backStackEntry ->
            val encodedName = backStackEntry.arguments?.getString("listName") ?: "Unbenannt"
            val listName = Uri.decode(encodedName)

            val context = LocalContext.current
            var listId by remember { mutableStateOf<Int?>(null) }

            LaunchedEffect(listName) {
                listId = withContext(Dispatchers.IO) {
                    ListRepository(context).getListIdByName(listName)
                }
            }

            listId?.let { validListId ->
                val foodSpotViewModel =
                    provideFoodSpotViewModel(listId = validListId, spotId = null)

                ListDetailScreen(
                    navController = navController,
                    viewModel = foodSpotViewModel,
                    listName = listName,
                    onBackClick = { navController.popBackStack() },
                    onDeleteConfirmed = {},
                    onSpotClick = { spot ->
                        navController.navigate("spotDetail/${spot.id}")
                    }
                )
            }
        }
    }
}

