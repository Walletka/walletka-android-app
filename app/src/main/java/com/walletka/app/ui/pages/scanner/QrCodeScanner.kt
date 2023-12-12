package com.walletka.app.ui.pages.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.walletka.app.R


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@ExperimentalGetImage
@Composable
fun ScannerScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val permission = Manifest.permission.CAMERA

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Open camera
        } else {
            // Show dialog
        }
    }

    LaunchedEffect(key1 = "RequestCameraPermission", block = {
        checkAndRequestCameraPermission(context, permission, launcher)
    })

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, "back")
                    }
                },
                title = { Text("Scanner") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) {
        ConstraintLayout(
            modifier = Modifier.padding(0.dp)
        ) {
            val (camera, actions) = createRefs()

            Box(Modifier.constrainAs(camera) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
                QrCodeScannerCameraView (){ value, _ ->
                    Log.i("QrScanner", value ?: "nothing found")
                }
            }

            Box(modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp)
                )
                .constrainAs(actions) {
                    bottom.linkTo(parent.bottom, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.value(200.dp)
                }) {
                Column {
                    TextButton(
                        onClick = { /*TODO*/ },
                        Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Row() {
                            Icon(
                                painterResource(
                                    id = R.drawable.baseline_content_paste_24
                                ),
                                "Paste"
                            )
                            Text("Paste", Modifier.align(Alignment.CenterVertically))
                        }
                    }
                    Divider()
                    TextButton(
                        onClick = { /*TODO*/ },
                        Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Row {
                            Icon(
                                painterResource(
                                    id = R.drawable.baseline_keyboard_24
                                ),
                                "Manual"
                            )
                            Text("Manual", Modifier.align(Alignment.CenterVertically))
                        }
                    }
                }
            }
        }
    }
}

fun checkAndRequestCameraPermission(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        // Open camera because permission is already granted
    } else {
        // Request a permission
        launcher.launch(permission)
    }
}

@ExperimentalGetImage
@Composable
@Preview(showBackground = true)
fun PreviewScannerScreen() {
    ScannerScreen(navController = rememberNavController())
}