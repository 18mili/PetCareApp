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
    val age: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    ownerName: String,
    onAddPet: () -> Unit,
    onLogout: () -> Unit
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
        topBar = {
            TopAppBar(
                title = { Text("Mascotas de $ownerName") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPet) {
                Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
            }
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
                    ListItem(
                        headlineContent = { Text(pet.name) },
                        supportingContent = { Text("${pet.species} - ${pet.age} años") }
                    )
                    Divider()
                }
            }
        }
    }
}
