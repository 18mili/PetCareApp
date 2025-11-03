package cl.duoc.petcare

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun HomeShell(ownerName: String,
              onAddPet: () -> Unit,
              onLogout: () -> Unit,
              onNavigateToAddPet: () -> Unit,
              onNavigateToScan: () -> Unit) {

    var selected by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (selected == 0) { // Solo mostrar FAB en la pestaña Inicio
                FloatingActionButton(
                    onClick = onNavigateToAddPet,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selected == 0,
                    onClick = { selected = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = selected == 1,
                    onClick = { selected = 1 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
                NavigationBarItem(
                    selected = selected == 2,
                    onClick = { selected = 2 },
                    icon = { Icon(Icons.Default.LocalHospital, contentDescription = "Emergencia") },
                    label = { Text("Emergencia") }
                )
            }
        }
    ) { inner ->
        when (selected) {
            0 -> PetListScreen(
                ownerName = ownerName,
                onAddPet = onNavigateToAddPet,
                onLogout = onLogout,
                onScanFoods = onNavigateToScan
            )
            1 -> ProfileScreen(ownerName = ownerName, onBackToHome = { selected = 0 })
            2 -> VetEmergencyScreen(ownerName = ownerName)
        }
    }
}
