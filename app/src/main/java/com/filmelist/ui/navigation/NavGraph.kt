package com.filmelist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.filmelist.ui.list_detail.ListDetailScreen
import com.filmelist.ui.lists.ListsScreen
import com.filmelist.ui.movie_detail.MovieDetailScreen
import com.filmelist.ui.search.SearchScreen

sealed class Screen(val route: String) {
    data object Lists : Screen("lists")
    data object Search : Screen("search")
    data object ListDetail : Screen("list/{listId}") {
        fun createRoute(listId: Long) = "list/$listId"
    }
    data object MovieDetail : Screen("movie/{movieId}/{mediaType}") {
        fun createRoute(movieId: Int, mediaType: String) = "movie/$movieId/$mediaType"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Lists.route) {

        composable(Screen.Lists.route) {
            ListsScreen(
                onListClick = { listId -> navController.navigate(Screen.ListDetail.createRoute(listId)) },
                onSearchClick = { navController.navigate(Screen.Search.route) },
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onMovieClick = { movieId, mediaType ->
                    navController.navigate(Screen.MovieDetail.createRoute(movieId, mediaType))
                },
            )
        }

        composable(
            route = Screen.ListDetail.route,
            arguments = listOf(navArgument("listId") { type = NavType.LongType }),
        ) { backStack ->
            val listId = backStack.arguments?.getLong("listId") ?: return@composable
            ListDetailScreen(
                listId = listId,
                onBack = { navController.popBackStack() },
                onMovieClick = { movieId, mediaType ->
                    navController.navigate(Screen.MovieDetail.createRoute(movieId, mediaType))
                },
            )
        }

        composable(
            route = Screen.MovieDetail.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType },
                navArgument("mediaType") { type = NavType.StringType },
            ),
        ) { backStack ->
            val movieId = backStack.arguments?.getInt("movieId") ?: return@composable
            val mediaType = backStack.arguments?.getString("mediaType") ?: "MOVIE"
            MovieDetailScreen(
                movieId = movieId,
                mediaType = mediaType,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
