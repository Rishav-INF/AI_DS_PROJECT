package com.example.ai_ds_project

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppIntroductionScreen()
            }
        }
    }
}

@Composable
fun AppIntroductionScreen() {
    var selectedOption by remember { mutableStateOf<AppOption?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Title
        Text(
            text = "Product Return System",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2874F0),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // App Description
        Text(
            text = "This app helps customers submit product return requests with images, and allows admin to review and analyze the returns.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Options Title
        Text(
            text = "Select your role:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Radio Options
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppOption.entries.forEach { option ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = (option == selectedOption),
                            onClick = { selectedOption = option }
                        )
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == selectedOption),
                        onClick = { selectedOption = option }
                    )
                    Text(
                        text = when (option) {
                            AppOption.USER -> "I want to return a product"
                            AppOption.ADMIN -> "I'm reviewing returns"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Open Button
        Button(
            onClick = {
                when (selectedOption) {
                    AppOption.USER -> {
                        context.startActivity(Intent(context, ProductReturnActivity::class.java))
                    }
                    AppOption.ADMIN -> {
                        context.startActivity(Intent(context, AdminDashboardActivity::class.java))
                    }
                    null -> {
                        // No option selected
                    }
                }
            },
            enabled = selectedOption != null,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFB641B)
            )
        ) {
            Text("Open", fontSize = 18.sp)
        }
    }
}

enum class AppOption {
    USER, ADMIN
}