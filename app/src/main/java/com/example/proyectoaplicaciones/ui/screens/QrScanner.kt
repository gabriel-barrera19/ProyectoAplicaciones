package com.example.proyectoaplicaciones.ui.screens

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun QrScanner(
    alDetectarCodigo: (String) -> Unit
) {
    val contexto = LocalContext.current
    val propietarioCicloVida = LocalLifecycleOwner.current

    val vistaPrevia = remember {
        PreviewView(contexto)
    }

    val ejecutorCamara = remember {
        Executors.newSingleThreadExecutor()
    }

    val proveedorCamara = remember {
        ProcessCameraProvider.getInstance(contexto)
    }

    val opciones = remember {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    }

    val lector = remember {
        BarcodeScanning.getClient(opciones)
    }

    DisposableEffect(Unit) {
        val listener = Runnable {
            val proveedor = proveedorCamara.get()

            val vistaPreviaCamara = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = vistaPrevia.surfaceProvider
                }

            val analizador = ImageAnalysis.Builder()
                .setBackpressureStrategy(
                    ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                )
                .build()

            analizador.setAnalyzer(ejecutorCamara) { imagenProxy ->
                val imagen = imagenProxy.image

                if (imagen == null) {
                    imagenProxy.close()
                } else {
                    val imagenEntrada = InputImage.fromMediaImage(
                        imagen,
                        imagenProxy.imageInfo.rotationDegrees
                    )

                    lector.process(imagenEntrada)
                        .addOnSuccessListener { codigos ->
                            codigos.firstOrNull()?.rawValue?.let { codigo ->
                                alDetectarCodigo(codigo)
                            }
                        }
                        .addOnCompleteListener {
                            imagenProxy.close()
                        }
                }
            }

            try {
                proveedor.unbindAll()

                proveedor.bindToLifecycle(
                    propietarioCicloVida,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    vistaPreviaCamara,
                    analizador
                )
            } catch (error: Exception) {
                error.printStackTrace()
            }
        }

        proveedorCamara.addListener(
            listener,
            androidx.core.content.ContextCompat.getMainExecutor(contexto)
        )

        onDispose {
            if (proveedorCamara.isDone) {
                proveedorCamara.get().unbindAll()
            }

            lector.close()
            ejecutorCamara.shutdown()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = { vistaPrevia },
            modifier = Modifier.fillMaxSize()
        )
    }
}