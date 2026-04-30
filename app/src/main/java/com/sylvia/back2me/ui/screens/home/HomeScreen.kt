package com.sylvia.back2me.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.sylvia.back2me.data.AuthViewModel
import com.sylvia.back2me.navigation.*
import com.sylvia.back2me.ui.theme.newBlue

data class Item(
    val title: String = "",
    val type: String = "Lost",
    val location: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val authViewModel = if (!isPreview) {
        AuthViewModel(navController, context)
    } else null

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val focusRequester = remember { FocusRequester() }

    // 🔥 Sample fallback (preview only)
    val sampleItems = listOf(
        Item("Black Wallet", "Lost", "Library"),
        Item("iPhone 11", "Found", "Cafeteria"),
        Item("Keys", "Lost", "Bus Stop")
    )

    val backendResults = authViewModel?.searchResults ?: emptyList()

    Scaffold(

        // 🔹 TOP BAR
        topBar = {
            TopAppBar(
                title = { Text("Back2Me") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isPreview) navController.navigate(ROUTE_LOGIN)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = newBlue,
                    titleContentColor = Color.White
                )
            )
        },

        // 🔹 BOTTOM BAR
        bottomBar = {
            NavigationBar(containerColor = newBlue) {

                var selectedIndex by remember { mutableStateOf(0) }

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null, tint = Color.White) },
                    label = { Text("Home", color = Color.White) },
                    selected = selectedIndex == 0,
                    onClick = {
                        selectedIndex = 0
                        if (!isPreview) navController.navigate(ROUT_HOME)
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                    label = { Text("Search", color = Color.White) },
                    selected = selectedIndex == 1,
                    onClick = {
                        selectedIndex = 1
                        focusRequester.requestFocus() // 🔥 focus search bar
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null, tint = Color.White) },
                    label = { Text("MyPosts", color = Color.White) },
                    selected = selectedIndex == 2,
                    onClick = {
                        selectedIndex = 2
                        if (!isPreview) navController.navigate(ROUTE_POST)
                    }
                )
            }
        },

        // 🔹 FAB
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ROUTE_ADD_ITEM) },
                containerColor = newBlue
            ) {
                Icon(Icons.Default.Add, null)
            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // 🔹 SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    authViewModel?.searchItems(it)
                },
                placeholder = { Text("Search items...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .focusRequester(focusRequester)
            )

            // 🔥 SEARCH MODE
            if (searchQuery.isNotEmpty()) {

                val resultsToShow =
                    if (isPreview) sampleItems
                    else backendResults.map {
                        Item(it, "Result", "Unknown") // backend fallback mapping
                    }

                if (resultsToShow.isEmpty()) {
                    Text(
                        "Item not found",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {

                    LazyColumn {
                        items(resultsToShow) { item ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {

                                    Text(
                                        item.title,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        item.type,
                                        color = if (item.type == "Lost") Color.Red else Color.Green
                                    )

                                    Text(item.location)
                                }
                            }
                        }
                    }
                }

            } else {

                // 🔹 FILTER BUTTONS
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

                // 🔹 FILTERED LIST
                val filteredItems = sampleItems.filter {
                    selectedFilter == "All" || it.type == selectedFilter
                }

                LazyColumn(modifier = Modifier.weight(1f)) {

                    items(filteredItems) { item ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {

                            Column(modifier = Modifier.padding(12.dp)) {

                                Text(item.title, fontWeight = FontWeight.Bold)

                                Text(
                                    item.type,
                                    color = if (item.type == "Lost") Color.Red else Color.Green
                                )

                                Text(item.location)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen(rememberNavController())
}