package cl.duoc.petcare

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        nameError = if (name.isBlank()) "Nombre obligatorio" else null
        emailError = if (email.isBlank()) "Correo obligatorio" else null
        passError = if (password.length < 4) "Mínimo 4 caracteres" else null
        confirmError = if (confirm != password) "No coincide" else null
        return listOf(nameError, emailError, passError, confirmError).all { it == null }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; if (it.isNotBlank()) nameError = null },
            label = { Text("Nombre completo") },
            isError = nameError != null,
            supportingText = { if (nameError != null) Text(nameError!!) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; if (it.isNotBlank()) emailError = null },
            label = { Text("Correo") },
            isError = emailError != null,
            supportingText = { if (emailError != null) Text(emailError!!) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; if (it.length >= 4) passError = null },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passError != null,
            supportingText = { if (passError != null) Text(passError!!) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it; if (it == password) confirmError = null },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = confirmError != null,
            supportingText = { if (confirmError != null) Text(confirmError!!) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (validate()) {
                    onRegisterSuccess(name)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarme")
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Volver al login",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onBackToLogin() }
        )
    }
}
