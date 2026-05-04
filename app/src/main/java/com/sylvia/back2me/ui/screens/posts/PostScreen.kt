package com.sylvia.back2me.ui.screens.posts

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.sylvia.back2me.data.LostItemViewModel
import com.sylvia.back2me.models.LostItem
import com.sylvia.back2me.navigation.ROUTE_ADD_ITEM
import com.sylvia.back2me.navigation.ROUTE_PROFILE
import com.sylvia.back2me.navigation.ROUT_HOME

val newBlue = Color(0xFF1976D2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController: NavController) {
    val context = LocalContext.current
    val itemViewModel: LostItemViewModel = viewModel()
    val allItems = itemViewModel.items
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Filter items to only show those belonging to the current user
    val userPosts = remember(allItems) {
        allItems.filter { it.userId == currentUserId }
    }

    LaunchedEffect(Unit) {
        itemViewModel.fetchItems(context)
    }

    var selectedIndex by remember { mutableIntStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Posts", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = newBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
            )
        },
        bottomBar = {
            NavigationBar(containerColor = newBlue) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null, tint = Color.White) },
                    label = { Text("Home", color = Color.White) },
                    selected = selectedIndex == 0,
                    onClick = { navController.navigate(ROUT_HOME) { launchSingleTop = true } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, null, tint = Color.White) },
                    label = { Text("Posts", color = Color.White) },
                    selected = selectedIndex == 1,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null, tint = Color.White) },
                    label = { Text("Profile", color = Color.White) },
                    selected = selectedIndex == 2,
                    onClick = { navController.navigate(ROUTE_PROFILE) { launchSingleTop = true } }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ROUTE_ADD_ITEM) },
                containerColor = newBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "My Activity",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (userPosts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You haven't posted any items yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(userPosts) { item ->
                        PostItem(
                            item = item,
                            onDeleteClick = {
                                // Call the delete function in your ViewModel
                                itemViewModel.deleteItem(context, item.id!!)
                                Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(item: LostItem, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = if (item.type == "Lost") Color(0xFFD32F2F) else Color(0xFF2E7D32),
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = item.type,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // 🗑️ DELETE BUTTON
                IconButton(onClick = { onDeleteClick() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Post",
                        tint = Color(0xFFD32F2F) // Red color for delete
                    )
                }
            }

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📍 ${item.location}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostScreenPreview() {
    PostScreen(rememberNavController())
}