package cl.duoc.petcare

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    ownerName: String,
    onPetSaved: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { PetLocalDataStore(context) }
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }
    var species by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        nameError = if (name.isBlank()) "El nombre es obligatorio" else null
        return nameError == null
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nueva mascota de $ownerName") })
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (name.isNotBlank()) nameError = null
                },
                label = { Text("Nombre de la mascota") },
                isError = nameError != null,
                supportingText = { if (nameError != null) Text(nameError!!) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = species,
                onValueChange = { species = it },
                label = { Text("Especie") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Edad") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (!validate()) return@Button

                    scope.launch {
                        repo.addPet(
                            Pet(
                                owner = ownerName,
                                name = name,
                                species = species,
                                age = age
                            )
                        )
                        onPetSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar mascota")
            }
        }
    }
}
