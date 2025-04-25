package com.example.ai_ds_project

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AdminDashboardScreen()
            }
        }
    }
}

data class UserReturnData(
    val userId: String,
    val productId: String,
    val images: List<Bitmap>
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen() {
    var userReturns by remember { mutableStateOf<List<UserReturnData>>(emptyList()) }
    val context = LocalContext.current

    // Fetch all return data from Firebase
    LaunchedEffect(Unit) {
        val database = Firebase.database.reference.child("user_returns")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val returnsList = mutableListOf<UserReturnData>()

                snapshot.children.forEach { userSnapshot ->
                    val userId = userSnapshot.key ?: return@forEach

                    userSnapshot.children.forEach { productSnapshot ->
                        val productId = productSnapshot.key ?: return@forEach
                        val images = mutableListOf<Bitmap>()

                        productSnapshot.children.forEach { imageSnapshot ->
                            val base64String = imageSnapshot.getValue(String::class.java) ?: return@forEach
                            try {
                                val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                images.add(bitmap)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        if (images.isNotEmpty()) {
                            returnsList.add(UserReturnData(userId, productId, images))
                        }
                    }
                }

                userReturns = returnsList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2874F0),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(userReturns) { userReturn ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "User: ${userReturn.userId}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Product: ${userReturn.productId}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(userReturn.images) { bitmap ->
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                // Call your analysis function here
                                analyzeImages(userReturn.images, context)
                            },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFB641B)
                            )
                        ) {
                            Text("Analyze")
                        }
                    }
                }
            }
        }
    }
}

// Function to analyze the images
// Function to analyze the images
// Function to analyze the images
fun analyzeImages(images: List<Bitmap>, context: android.content.Context) {
    // Loop through each image and classify it using the classifyImage method
    images.forEach { bitmap ->
        // Classify each image (Real vs Fake)
        val result = classifyImage(context, bitmap)

        // Show the result of classification in a Toast
        android.widget.Toast.makeText(
            context,
            "Analysis Result: $result",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }
}





@Preview
@Composable
fun AdminDashboardPreview() {
    MaterialTheme {
        AdminDashboardScreen()
    }
}

