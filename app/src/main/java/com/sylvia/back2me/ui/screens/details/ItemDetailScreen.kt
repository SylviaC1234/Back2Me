package com.sylvia.back2me.ui.screens.details

import android.content.Intent
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.sylvia.back2me.data.LostItemViewModel
import com.sylvia.back2me.ui.theme.newBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    navController: NavController,
    itemId: String?,
    // Shared ViewModel passed as a parameter
    itemViewModel: LostItemViewModel = viewModel()
) {
    val context = LocalContext.current

    // Find the specific item in the shared list
    val item = itemViewModel.items.find { it.id == itemId }

    var showContactDialog by remember { mutableStateOf(false) }

    // If the list is empty (e.g. on a fresh app restart), fetch data
    LaunchedEffect(Unit) {
        if (itemViewModel.items.isEmpty()) {
            itemViewModel.fetchItems(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = newBlue)
            )
        }
    ) { padding ->
        item?.let { currentItem ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = currentItem.imageUrl,
                    contentDescription = "Item Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                    error = painterResource(android.R.drawable.ic_menu_report_image)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentItem.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color = if (currentItem.type == "Lost") Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = currentItem.type.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (currentItem.type == "Lost") Color.Red else Color(0xFF2E7D32),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_mylocation),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = currentItem.location, color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Text(text = "Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = currentItem.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { showContactDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = newBlue)
                ) {
                    Text("Contact Owner", style = MaterialTheme.typography.titleMedium)
                }
            }

            if (showContactDialog) {
                AlertDialog(
                    onDismissRequest = { showContactDialog = false },
                    title = { Text("Contact ${currentItem.title}") },
                    text = { Text("The owner listed their contact as: ${currentItem.contact}") },
                    confirmButton = {
                        Button(onClick = {
                            showContactDialog = false
                            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                data = "tel:${currentItem.contact}".toUri()
                            }
                            context.startActivity(callIntent)
                        }) { Text("Call Now") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showContactDialog = false
                            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = "smsto:${currentItem.contact}".toUri()
                            }
                            context.startActivity(smsIntent)
                        }) { Text("Send Message") }
                    }
                )
            }

        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = newBlue)
            Text("Locating Item...", modifier = Modifier.padding(top = 80.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ItemDetailScreenPreview() {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Preview with a dummy ID
        ItemDetailScreen(
            navController = navController,
            itemId = "sample_id"
        )
    }
}