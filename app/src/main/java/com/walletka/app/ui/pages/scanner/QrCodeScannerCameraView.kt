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
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.Executors
import javax.inject.Inject

@ExperimentalGetImage
@Composable
fun QrCodeScannerCameraView(
    viewModel: QrCodeScannerCameraViewModel = hiltViewModel(),
    onQrCodeFound: (String?, ULong?) -> Unit,
) {
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
                            BarcodeCodeAnalyzer(viewModel) { destination, amount ->
                                onQrCodeFound(destination, amount)
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
    QrCodeScannerCameraView(hiltViewModel()) { _,_ ->

    }
}

class BarcodeCodeAnalyzer(
    val viewModel: QrCodeScannerCameraViewModel,
    val callback: (String?, ULong?) -> Unit
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
                    if (barcodes.size > 0) {
                        Log.i("QrScanner", "Found WrCode ${barcodes[0]}")
                        try {
                            val parsed = barcodes[0].rawValue?.parseInvoice()
                            if (parsed?.first != null) {
                                callback(parsed.first, parsed.second)
                                return@addOnSuccessListener
                            }
                        } catch (_: Exception) {
                        }

                        callback(barcodes[0].rawValue, null)
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

// BIP21
private fun String.parseInvoice(): Pair<String?, ULong?>? {
    if (startsWith("ln")) {
        return Pair(this, null)
    }

    if (this.checkIfBitcoinAddress()) {
        return Pair(this, null)
    }

    if (startsWith("bitcoin:")) {
        val addressEndIndex = if (this.indexOf("?") != -1) this.indexOf("?") else this.length
        val address = this.substring(8, addressEndIndex)
        val params = substring(indexOf("?") + 1).split("&").map {
            val parts = it.split('=')
            val name = parts.firstOrNull() ?: ""
            val value = parts.drop(1).firstOrNull() ?: ""
            Pair(name, value)
        }
        val lnInvoice = params.firstOrNull { it.first == "lightning" }?.second
        val amount =
            params.firstOrNull { it.first == "amount" }?.second?.toDouble()?.times(100000000)
                ?.toULong()

        return Pair(lnInvoice ?: address, amount)
    }

    return null
}

fun String.checkIfBitcoinAddress(): Boolean {
    return if (startsWith("1") || startsWith("m") || startsWith("n")) {
        true
    } else if (startsWith("3") || startsWith("2")) {
        true
    } else if (startsWith("bc1") || startsWith("tb1")) {
        if (startsWith("bc1q") || startsWith("tb1q")) {
            true
        } else startsWith("bc1p") || startsWith("tb1p")
    } else {
        false
    }
}

@HiltViewModel
class QrCodeScannerCameraViewModel @Inject constructor() : ViewModel() {

}