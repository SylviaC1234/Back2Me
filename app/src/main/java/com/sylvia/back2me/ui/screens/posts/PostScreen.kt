package com.sylvia.back2me.ui.screens.posts

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sylvia.back2me.navigation.ROUT_HOME

// 🔥 SIMPLE MODEL (keep for now if you don't already have one)
data class Post(
    val id: String,
    val title: String,
    val description: String
)

val newBlue = Color(0xFF1976D2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController: NavController) {

    var selectedIndex by remember { mutableIntStateOf(0) }

    val posts = listOf(
        Post("1", "Lost Phone", "Black Samsung lost near school"),
        Post("2", "Found Wallet", "Brown wallet found in town"),
        Post("3", "Missing Keys", "Car keys with red keychain")
    )

    Scaffold(

        // 🔵 TOP BAR
        topBar = {
            TopAppBar(
                title = { Text("My Posts") },
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

        // 🔵 BOTTOM BAR
        bottomBar = {
            NavigationBar(containerColor = newBlue) {

                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.White
                        )
                    },
                    label = { Text("Home", color = Color.White) },
                    selected = selectedIndex == 0,
                    onClick = {
                        selectedIndex = 0
                        navController.navigate(ROUT_HOME) {
                            popUpTo(ROUT_HOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.List,
                            contentDescription = "Posts",
                            tint = Color.White
                        )
                    },
                    label = { Text("Posts", color = Color.White) },
                    selected = selectedIndex == 1,
                    onClick = {
                        selectedIndex = 1
                        navController.navigate(ROUT_HOME) {
                            popUpTo(ROUT_HOME)
                            launchSingleTop = true
                        }
                    }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    },
                    label = { Text("Profile", color = Color.White) },
                    selected = selectedIndex == 2,
                    onClick = {
                        selectedIndex = 2
                        navController.navigate(ROUT_HOME) {
                            popUpTo(ROUT_HOME)
                            launchSingleTop = true
                        }
                    }
                )
            }
        },

        // 🔵 FLOATING BUTTON
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = newBlue
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },

        // 🔵 CONTENT
        content = { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Text(
                    text = "My Posts",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (posts.isEmpty()) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No posts yet")
                    }

                } else {

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(posts) { post ->
                            PostItem(post)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun PostItem(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostScreenPreview() {
    PostScreen(rememberNavController())
}