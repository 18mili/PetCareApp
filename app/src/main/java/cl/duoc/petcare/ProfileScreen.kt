package cl.duoc.petcare

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(ownerName: String, onBackToHome: () -> Unit) {
    val context = LocalContext.current
    val petRepo = remember { PetLocalDataStore(context) }
    val userRepo = remember { UserLocalDataStore(context) }
    val scope = rememberCoroutineScope()

    var pets by remember { mutableStateOf<List<Pet>>(emptyList()) }
    var user by remember { mutableStateOf<User?>(null) }
    var editing by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editImageUri by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            editImageUri = uri?.toString()
        }
    )

    LaunchedEffect(ownerName) {
        pets = petRepo.getPetsByOwner(ownerName)
        user = userRepo.getUserByName(ownerName)
        user?.let {
            editName = it.name
            editEmail = it.email
            editImageUri = if (it.profileImage.isNotBlank()) it.profileImage else null
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header: imagen + nombre + edit
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (editImageUri != null) {
                            AsyncImage(model = editImageUri, contentDescription = "Foto de perfil", modifier = Modifier.size(72.dp))
                        } else {
                            Icon(imageVector = Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(text = user?.name ?: ownerName, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                            Text(text = "${pets.size} mascotas registradas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        }
                    }

                    IconButton(onClick = { editing = !editing }) {
                        if (editing) Icon(Icons.Default.Save, contentDescription = "Guardar") else Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (editing) {
                // Editor de perfil
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editEmail, onValueChange = { editEmail = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { imagePicker.launch("image/*") }) { Text("Seleccionar imagen de perfil") }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            // cancelar
                            editing = false
                            // reset edits from user
                            user?.let { u -> editName = u.name; editEmail = u.email; editImageUri = if (u.profileImage.isNotBlank()) u.profileImage else null }
                        }) { Text("Cancelar") }

                        Button(onClick = {
                            // guardar cambios
                            scope.launch {
                                val oldName = user?.name ?: ownerName
                                val updatedUser = User(name = editName, email = editEmail, password = user?.password ?: "", profileImage = editImageUri ?: "")
                                userRepo.updateUser(updatedUser)
                                // actualizar mascotas si cambia el nombre
                                if (!oldName.equals(editName, ignoreCase = true)) {
                                    petRepo.updateOwnerName(oldName, editName)
                                }
                                user = updatedUser
                                pets = petRepo.getPetsByOwner(updatedUser.name)
                                editing = false
                            }
                        }) { Text("Guardar cambios") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Mis Mascotas", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)

            Spacer(modifier = Modifier.height(8.dp))

            if (pets.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "No tienes mascotas registradas", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "¡Comienza agregando tu primera mascota!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(pets) { pet ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth().clickable { }, colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = pet.name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = "${pet.species} - ${pet.age} años", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                if (pet.medicalInfo.isNotBlank() || pet.behavior.isNotBlank()) HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                if (pet.medicalInfo.isNotBlank()) {
                                    Text(text = "Información médica:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    Text(text = pet.medicalInfo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                }
                                if (pet.behavior.isNotBlank()) {
                                    Text(text = "Comportamiento:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    Text(text = pet.behavior, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
