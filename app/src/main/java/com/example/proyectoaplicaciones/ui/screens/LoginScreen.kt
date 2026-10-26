package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoaplicaciones.viewmodel.LoginViewModel

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

    // Controla la aparición animada del contenido.
    var mostrarContenido by remember {
        mutableStateOf(false)
    }

    // Activa la animación cuando se muestra la pantalla.
    LaunchedEffect(Unit) {
        mostrarContenido = true
    }

    // Organiza y centra el contenido de la pantalla.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Anima la aparición del contenido del Login.
        AnimatedVisibility(
            visible = mostrarContenido,
            enter = fadeIn(
                animationSpec = tween(600)
            ) + slideInVertically(
                animationSpec = tween(600),
                initialOffsetY = { it / 8 }
            )
        ) {

            // Agrupa los campos y botones del formulario.
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Título de la aplicación.
                Text(
                    text = "Gestión de oportunidades",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtítulo del Login.
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
                    label = {
                        Text("Usuario")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !estado.cargando,
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
                    label = {
                        Text("Contraseña")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !estado.cargando,
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

                // Botón que valida los datos y muestra la carga.
                Button(
                    onClick = {
                        viewModel.validarAcceso { accesoValido ->
                            if (accesoValido) {
                                alLoginExitoso()
                            }
                        }
                    },
                    enabled = !estado.cargando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (estado.cargando) {

                        // Círculo animado mientras se valida el acceso.
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimaryFixed
                        )

                    } else {

                        // Texto normal cuando no se está procesando.
                        Text("Ingresar")
                    }
                }
            }
        }
    }
}