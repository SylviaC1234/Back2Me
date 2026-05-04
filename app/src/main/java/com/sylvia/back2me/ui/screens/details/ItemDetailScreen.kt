package com.sylvia.back2me.ui.screens.details

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(navController: NavController, itemId: String?) {

    val context = LocalContext.current

    // 🔥 dialog state
    var showContactDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {

            // 🔹 IMAGE
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "Item Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 TITLE + STATUS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Black Backpack",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Lost",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "📍 Nairobi CBD")
            Text(text = "📅 April 28, 2026")

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 DESCRIPTION
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Black backpack with a laptop inside. Lost near the school gate during break time."
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 CONTACT BUTTON
            Button(
                onClick = { showContactDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Contact Owner")
            }
        }
    }

    // 🔥 CONTACT DIALOG
    if (showContactDialog) {

        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text("Contact Owner") },
            text = { Text("Choose how you want to contact the owner") },

            confirmButton = {
                TextButton(
                    onClick = {
                        showContactDialog = false

                        val callIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = "tel:0717085866".toUri()
                        }
                        context.startActivity(callIntent)
                    }
                ) {
                    Text("Call")
                }
            },

            dismissButton = {
                Row {

                    TextButton(
                        onClick = {
                            showContactDialog = false

                            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = "smsto:0717085866".toUri()
                            }
                            context.startActivity(smsIntent)
                        }
                    ) {
                        Text("Message")
                    }

                    TextButton(
                        onClick = {
                            showContactDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailScreenPreview() {
    ItemDetailScreen(
        navController = rememberNavController(),
        itemId = "sample_id"
    )
}