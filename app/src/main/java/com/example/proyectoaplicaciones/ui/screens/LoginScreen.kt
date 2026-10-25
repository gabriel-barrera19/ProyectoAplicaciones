package com.example.proyectoaplicaciones.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoaplicaciones.viewmodel.LoginViewModel
import androidx.compose.ui.text.style.TextAlign

/**
 * Pantalla de inicio de sesión.
 *
 * Recibe el ViewModel y una función que se ejecuta
 * cuando el acceso es correcto.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    alLoginExitoso: () -> Unit
) {
    // Observa el estado del Login y se actualiza cuando cambia.
    val estado by viewModel.estado.collectAsState()

    // Organiza los componentes de la pantalla verticalmente.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Título de la aplicación.
        Text(
            text = "Gestión de oportunidades",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo para ingresar el usuario.
        OutlinedTextField(
            value = estado.usuario,
            onValueChange = viewModel::onUsuarioChange,
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = estado.errores.usuario != null,
            supportingText = {
                estado.errores.usuario?.let { error ->
                    Text(text = error)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo para ingresar la contraseña de forma oculta.
        OutlinedTextField(
            value = estado.contrasena,
            onValueChange = viewModel::onContrasenaChange,
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            isError = estado.errores.contrasena != null,
            supportingText = {
                estado.errores.contrasena?.let { error ->
                    Text(text = error)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mensaje cuando las credenciales son incorrectas.
        estado.errores.credenciales?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Comprueba los datos antes de permitir el ingreso.
        Button(
            onClick = {
                if (viewModel.validarAcceso()) {
                    alLoginExitoso()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }
    }
}