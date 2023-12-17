package com.walletka.app.ui.pages.scanner

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.walletka.app.dto.QrCodeResultDto
import com.walletka.app.usecases.AnalyzeQrCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Inject

@ExperimentalGetImage
@Composable
fun QrCodeScannerCameraView(
    viewModel: QrCodeScannerCameraViewModel = hiltViewModel(),
    onQrCodeFound: (QrCodeResultDto?) -> Unit,
) {

    if (viewModel.result != null) {
        onQrCodeFound(viewModel.result)
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val cameraExecutor = Executors.newSingleThreadExecutor()
            val previewView = PreviewView(context).also {
                it.scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val imageCapture = ImageCapture.Builder().build()

                val imageAnalyzer = ImageAnalysis.Builder()
                    .build()
                    .also {
                        it.setAnalyzer(
                            cameraExecutor,
                            BarcodeCodeAnalyzer(viewModel) {
                                onQrCodeFound(it)
                            })
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        context as ComponentActivity,
                        cameraSelector,
                        preview,
                        imageCapture,
                        imageAnalyzer
                    )

                } catch (exc: Exception) {
                    Log.e("QrCodeScanner", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))
            previewView
        }
    )
}

@OptIn(ExperimentalGetImage::class)
@Composable
@androidx.compose.ui.tooling.preview.Preview
fun PreviewQrCodeScannerCameraView() {
    QrCodeScannerCameraView(hiltViewModel()) {

    }
}

class BarcodeCodeAnalyzer(
    val viewModel: QrCodeScannerCameraViewModel,
    val callback: (QrCodeResultDto) -> Unit
) : ImageAnalysis.Analyzer {
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val scanner = BarcodeScanning.getClient(options)

        val mediaImage = imageProxy.image
        mediaImage?.let {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (!viewModel.processing && viewModel.result == null) {
                        if (barcodes.size > 0) {
                            viewModel.processInput(barcodes[0].rawValue!!)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("QrScanner", "Error scanning Qr codes", it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}

@HiltViewModel
class QrCodeScannerCameraViewModel @Inject constructor(
    private val analyzeQrCode: AnalyzeQrCodeUseCase
) : ViewModel() {

    var processing by mutableStateOf(false)
    var result: QrCodeResultDto? by mutableStateOf(null)

    fun processInput(input: String) {
        processing = true
        if (result == null) {
            viewModelScope.launch {
                result = analyzeQrCode(input)
                processing = false
            }
        }
    }
}