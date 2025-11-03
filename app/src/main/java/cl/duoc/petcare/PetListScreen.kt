package cl.duoc.petcare

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class Pet(
    val owner: String,
    val name: String,
    val species: String,
    val age: String,
    val medicalInfo: String = "",
    val behavior: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    ownerName: String,
    onAddPet: () -> Unit,
    onLogout: () -> Unit,
    onScanFoods: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { PetLocalDataStore(context) }
    val scope = rememberCoroutineScope()

    // estado que se muestra en la UI
    var pets by remember { mutableStateOf<List<Pet>>(emptyList()) }

    // cuando entro a la pantalla, cargo las mascotas del dueño
    LaunchedEffect(ownerName) {
        val list = repo.getPetsByOwner(ownerName)
        pets = list
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Mascotas de $ownerName") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    TextButton(
                        onClick = onScanFoods,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Escanear alimento")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        }
    ) { inner ->
        if (pets.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Aún no registras mascotas 🐶🐱")
                Text("Presiona el botón + para agregar una.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
            ) {
                items(pets) { pet ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        )
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = pet.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            supportingContent = {
                                Column {
                                    Text(
                                        text = "${pet.species} - ${pet.age} años",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                    )
                                    if (pet.behavior.isNotBlank()) {
                                        Text(
                                            text = "Comportamiento: ${if (pet.behavior.length > 60) pet.behavior.take(60) + "..." else pet.behavior}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                        )
                                    }
                                    if (pet.medicalInfo.isNotBlank()) {
                                        Text(
                                            text = "Salud: ${if (pet.medicalInfo.length > 60) pet.medicalInfo.take(60) + "..." else pet.medicalInfo}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
