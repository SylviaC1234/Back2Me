package com.sylvia.back2me.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.sylvia.back2me.data.LostItemViewModel
import com.sylvia.back2me.models.LostItem
import com.sylvia.back2me.navigation.*
import com.sylvia.back2me.ui.theme.newBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    // We pass the ViewModel as a parameter so it can be shared
    itemViewModel: LostItemViewModel = viewModel()
) {
    val context = LocalContext.current
    val items = itemViewModel.items

    // Fetch items when screen loads
    LaunchedEffect(Unit) {
        itemViewModel.fetchItems(context)
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val focusRequester = remember { FocusRequester() }

    val filteredItems = items.filter {
        (selectedFilter == "All" || it.type == selectedFilter) &&
                (it.title.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Back2Me", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(ROUTE_LOGIN) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = newBlue)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = newBlue) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null, tint = Color.White) },
                    label = { Text("Home", color = Color.White) },
                    selected = true,
                    onClick = { /* Stay here */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                    label = { Text("Search", color = Color.White) },
                    selected = false,
                    onClick = { focusRequester.requestFocus() }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, null, tint = Color.White) },
                    label = { Text("MyPosts", color = Color.White) },
                    selected = false,
                    onClick = { navController.navigate(ROUTE_POST) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ROUTE_ADD_ITEM) },
                containerColor = newBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search items...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp).focusRequester(focusRequester),
                shape = RoundedCornerShape(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("All", "Lost", "Found").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = newBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items posted yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems) { item ->
                        ItemCard(
                            item = item,
                            onClick = {
                                item.id?.let { itemId ->
                                    // Ensure the route matches your NavHost definition
                                    navController.navigate("$ROUTE_ITEM_DETAILS/$itemId")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(item: LostItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = "Item image",
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(android.R.drawable.ic_menu_gallery),
                error = painterResource(android.R.drawable.ic_menu_report_image)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = item.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.type == "Lost") Color.Red else Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold
                )
                Text(text = "📍 ${item.location}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Preview works because of the default value in the parameter
        HomeScreen(navController = navController)
    }
}