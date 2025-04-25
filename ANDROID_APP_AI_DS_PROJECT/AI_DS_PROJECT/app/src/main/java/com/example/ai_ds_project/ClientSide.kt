package com.example.ai_ds_project

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import coil.compose.rememberImagePainter

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import java.io.InputStream

import com.google.firebase.FirebaseApp

class ProductReturnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ProductReturnScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductReturnScreen() {
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var returnReason by remember { mutableStateOf("") }
    var showCameraDialog by remember { mutableStateOf(false) }
    var userId by remember { mutableStateOf("") }
    var productId by remember { mutableStateOf("") }

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { selectedImages = selectedImages + it } }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Return Product") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2874F0),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val contentResolver = context.contentResolver
                    val base64Images = mutableListOf<String>()

                    try {
                        selectedImages.forEach { uri ->
                            val inputStream: InputStream? = contentResolver.openInputStream(uri)
                            val bytes = inputStream?.readBytes()
                            inputStream?.close()
                            if (bytes != null) {
                                val encoded = Base64.encodeToString(bytes, Base64.DEFAULT)
                                base64Images.add(encoded)
                            }
                        }

                        val databaseRef = FirebaseDatabase.getInstance().reference
                            .child("user_returns")
                            .child(userId)
                            .child(productId)

                        databaseRef.setValue(base64Images)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Return submitted successfully!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Submission failed: ${e.message}", Toast.LENGTH_LONG).show()
                                Log.d("error while upload","${e.message}")
                            }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                },
                enabled = selectedImages.size >= 2 && returnReason.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFB641B),
                    disabledContainerColor = Color(0xFFCCCCCC)
                )
            ) {
                Text("Submit Return Request", fontSize = 16.sp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Return Instructions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "1. Take clear photos of the damaged/faulty product\n" +
                                "2. Show product serial number if available\n" +
                                "3. Capture all angles of the damage\n" +
                                "4. Minimum 2 photos required",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("USER_ID") },
                placeholder = { Text("Enter your user id") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = productId,
                onValueChange = { productId = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("PRODUCT_ID") },
                placeholder = { Text("Enter your product id") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = returnReason,
                onValueChange = { returnReason = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Reason for Return") },
                placeholder = { Text("E.g., Product damaged, Wrong item received") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Upload Images of Faulty Product (${selectedImages.size}/5)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(selectedImages) { uri ->
                    Box(modifier = Modifier.size(120.dp)) {
                        Image(
                            painter = rememberImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { selectedImages = selectedImages - uri },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White)
                        }
                    }
                }

                item {
                    if (selectedImages.size < 5) {
                        AddImageButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCameraDialog) {
        AlertDialog(
            onDismissRequest = { showCameraDialog = false },
            title = { Text("Camera Access") },
            text = { Text("Please allow camera permission to take photos of the faulty product") },
            confirmButton = {
                Button(onClick = { showCameraDialog = false }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCameraDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AddImageButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick)
            .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF0F0F0)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add image",
                tint = Color(0xFF666666),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add Photo",
                color = Color(0xFF666666),
                fontSize = 12.sp
            )
        }
    }
}
