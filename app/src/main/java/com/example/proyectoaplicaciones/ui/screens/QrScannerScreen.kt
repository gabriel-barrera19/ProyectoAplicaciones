package com.example.proyectoaplicaciones.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoaplicaciones.viewmodel.QrViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    alVolver: () -> Unit,
    qrViewModel: QrViewModel = viewModel()
) {
    val contexto = LocalContext.current

    // Observa el resultado del código QR.
    val resultado by qrViewModel.resultadoQr.collectAsState()

    // Comprueba si el permiso de cámara ya fue concedido.
    val permisoInicial = remember {
        ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Guarda el estado actual del permiso.
    var permisoConcedido by remember {
        mutableStateOf(permisoInicial)
    }

    // Solicita permiso y actualiza el estado según la respuesta.
    val solicitarPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoConcedido = concedido
    }

    // Solicita el permiso automáticamente al abrir la pantalla.
    LaunchedEffect(Unit) {
        if (!permisoConcedido) {
            solicitarPermiso.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Lector de códigos QR")
                },
                navigationIcon = {
                    TextButton(
                        onClick = alVolver
                    ) {
                        Text("Volver")
                    }
                }
            )
        }
    ) { paddingInterno ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (permisoConcedido) {

                Text(
                    text = "Apunta la cámara hacia un código QR.",
                    style = MaterialTheme.typography.bodyLarge
                )

                // Espacio donde se muestra la cámara.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    QrScanner(
                        alDetectarCodigo = { codigo ->
                            qrViewModel.alDetectarCodigo(codigo)
                        }
                    )
                }

                if (resultado.isNotBlank()) {
                    Text(
                        text = "Resultado del QR:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = resultado,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = qrViewModel::limpiarResultado,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Limpiar resultado")
                    }
                }
            } else {
                Text(
                    text = "Necesitamos permiso para usar la cámara y leer códigos QR."
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Button(
                    onClick = {
                        solicitarPermiso.launch(
                            Manifest.permission.CAMERA
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Conceder permiso de cámara")
                }
            }
        }
    }
}