package com.sylvia.back2me.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.sylvia.back2me.navigation.ROUTE_ADD_ITEM
import com.sylvia.back2me.navigation.ROUTE_ITEM_DETAIL
import com.sylvia.back2me.navigation.ROUTE_VIEW_ITEM
import com.sylvia.back2me.navigation.ROUT_HOME
import com.sylvia.back2me.ui.theme.newBlue

// 🔹 Sample Data Class (add if you don't have it elsewhere)
data class Item(
    val title: String = "",
    val type: String = "Lost",
    val location: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController){

    Scaffold(

        //TopBar
        topBar = {
            TopAppBar(
                title = { Text("Back2Me") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = newBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions =  {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                    }

                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                    }
                }
            )
        },

        //BottomBar
        bottomBar = {
            NavigationBar(containerColor = newBlue) {

                var selectedIndex by remember { mutableStateOf(0) }

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) },
                    label = { Text("Home", color = Color.White) },
                    selected = selectedIndex == 0,
                    onClick = {
                        selectedIndex = 0
                        navController.navigate(ROUT_HOME)
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                    label = { Text("Search", color = Color.White) },
                    selected = selectedIndex == 1,
                    onClick = {

                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                    label = { Text("MyPosts", color = Color.White) },
                    selected = selectedIndex == 2,
                    onClick = {
                        selectedIndex = 2
                        navController.navigate(ROUTE_ITEM_DETAIL)
                    }
                )
            }
        },

        //FloatingActionButton
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = newBlue
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // 🔹 State
            var selectedFilter by remember { mutableStateOf("All") }
            var searchQuery by remember { mutableStateOf("") }

            val items = listOf(
                Item("Black Wallet", "Lost", "Library"),
                Item("iPhone 11", "Found", "Cafeteria"),
                Item("Keys", "Lost", "Bus Stop")
            )

            // 🔹 Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search items...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // 🔹 Filters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("All", "Lost", "Found").forEach { filter ->
                    Button(
                        onClick = { selectedFilter = filter },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (filter) {
                                "Lost" -> Color.Red
                                "Found" -> Color.Green
                                else -> if (selectedFilter == filter) newBlue else Color.LightGray
                            }
                        )
                    ) {
                        Text(filter, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 🔹 Filter Logic
            val filteredItems = items.filter {
                (selectedFilter == "All" || it.type == selectedFilter) &&
                        (it.title.contains(searchQuery, true) ||
                                it.location.contains(searchQuery, true))
            }

            // 🔹 Item List
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(filteredItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable { },
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {

                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                            Text(
                                item.type,
                                color = if (item.type == "Lost") Color.Red else Color.Green
                            )

                            Text(item.location)
                        }
                    }
                }
            }

            // Bottom Button (still kept)
            Button(
                onClick = { },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Item")
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen(rememberNavController())
}