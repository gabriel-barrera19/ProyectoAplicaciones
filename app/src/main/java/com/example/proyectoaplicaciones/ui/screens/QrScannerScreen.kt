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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoaplicaciones.viewmodel.QrViewModel
import com.example.proyectoaplicaciones.viewmodel.OportunidadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    alVolver: () -> Unit,
    alOportunidadCargada: () -> Unit,
    oportunidadViewModel: OportunidadViewModel,
    qrViewModel: QrViewModel = viewModel()
) {
    val contexto = LocalContext.current

    val resultado by qrViewModel.resultadoQr.collectAsState()
    val oportunidadQr by qrViewModel.oportunidadQr.collectAsState()
    val mensajeError by qrViewModel.mensajeError.collectAsState()

    val permisoInicial = remember {
        ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    var permisoConcedido by remember {
        mutableStateOf(permisoInicial)
    }

    val solicitarPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoConcedido = concedido
    }

    LaunchedEffect(Unit) {
        if (!permisoConcedido) {
            solicitarPermiso.launch(Manifest.permission.CAMERA)
        }
    }

    // Cuando el QR es válido, carga los datos y abre el formulario.
    LaunchedEffect(oportunidadQr) {
        oportunidadQr?.let { oportunidad ->
            oportunidadViewModel.cargarOportunidadDesdeQr(oportunidad)
            alOportunidadCargada()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Lector de códigos QR")
                },
                navigationIcon = {
                    TextButton(onClick = alVolver) {
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
                        text = mensajeError.ifBlank {
                            "Código QR leído. Procesando información..."
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (mensajeError.isNotBlank()) {
                        Button(
                            onClick = qrViewModel::limpiarResultado,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Intentar nuevamente")
                        }
                    }
                }

            } else {

                Text(
                    text = "Necesitamos permiso para usar la cámara y leer códigos QR."
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        solicitarPermiso.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Conceder permiso de cámara")
                }
            }
        }
    }
}