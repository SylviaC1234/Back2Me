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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.sylvia.back2me.data.LostItemViewModel
import com.sylvia.back2me.models.LostItem
import com.sylvia.back2me.navigation.ROUTE_ADD_ITEM
import com.sylvia.back2me.navigation.ROUTE_PROFILE
import com.sylvia.back2me.navigation.ROUTE_HOME

val newBlue = Color(0xFF1976D2)

@Composable
fun PostScreen(
    navController: NavController,
    itemViewModel: LostItemViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isPreview = androidx.compose.ui.platform.LocalInspectionMode.current

    val allItems = if (isPreview) {
        listOf(
            LostItem("1", "Black Backpack", "Left at station", "Nairobi", "user1", "Lost"),
            LostItem("2", "Keys", "Found near gate", "Mombasa", "user1", "Found")
        )
    } else {
        itemViewModel.items
    }

    val currentUserId = if (isPreview)
        "user1"
    else
        FirebaseAuth.getInstance().currentUser?.uid

    val userPosts = remember(allItems, currentUserId) {
        allItems.filter { it.userId == currentUserId }
    }

    // 🔥 THIS IS THE FIX: refresh whenever you return to screen
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && !isPreview) {
                itemViewModel.fetchItems(context)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    PostScreenContent(
        userPosts = userPosts,
        navController = navController,
        onDeleteClick = { item ->
            if (!isPreview) {
                item.id?.let {
                    itemViewModel.deleteItem(context, it)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreenContent(
    userPosts: List<LostItem>,
    navController: NavController,
    onDeleteClick: (LostItem) -> Unit
) {
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
                    onClick = { navController.navigate(ROUTE_HOME) { launchSingleTop = true } }
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("You haven't posted any items yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(userPosts, key = { it.id ?: it.hashCode() }) { item ->
                        PostItem(
                            item = item,
                            onDeleteClick = { onDeleteClick(item) }
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.title, fontWeight = FontWeight.Bold)

                    Surface(
                        color = if (item.type == "Lost")
                            Color(0xFFD32F2F)
                        else
                            Color(0xFF2E7D32),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = item.type,
                            modifier = Modifier.padding(6.dp),
                            color = Color.White
                        )
                    }
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }

            Text(item.description, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(6.dp))

            Text("📍 ${item.location}", color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostScreenPreview() {
    PostScreen(rememberNavController())
}