package cl.duoc.petcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetCareApp()
        }
    }
}

@Composable
fun PetCareApp() {
    val rootNavController = rememberNavController()
    val context = LocalContext.current

    MaterialTheme {
        Scaffold { innerPadding ->
            NavHost(
                navController = rootNavController,
                startDestination = "login",
                // 👇 aquí estaba el error
                modifier = Modifier.padding(innerPadding)
            ) {
                // LOGIN
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = { ownerName ->
                            rootNavController.navigate("home/$ownerName") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onGoToRegister = {
                            rootNavController.navigate("register")
                        }
                    )
                }

                // REGISTER
                composable("register") {
                    RegisterScreen(
                        onRegisterSuccess = { ownerName ->
                            rootNavController.navigate("home/$ownerName") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onBackToLogin = {
                            rootNavController.popBackStack()
                        }
                    )
                }

                // HOME (lista de mascotas)
                composable(
                    route = "home/{owner}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val owner = backStackEntry.arguments?.getString("owner") ?: "Tutor"
                    PetListScreen(
                        ownerName = owner,
                        onAddPet = {
                            rootNavController.navigate("addPet/$owner")
                        },
                        onLogout = {
                            rootNavController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                // ADD PET
                composable(
                    route = "addPet/{owner}",
                    arguments = listOf(
                        navArgument("owner") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val owner = backStackEntry.arguments?.getString("owner") ?: ""
                    AddPetScreen(
                        ownerName = owner,
                        onPetSaved = {
                            // volver a la lista
                            rootNavController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
