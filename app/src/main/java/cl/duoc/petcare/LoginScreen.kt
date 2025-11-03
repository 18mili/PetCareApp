package cl.duoc.petcare

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onGoToRegister: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { UserLocalDataStore(context) }
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        emailError = if (email.isBlank()) "El correo es obligatorio" else null
        passError = if (password.length < 4) "Mínimo 4 caracteres" else null
        return emailError == null && passError == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("PetCare", style = MaterialTheme.typography.headlineLarge)
        Text("Iniciar sesión", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (email.isNotBlank()) emailError = null
            },
            label = { Text("Correo") },
            isError = emailError != null,
            supportingText = { if (emailError != null) Text(emailError!!) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (password.length >= 4) passError = null
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passError != null,
            supportingText = { if (passError != null) Text(passError!!) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (!validate()) return@Button

                scope.launch {
                    val user = repo.getUserByEmail(email)
                    if (user == null) {
                        emailError = "No existe una cuenta con ese correo"
                        return@launch
                    }
                    if (user.password != password) {
                        passError = "Contraseña incorrecta"
                        return@launch
                    }

                    onLoginSuccess(user.name)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "¿No tienes cuenta? Crea una",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onGoToRegister() }
        )
    }
}
