package com.example.multex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.multex.ui.screens.EditorScreen
import com.example.multex.ui.screens.ImageSelectionScreen
import com.example.multex.ui.screens.SplashScreen
import com.example.multex.ui.theme.MultexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultexTheme {
                val navController = rememberNavController()
                // Create the ViewModel at the Activity level, so it's shared across all screens.
                val sharedViewModel: SharedViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.fillMaxSize()
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
