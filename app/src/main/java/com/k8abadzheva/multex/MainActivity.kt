package com.k8abadzheva.multex

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.k8abadzheva.multex.ui.screens.EditorScreen
import com.k8abadzheva.multex.ui.screens.ImageSelectionScreen
import com.k8abadzheva.multex.ui.screens.SplashScreen
import com.k8abadzheva.multex.ui.theme.MultexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            MultexTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    // Create the ViewModel at the Activity level, so it's shared across all screens.
                    val sharedViewModel: SharedViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                    ) {
                        composable("splash") {
                            SplashScreen(navController)
                        }
                        composable("image_selection") {
                            // Pass the single, shared ViewModel instance to the screen.
                            ImageSelectionScreen(
                                navController = navController,
                                viewModel = sharedViewModel
                            )
                        }
                        composable("editor") {
                            // Pass the same instance and navController to the editor screen.
                            EditorScreen(viewModel = sharedViewModel, navController = navController)
                        }
                    }
                }
            }
        }
    }
}
